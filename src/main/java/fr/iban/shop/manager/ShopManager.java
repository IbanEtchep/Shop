package fr.iban.shop.manager;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

import fr.iban.shop.ShopItem;
import fr.iban.shop.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;

import fr.iban.shop.ShopPlugin;
import fr.iban.shop.events.ShopReloadEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ShopManager {

	private final ShopPlugin plugin;

	private final FileConfiguration shopsConfig;
	public final NamespacedKey sellWandKey;
	//         CATEGORY     ID       ITEM        
	private final Map<String, Map<Integer, ShopItem>> shopItems = new HashMap<>();

	public ShopManager(ShopPlugin plugin) {
		this.plugin = plugin;
		shopsConfig = plugin.getShopsConfig();
		sellWandKey = new NamespacedKey(plugin, "sell_wand");
	}

	/*
	 * Load tous les shops en cache.
	 */
	public void loadShops() {
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
	}

	/*
	 * Sauvegarder un article dans shops.yml
	 */
	public void saveShop(ShopItem item) {
		if(item.getItem() == null) return;
		String catPath = "shops."+item.getCategory()+".";
		String itemPath = catPath+item.getId()+".";
		shopsConfig.set(itemPath+"buy", item.getBuy());
		shopsConfig.set(itemPath+"sell", item.getSell());
		shopsConfig.set(itemPath+"item", item.getItem());
		shopsConfig.set(itemPath+"maxstock", item.getMaxStock());
		shopsConfig.set(itemPath+"stock", item.getStock());
	}

	public void reloadShops() {
		saveShops();
		loadShops();
		Bukkit.getPluginManager().callEvent(new ShopReloadEvent());
	}

	public void reloadConfig() {
		plugin.reloadConfig();
		loadShops();
		Bukkit.getPluginManager().callEvent(new ShopReloadEvent());
	}

	public void saveShops() {
		for(Map<Integer, ShopItem> category : getShopItems().values()) {
			for(ShopItem item : category.values()) {
				saveShop(item);
			}
		}
		try {
			shopsConfig.save(ShopPlugin.getInstance().getShopsFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Map<String, Map<Integer, ShopItem>> getShopItems() {
		return shopItems;
	}

	public int getNewID(String category) {
		int id = 0;
		for(ShopItem item : shopItems.get(category).values()) {
			if(item.getId() > id) {
				id = item.getId();
			}
		}
		return id + 1;
	}

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
}
