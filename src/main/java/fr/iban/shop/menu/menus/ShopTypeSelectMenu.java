package fr.iban.shop.menu.menus;

import fr.iban.bukkitcore.menu.Menu;
import fr.iban.shop.utils.ItemBuilder;
import fr.iban.shop.utils.ShopAction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ShopTypeSelectMenu extends Menu {

	private final Consumer<ShopAction> callback;

	public ShopTypeSelectMenu(Player player, Consumer<ShopAction> callback) {
		super(player);
		this.callback = callback;
	}

	@Override
	public String getMenuName() {
		return "§5Acheter ou vendre?";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void handleMenu(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		if(item == null) return;
		
		if(displayNameEquals(item, "§2§lAcheter")){
			callback.accept(ShopAction.BUY);
		}else if(displayNameEquals(item, "§2§lVendre")){
			callback.accept(ShopAction.SELL);
		}

	}

	@Override
	public void setMenuItems() {
		inventory.setItem(11, new ItemBuilder(Material.CHEST).setName("§2§lAcheter").build());
		inventory.setItem(15, new ItemBuilder(Material.CHEST).setName("§2§lVendre").build());
		while(inventory.firstEmpty() != -1) {
			inventory.setItem(inventory.firstEmpty(), FILLER_GLASS);
		}
	}

}
