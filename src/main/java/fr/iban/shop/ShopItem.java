package fr.iban.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.iban.shop.utils.ShopAction;
import net.md_5.bungee.api.ChatColor;

public class ShopItem {

	private final int id;
	private double buyPrice;
	private double sellPrice;
	private ItemStack itemStack;
	private final String category;
	private int maxStock = 0;
	private int stock = 0;

	public ShopItem(int id, double buyPrice, Material material, String category, int maxstock) {
		this(id, buyPrice, buyPrice/10, new ItemStack(material), category, maxstock, maxstock/2);
	}
	
	public ShopItem(int id, double buyPrice, double sellPrice, ItemStack item, String category) {
		this.id = id;
		this.setBuyPrice(buyPrice);
		this.setSellPrice(sellPrice);
		this.itemStack = item;
		this.category = category;
	}

	public ShopItem(int id, double buyPrice, double sellPrice, ItemStack item, String category, int maxStock) {
		this.id = id;
		this.setBuyPrice(buyPrice);
		this.setSellPrice(sellPrice);
		this.itemStack = item;
		this.category = category;
		this.maxStock = maxStock;
	}

	public ShopItem(int id, double buyPrice, double sellPrice, ItemStack item, String category, int maxStock, int stock) {
		super();
		this.id = id;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.itemStack = item;
		this.category = category;
		this.maxStock = maxStock;
		this.setStock(stock);
	}

	public ItemStack getItemStack() {
		return itemStack;
	}
	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
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

	public void setMaxStock(int maxStock) {
		this.maxStock = maxStock;
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}

	public double getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public double getModifier(int currentstock) {
		if(maxStock == 0) return 1;
		double denominateur = currentstock;
		if(currentstock < maxStock/4.0) {
			denominateur = maxStock/4.0;
		}
		return ((double)maxStock/2)/denominateur;
	}

	private double getPercent(int currentstock) {
		double percent = (1-getModifier(currentstock));
		return Math.round(percent*1000)/10.0;
	}

	public String getPriceVariationString(ShopAction action) {
		double percent = getPercent(stock);
		if(percent == 0) {
			return "";
		}else if(percent > 0){
			return (action == ShopAction.BUY ? ChatColor.GREEN : ChatColor.RED) + " ⬇ -" + percent + "%";
		}else {
			return  (action == ShopAction.BUY ? ChatColor.RED : ChatColor.GREEN) + " ⬆ +" + Math.abs(percent) + "%";
		}
	}

	public double calculatePrice(int amount, ShopAction action) {
		double finalAmount = 0;
		int vstock = stock;
		if(action == ShopAction.BUY) {
			for (int i = 0; i < amount; i++) {
				finalAmount += buyPrice * getModifier(vstock);
				vstock--;
			}
		}else if(action == ShopAction.SELL) {
			for (int i = 0; i < amount; i++) {
				finalAmount += sellPrice * getModifier(vstock);
				vstock++;
			}
		}
		return (double)Math.round(finalAmount * 100)/100;
	}

}
