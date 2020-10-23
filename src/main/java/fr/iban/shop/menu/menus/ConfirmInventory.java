package fr.iban.shop.menu.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.iban.shop.manager.ShopItem;
import fr.iban.shop.menu.Menu;

public class ConfirmInventory extends Menu {

	public ConfirmInventory(Player player, ShopItem shopItem) {
		super(player);
	}

	@Override
	public String getMenuName() {
		return null;
	}

	@Override
	public int getSlots() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void handleMenu(InventoryClickEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMenuItems() {
		// TODO Auto-generated method stub
		
	}

}
