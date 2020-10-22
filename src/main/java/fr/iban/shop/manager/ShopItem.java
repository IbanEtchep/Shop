package fr.iban.shop.manager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class ShopItem implements ConfigurationSerializable {
	
	private int id;
	private double price;
	private ItemStack item;
	private String category;
	private int maxStock = 1000;
	
	public ShopItem(int id, double price, ItemStack item, String category) {
		this.id = id;
		this.price = price;
		this.item = item;
		this.category = category;
	}
	
	public ShopItem(int id, double price, ItemStack item, String category, int maxStock) {
		this.id = id;
		this.price = price;
		this.item = item;
		this.category = category;
		this.maxStock = maxStock;
	}

	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public ItemStack getItem() {
		return item;
	}
	public void setItem(ItemStack item) {
		this.item = item;
	}

	@Override
	public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
		return map;
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

	
}
