package fr.iban.shop.menu.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.iban.shop.ShopPlugin;
import fr.iban.shop.ShopItem;
import fr.iban.shop.manager.TransactionManager;
import fr.iban.shop.menu.Menu;
import fr.iban.shop.utils.ItemBuilder;
import fr.iban.shop.utils.ShopAction;

public class ConfirmMenu extends Menu {

	private final ShopItem shopItem;
	private final ShopAction action;

	private int amount = 1;

	public ConfirmMenu(Player player, ShopItem shopItem, ShopAction action) {
		super(player);
		this.shopItem = shopItem;
		this.action = action;
	}

	@Override
	public String getMenuName() {
		return "§8" + action.getString() + " : " + shopItem.getItemStack().getItemMeta().getDisplayName();
	}

	@Override
	public int getSlots() {
		return 54;
	}

	@Override
	public void handleMenu(InventoryClickEvent e) {
		ItemStack current = e.getCurrentItem();
		if(current == null) return;

		switch (current.getItemMeta().getDisplayName()) {
		case "§c§lMettre à 1":
			amount = 1;
			break;
		case "§c§lRetirer 10":
			amount -= 10;
			break;
		case "§c§lRetirer 1":
			amount -= 1;
			break;
		case "§a§lMettre à 64":
			amount = 64;
			break;
		case "§a§lAjouter 10":
			amount += 10;
			break;
		case "§a§lAjouter 1":
			amount += 1;
			break;
		case "§cRetour":
			new CategoryMenu(player, shopItem.getCategory()).open();
			return;
		case "§aConfirmer":
			TransactionManager tm = ShopPlugin.getInstance().getTransactionManager();
			if(action == ShopAction.BUY) {
				tm.buyItem(player, shopItem, amount);
			}else {
				tm.sellItem(player, shopItem, amount);
			}
			break;
		default:
			if(current.getItemMeta().getDisplayName().startsWith("§aTout vendre pour")) {
				ShopPlugin.getInstance().getTransactionManager().sellItem(player, shopItem, ShopPlugin.getInstance().getTransactionManager().getSellAllAmount(shopItem, player));
			}
			return;
		}
		super.open();

	}

	@Override
	public void setMenuItems() {

		double prix = shopItem.calculatePrice(amount, action);

		inventory.setItem(22, new ItemBuilder(shopItem.getItemStack().clone()).setAmount(amount).addLore("§d§lPrix : §5§l" + prix + ShopPlugin.SYMBOLE).build());

		if(amount > 1)
			inventory.setItem(18, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName("§c§lMettre à 1").build());
		if(amount > 10)
			inventory.setItem(19, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName("§c§lRetirer 10").build());
		if(amount > 1)
			inventory.setItem(20, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName("§c§lRetirer 1").build());

		if(amount < 64) {
			inventory.setItem(24, new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setName("§a§lAjouter 1").build());
			inventory.setItem(26, new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setName("§a§lMettre à 64").build());
		}
		if(amount <= 54 )
			inventory.setItem(25, new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setName("§a§lAjouter 10").build());

		inventory.setItem(39, new ItemBuilder(Material.RED_STAINED_GLASS).setName("§cRetour").build());
		if(action == ShopAction.SELL) {
			int sellAllAmount = ShopPlugin.getInstance().getTransactionManager().getSellAllAmount(shopItem, player);
			inventory.setItem(40, new ItemBuilder(Material.LIME_STAINED_GLASS).setName("§aTout vendre pour : §f" + shopItem.calculatePrice(sellAllAmount, ShopAction.SELL)+ ShopPlugin.SYMBOLE).build());
		}
		inventory.setItem(41, new ItemBuilder(Material.GREEN_STAINED_GLASS).setName("§aConfirmer").build());
	}

}
