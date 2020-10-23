package fr.iban.shop.manager;

import org.bukkit.inventory.ItemStack;

public class ShopItem {
	
	private int id;
	private double buy;
	private double sell;
	private ItemStack item;
	private String category;
	private int maxStock = 1000;
	
	public ShopItem(int id, double buy, double sell, ItemStack item, String category) {
		this.id = id;
		this.setBuy(buy);
		this.setSell(sell);
		this.item = item;
		this.category = category;
	}
	
	public ShopItem(int id, double buy, double sell, ItemStack item, String category, int maxStock) {
		this.id = id;
		this.setBuy(buy);
		this.setSell(sell);
		this.item = item;
		this.category = category;
		this.maxStock = maxStock;
	}

	public ItemStack getItem() {
		return item;
	}
	public void setItem(ItemStack item) {
		this.item = item;
	}

	public int getId() {
		return id;
	}

	public String getCategory() {
		return category;
	}

	public int getMaxStock() {
		return maxStock;
	}

	public double getSell() {
		return sell;
	}

	public void setSell(double sell) {
		this.sell = sell;
	}

	public double getBuy() {
		return buy;
	}

	public void setBuy(double buy) {
		this.buy = buy;
	}

	
}
