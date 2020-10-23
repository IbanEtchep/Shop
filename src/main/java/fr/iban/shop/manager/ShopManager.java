package fr.iban.shop.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;

import fr.iban.shop.Shop;

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
						shopsConfig.getInt(path+"stock")
						));
			}	
		}
		shop.getLogger().log(Level.INFO, "Chargement des shops terminé. ("+shopItems.size()+" articles chargés)");

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
		shopsConfig.set(itemPath+"stock", item.getMaxStock());
	}

	public Map<String, Map<Integer, ShopItem>> getShopItems() {
		return shopItems;
	}
}
