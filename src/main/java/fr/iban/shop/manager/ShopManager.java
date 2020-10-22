package fr.iban.shop.manager;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;

import fr.iban.shop.Shop;

public class ShopManager {

	private Shop shop;
	
	private FileConfiguration shopsConfig;
	
	private Map<Integer, ShopItem> shopItems = new HashMap<Integer, ShopItem>();


	public ShopManager(Shop shop) {
		this.shop = shop;
		shopsConfig = shop.getShopsConfig();
	}

	/*
	 * Load tous les shops en cache.
	 */
	public void loadShops() {
		for(String id :shopsConfig.getConfigurationSection("shops").getKeys(false)) {
			String path = "shops."+id+".";
			shopItems.put(Integer.parseInt(id), new ShopItem(
					Integer.parseInt(id),
					shopsConfig.getDouble(path+"price"),
					shopsConfig.getItemStack(path+"item"),
					shopsConfig.getString(path+"category"),
					shopsConfig.getInt(path+"stock")
					));
		}
	}
	
	/*
	 * Sauvegarder un article dans shops.yml
	 */
	public void saveShop(ShopItem item) {
		String path = "shops."+item.getId()+".";
		shopsConfig.set(path+"price", item.getPrice());
		shopsConfig.set(path+"category", item.getCategory());
		shopsConfig.set(path+"item", item.getItem());
		shopsConfig.set(path+"stock", item.getMaxStock());
	}
	

	public Map<Integer, ShopItem> getShopItems() {
		return shopItems;
	}
}
