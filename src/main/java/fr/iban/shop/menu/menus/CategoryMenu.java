package fr.iban.shop.menu.menus;

import fr.iban.shop.ShopItem;
import fr.iban.shop.ShopPlugin;
import fr.iban.shop.manager.ShopManager;
import fr.iban.shop.menu.PaginatedMenu;
import fr.iban.shop.utils.ItemBuilder;
import fr.iban.shop.utils.ShopAction;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CategoryMenu extends PaginatedMenu{

	private final String category;
	private final Economy economy = ShopPlugin.getInstance().getEconomy();

	public CategoryMenu(Player player, String category) {
		super(player);
		this.category = category;
	}

	@Override
	public String getMenuName() {
		return "§6Shop §8> §e" + StringUtils.capitalize(category);
	}

	@Override
	public int getSlots() {
		return 54;
	}

	@Override
	public void handleMenu(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ShopManager sm = ShopPlugin.getInstance().getShopManager();
		List<ShopItem> shopItems = new ArrayList<>(sm.getShopItems(category));

		if(e.getClickedInventory() == e.getView().getTopInventory()) {
			if(e.getCurrentItem() == null) return;
			if(e.getCurrentItem().getType().toString().toUpperCase().endsWith("GLASS_PANE")) {
				if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "précédent")){
					if (page == 0){
						p.sendMessage("§cVous êtes déjà à la première page.");
					}else{
						page = page - 1;
						super.open();
					}
				}else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN  + "suivant")){
					if ((index + 1) <= shopItems.size()){
						page = page + 1;
						super.open();
					}else{
						p.sendMessage("§cVous êtes déjà à la dernière page.");
					}
				}else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_RED + "fermer")){
					p.closeInventory();
				}
			}else {
				ShopItem clickedItem = getMatch(shopItems, e.getCurrentItem());

				if(clickedItem == null) return;		

				if(e.getClick() == ClickType.RIGHT) {
					//Vendre
					if(clickedItem.getSellPrice() > 0) {
						new ConfirmMenu(p, clickedItem, ShopAction.SELL).open();
					}
				}else if(e.getClick() == ClickType.LEFT) {
					//Acheter
					if(clickedItem.getBuyPrice() > 0) {
						new ConfirmMenu(p, clickedItem, ShopAction.BUY).open();
					}
				}else if(e.getClick() == ClickType.SHIFT_RIGHT) {
					//Tout vendre
					if(clickedItem.getSellPrice() > 0) {
						int amount = ShopPlugin.getInstance().getTransactionManager().getSellAllAmount(clickedItem, player);
						if(amount > 0) {
							ShopPlugin.getInstance().getTransactionManager().sellItem(p, clickedItem, amount);
							super.open();
						}
					}
				}else if(e.getClick() == ClickType.SHIFT_LEFT && player.hasPermission("shop.admin")) {
					new ShopItemEditMenu(p, clickedItem).open();
				}

			}
		}else if(e.getClick() == ClickType.SHIFT_RIGHT && player.hasPermission("shop.admin")) {
			if(e.getCurrentItem() == null) return;
			for (ShopItem shopItem : shopItems) {
				if(shopItem.getItemStack().isSimilar(e.getCurrentItem())) return;
			}
			ShopItem item = new ShopItem(
					-1,
					0, 0, new ItemBuilder(e.getCurrentItem().clone()).setAmount(1).build(), category, 0, 0);
			sm.addShopItem(item);
		}
	}

	@Override
	public void open() {
		ShopManager sm = ShopPlugin.getInstance().getShopManager();

		if(!sm.getCategories().contains(category)) {
			player.sendMessage("§cLa catégorie " + category + " n'éxiste pas.");
			return;
		}

		super.open();
	}

	@Override
	public void setMenuItems() {
		ShopManager sm = ShopPlugin.getInstance().getShopManager();
		addMenuBorder();
		List<ShopItem> shopItems = new ArrayList<>(sm.getShopItems(category));

		if(!shopItems.isEmpty()) {
			for(int i = 0; i < getMaxItemsPerPage(); i++) {
				index = getMaxItemsPerPage() * page + i;
				if(index >= shopItems.size()) break;
				if (shopItems.get(index) != null){
					ShopItem shopItem = shopItems.get(index);
					if(shopItem.getItemStack() == null) return;
					inventory.addItem(getShopDisplayItem(shopItem));
				}
			}
		}

	}

	private ItemStack getShopDisplayItem(ShopItem shopItem) {
		ItemStack it = new ItemBuilder(shopItem.getItemStack().clone())
				.addLore(shopItem.getMaxStock() == 0 ? "§f§lStock: §7illimité" : "§f§lStock: §7" + shopItem.getStock()+"§f/§8"+shopItem.getMaxStock())
				.build();
		if(shopItem.getBuyPrice() != 0) {
			it = new ItemBuilder(it).addLore("§f§lAchat: §b" + economy.format(shopItem.calculatePrice(1, ShopAction.BUY)) + shopItem.getPriceVariationString(ShopAction.BUY) + "§7 (clic gauche)").build();
		}
		if(shopItem.getSellPrice() != 0) {
			int amount = ShopPlugin.getInstance().getTransactionManager().getSellAllAmount(shopItem, player);
			it = new ItemBuilder(it).addLore("§f§lVente: §b" + economy.format(shopItem.calculatePrice(1, ShopAction.SELL)) + shopItem.getPriceVariationString(ShopAction.SELL) + "§7 (clic droit)").build();
			if(amount != 0) {
				it = new ItemBuilder(it).addLore("§f§lVente rapide : §b×" + amount + "➪"+ economy.format(shopItem.calculatePrice(amount, ShopAction.SELL)) + " §7(shift + clic gauche)").build();
			}
		}
		return it;
	}

	private ShopItem getMatch(List<ShopItem> fromlist, ItemStack item) {
		for (ShopItem shopItem : fromlist) {
			if(shopItem.getItemStack().getType() == item.getType() && shopItem.getItemStack().getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
				return shopItem;
			}
		}
		return null;
	}

}
