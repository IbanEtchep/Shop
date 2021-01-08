package fr.iban.shop.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.iban.shop.Shop;
import fr.iban.shop.events.ShopActionEvent;
import fr.iban.shop.utils.ItemBuilder;
import fr.iban.shop.utils.ShopAction;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class TransactionManager {

	public void buyItem(Player player, ShopItem shopItem, int amount){
		if(shopItem.getMaxStock() != 0 && shopItem.getStock() - amount <= 1) {
			player.sendMessage("§cCet item n'est plus en stock.");
			return;
		}
		Economy econ = Shop.getEconomy();
		double prix = shopItem.calculatePrice(amount, ShopAction.BUY);
		if(econ.has(player, prix)) {
			EconomyResponse r =	econ.withdrawPlayer(player, prix);
			if(r.transactionSuccess()){
				ItemStack item = new ItemBuilder(shopItem.getItem().clone()).setAmount(amount).build();
				if(!isInventoryFull(player)) {
					player.getInventory().addItem(item);
				}else {
					player.getWorld().dropItem(player.getLocation(), item);
				}
				shopItem.setStock(shopItem.getStock() - amount);
				player.sendMessage("§aVous avez acheté " + amount + " " + item.getType().name() + " pour " + prix + "$");
				Bukkit.getPluginManager().callEvent(new ShopActionEvent(ShopAction.BUY));
			}
		}else {
			player.sendMessage("§cVous n'avez pas les fonds nécessaires.");
		}
	}


	public void sellItem(Player player, ShopItem shopItem, int amount) {
		if(shopItem.getMaxStock() != 0 && shopItem.getStock() + amount >= shopItem.getMaxStock()) {
			player.sendMessage("§cLes réserves sont pleines, impossible de vendre davantage de cet item.");
			return;
		}
		Economy econ = Shop.getEconomy();
		double prix = shopItem.calculatePrice(amount, ShopAction.SELL);
		ItemStack item = new ItemBuilder(shopItem.getItem().clone()).setAmount(amount).build();

		if(player.getInventory().containsAtLeast(item, amount)) {
			EconomyResponse r = econ.depositPlayer(player, prix);
			if(r.transactionSuccess()){
				player.getInventory().removeItem(item);
				player.updateInventory();
				player.sendMessage("§aVous avez vendu " + amount + " " + item.getType().name() + " pour " + prix + "$");	
				shopItem.setStock(shopItem.getStock() + amount);
				Bukkit.getPluginManager().callEvent(new ShopActionEvent(ShopAction.SELL));
			}
		}else {
			player.sendMessage("§cVous n'avez pas les items que vous voulez vendre.");
		}


	}
	
	public int getSellAllAmount(ShopItem item, Player player) {
		int amount = 0;
		for(ItemStack it : player.getInventory().getStorageContents()) {
			if(it != null && it.isSimilar(item.getItem())) {
				amount += it.getAmount();
			}
		}
		return amount;
	}

	private boolean isInventoryFull(Player player) {
		return player.getInventory().firstEmpty() == -1;
	}
}
