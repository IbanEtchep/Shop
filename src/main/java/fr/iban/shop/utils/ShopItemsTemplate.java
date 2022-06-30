package fr.iban.shop.utils;

import org.bukkit.Material;

import fr.iban.shop.Shop;
import fr.iban.shop.ShopItem;
import fr.iban.shop.manager.ShopManager;

public class ShopItemsTemplate {
	
	public static void addItems() {
		/*
		 * Plantations :
		 */
		addItem(1, 1, Material.CARROT, "plantes", 10000);
		addItem(2, 1, Material.POTATO, "plantes", 10000);
		addItem(3, 1, Material.WHEAT, "plantes", 10000);
		addItem(4, 1, Material.BEETROOT, "plantes", 10000);
		addItem(5, 1, Material.SUGAR_CANE, "plantes", 10000);
		addItem(6, 1, Material.MELON, "plantes", 10000);
		addItem(7, 1, Material.PUMPKIN, "plantes", 10000);
		addItem(8, 1, Material.CACTUS, "plantes", 10000);
		addItem(9, 1, Material.BAMBOO, "plantes", 10000);
		addItem(10, 1, Material.APPLE, "plantes", 10000);


//      shopManager.saveShop(new ShopItem(1, 10.3, 0.4, new ItemStack(Material.DIAMOND), "minerais"));
//      shopManager.saveShop(new ShopItem(2, 10.4, 0.4, new ItemStack(Material.IRON_INGOT), "minerais"));
//      shopManager.saveShop(new ShopItem(3, 10.3, 0.4, new ItemStack(Material.REDSTONE), "minerais"));
//      shopManager.saveShop(new ShopItem(4, 100.3, 0.4, new ItemStack(Material.COAL), "minerais"));
	}
	
	private static void addItem(int id, double buy, Material material, String category, int maxstock) {
		ShopManager sm = Shop.getInstance().getShopManager();
		sm.saveShop(new ShopItem(id, buy, material, category, maxstock));
	}

}
