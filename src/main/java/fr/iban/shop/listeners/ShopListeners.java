package fr.iban.shop.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.InventoryHolder;

import fr.iban.shop.events.ShopActionEvent;
import fr.iban.shop.events.ShopFluctuateEvent;
import fr.iban.shop.events.ShopReloadEvent;
import fr.iban.shop.menu.Menu;
import fr.iban.shop.menu.menus.CategoryMenu;
import fr.iban.shop.menu.menus.ConfirmMenu;

public class ShopListeners implements Listener {
	
	@EventHandler
	public void onShopReload(ShopReloadEvent e) {
		reloadInventories();
	}
	
	@EventHandler
	public void onShopFluctuate(ShopFluctuateEvent e) {
		reloadInventories();
	}
	
	@EventHandler
	public void onShopFluctuate(ShopActionEvent e) {
		reloadInventories();
	}
	
	public void reloadInventories() {
		for(Player player : Bukkit.getOnlinePlayers()) {
	        InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
	        if(holder instanceof CategoryMenu || holder instanceof ConfirmMenu) {
	        	Menu menu = (Menu)holder;
	        	menu.open();
	        }
		}
	}
}
