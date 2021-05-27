package fr.iban.shop.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import fr.iban.shop.Shop;
import fr.iban.shop.events.ShopReloadEvent;

public class ShopManager {

	private Shop shop;

	private FileConfiguration shopsConfig;


	//         CATEGORY     ID       ITEM        
	private Map<String, Map<Integer, ShopItem>> shopItems = new HashMap<>();

	public ShopManager(Shop shop) {
		this.shop = shop;
		shopsConfig = shop.getShopsConfig();
	}

	/*
	 * Load tous les shops en cache.
	 */
	public void loadShops() {
		shop.getLogger().log(Level.INFO, "Chargement des shops...");
		for(String category : shopsConfig.getConfigurationSection("shops").getKeys(false)) {
			shop.getLogger().log(Level.INFO, "Chargement de la catégorie : " + category);
			shopItems.put(category, new HashMap<>());
			String catPath = "shops."+category;
			for(String id :shopsConfig.getConfigurationSection(catPath).getKeys(false)) {
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
		shop.getLogger().log(Level.INFO, "Chargement des shops terminé.");
	}

	/*
	 * Sauvegarder un article dans shops.yml
	 */
	public void saveShop(ShopItem item) {
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

	public void saveShops() {
		for(Map<Integer, ShopItem> category : getShopItems().values()) {
			for(ShopItem item : category.values()) {
				saveShop(item);
			}
		}
		try {
			shopsConfig.save(Shop.getInstance().getShopsFile());
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
}
