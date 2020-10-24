package fr.iban.shop.manager;

import org.bukkit.inventory.ItemStack;

import fr.iban.shop.utils.ShopAction;

public class ShopItem {
	
	private int id;
	private double buy;
	private double sell;
	private ItemStack item;
	private String category;
	private int maxStock = 5000;
	private int stock = maxStock;
	
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

	public ShopItem(int id, double buy, double sell, ItemStack item, String category, int maxStock, int stock) {
		super();
		this.id = id;
		this.buy = buy;
		this.sell = sell;
		this.item = item;
		this.category = category;
		this.maxStock = maxStock;
		this.setStock(stock);
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

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}
	
	public double getModifier(int currentstock) {
		return (double)maxStock/currentstock;
	}
	
	public double calculatePrice(int amount, ShopAction action) {
		double finalAmount = 0;
		int vstock = stock;
		if(action == ShopAction.BUY) {
			for (int i = 0; i < amount; i++) {
				finalAmount += buy * getModifier(stock);
				vstock--;
			}
		}else if(action == ShopAction.SELL) {
			for (int i = 0; i < amount; i++) {
				finalAmount += sell * getModifier(stock);
				vstock++;
			}
		}
		return (double)Math.round(finalAmount * 100)/100;
	}
	
}
