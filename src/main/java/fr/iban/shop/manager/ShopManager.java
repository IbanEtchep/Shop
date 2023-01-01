package fr.iban.shop.manager;

import fr.iban.bukkitcore.CoreBukkitPlugin;
import fr.iban.shop.ShopItem;
import fr.iban.shop.ShopPlugin;
import fr.iban.shop.events.ShopReloadEvent;
import fr.iban.shop.storage.ShopDbAccess;
import fr.iban.shop.utils.ItemBuilder;
import fr.iban.shop.utils.ShopAction;
import fr.iban.shop.utils.StockSyncMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class ShopManager {

	private final ShopPlugin plugin;

	private final FileConfiguration shopsConfig;
	private final ShopDbAccess dbAccess;
	public final NamespacedKey sellWandKey;
	//         CATEGORY     ID       ITEM        
	private final List<ShopItem> shopItems = new ArrayList<>();
	private List<String> categories = new ArrayList<>();

	public ShopManager(ShopPlugin plugin) {
		this.plugin = plugin;
		shopsConfig = plugin.getShopsConfig();
		this.dbAccess = new ShopDbAccess(plugin);
		sellWandKey = new NamespacedKey(plugin, "sell_wand");
		loadShopsFromDB();
	}

	/*
	 * Load tous les shops en cache.
	 */
	private Map<String, Map<Integer, ShopItem>> loadShops() {
		Map<String, Map<Integer, ShopItem>> shopItems = new HashMap<>();
		plugin.getLogger().log(Level.INFO, "Chargement des shops...");
		for(String category : Objects.requireNonNull(shopsConfig.getConfigurationSection("shops")).getKeys(false)) {
			plugin.getLogger().log(Level.INFO, "Chargement de la catégorie : " + category);
			shopItems.put(category, new HashMap<>());
			String catPath = "shops."+category;
			for(String id : Objects.requireNonNull(shopsConfig.getConfigurationSection(catPath)).getKeys(false)) {
				String path = catPath+"."+id+".";
				shopItems.get(category).put(Integer.parseInt(id), new ShopItem(
						Integer.parseInt(id),
						shopsConfig.getDouble(path+"buy"),
						shopsConfig.getDouble(path+"sell"),
						shopsConfig.getItemStack(path+"item"),
						category,
						shopsConfig.getInt(path+"maxstock"),
						shopsConfig.getInt(path+"stock")
						));
			}	
		}
		plugin.getLogger().log(Level.INFO, "Chargement des shops terminé.");
		return shopItems;
	}

	public void reloadShops() {
		loadShopsFromDB().thenRun(() ->
				Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(new ShopReloadEvent()))
		);
	}

	public List<ShopItem> getShopItems() {
		return shopItems;
	}

	public List<ShopItem> getShopItems(String category) {
		return shopItems.stream().filter(item -> item.getCategory().equals(category)).toList();
	}

	public List<String> getCategories() {
		return categories;
	}

	public ShopItem getShopItemByID(int id) {
		return shopItems.stream().filter(item -> item.getId() == id).findFirst().orElse(null);
	}
	/*
	DATABASE ACCESS
	 */

	public CompletableFuture<Void> loadShopsFromDB() {
		return CompletableFuture.runAsync(() -> {
			shopItems.clear();
			categories.clear();
			categories = dbAccess.getCategories();
			plugin.getLogger().log(Level.INFO, "Chargement des shops...");
			shopItems.addAll(dbAccess.getItems());
			plugin.getLogger().log(Level.INFO, shopItems.size() + " shops chargés.");
		});
	}

	public void addShopItem(ShopItem item) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			dbAccess.addItem(item);
			reloadShops();
		});
	}

	public void saveShopItem(ShopItem item) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> dbAccess.updateItem(item));
	}

	public void saveStock(ShopItem item) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> dbAccess.saveStock(item));
		StockSyncMessage message = new StockSyncMessage(item.getId(), item.getStock());
		CoreBukkitPlugin.getInstance().getMessagingManager().sendMessage(ShopPlugin.STOCK_SYNC_CHANNEL, message);
	}

	public void deleteShopItem(ShopItem item) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			dbAccess.deleteItem(item.getId());
			reloadShops();
		});
	}

	public void addCategory(String name) {
		if(categories.contains(name)) return;
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> dbAccess.addCategory(name));
		categories.add(name);
	}

	public void addTransactionLog(ShopItem item, UUID uuid, int amount, double price, ShopAction action) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> dbAccess.addTransactionLog(item, uuid, amount, price, action));
	}

	public void migrate() {
		Map<String, Map<Integer, ShopItem>> shopItems = loadShops();
		shopItems.keySet().forEach(dbAccess::addCategory);
		plugin.getLogger().log(Level.INFO, "Catégories migrées.");
		for (Map<Integer, ShopItem> value : shopItems.values()) {
			for (ShopItem shopItem : value.values()) {
				dbAccess.addItem(shopItem);
			}
		}
		plugin.getLogger().log(Level.INFO, "Items migrés.");
	}
	/*
	SELL WAND
	 */

	public ItemStack getSellWand(int durability) {
		ItemStack wand = new ItemBuilder(Material.STICK)
				.setName("§2Bâton de vente")
				.addLore("§7Permet de vendre le contenu du coffre que vous cliquez.")
				.addLore("§aUtilisations restantes: §f" + durability)
				.setGlow(true)
				.build();
		ItemMeta meta = wand.getItemMeta();
		meta.getPersistentDataContainer().set(sellWandKey, PersistentDataType.INTEGER, durability);
		wand.setItemMeta(meta);
		return wand;
	}

	public boolean isSellWand(ItemStack itemStack) {
		return itemStack.getItemMeta() != null && itemStack.getItemMeta().getPersistentDataContainer().has(sellWandKey, PersistentDataType.INTEGER);
	}

	public void consumeSellWand(ItemStack itemStack, Player player) {
		if(!isSellWand(itemStack)) {
			return;
		}

		try {
			int currentDurability = itemStack.getItemMeta().getPersistentDataContainer().get(sellWandKey, PersistentDataType.INTEGER);
			int newDurability = currentDurability-1;
			if(newDurability > 0) {
				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.getPersistentDataContainer().set(sellWandKey, PersistentDataType.INTEGER, newDurability);
				itemMeta.setLore(Arrays.asList("§7Permet de vendre le contenu du coffre que vous cliquez.",
						"§aUtilisations restantes: §f" + (currentDurability-1)));
				itemStack.setItemMeta(itemMeta);
			}else{
				player.getInventory().removeItem(itemStack);
				player.sendMessage("§cVotre bâton de vente s'est cassé.");
			}

		}catch (NullPointerException ignore) {}
	}

	public List<ShopItem> getItemsToSell(Inventory inventory) {
		List<ShopItem> shopItems = new ArrayList<>();
		for (ItemStack itemStack : inventory.getStorageContents()) {
			for (ShopItem shopItem : getShopItems()) {
				if (shopItem.getItemStack().isSimilar(itemStack) && !shopItems.contains(shopItem)) {
					shopItems.add(shopItem);
				}
			}
		}
		return shopItems;
	}

	public ShopDbAccess getDbAccess() {
		return dbAccess;
	}
}
