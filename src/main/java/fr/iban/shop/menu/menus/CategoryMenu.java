package fr.iban.shop.menu.menus;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import fr.iban.shop.Shop;
import fr.iban.shop.manager.ShopItem;
import fr.iban.shop.manager.ShopManager;
import fr.iban.shop.menu.PaginatedMenu;
import fr.iban.shop.utils.ItemBuilder;
import fr.iban.shop.utils.ShopAction;

public class CategoryMenu extends PaginatedMenu{

	private String category;

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
		ShopManager sm = Shop.getInstance().getShopManager();
		List<ShopItem> shopItems = sm.getShopItems().get(category).values().stream().collect(Collectors.toList());

		if(e.getClickedInventory() == e.getView().getTopInventory()) {
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
					if(clickedItem.getSell() > 0) {
						new ConfirmMenu(p, clickedItem, ShopAction.SELL).open();
					}
				}else if(e.getClick() == ClickType.LEFT) {
					//Acheter
					if(clickedItem.getBuy() > 0) {
						new ConfirmMenu(p, clickedItem, ShopAction.BUY).open();
					}
				}else if(e.getClick() == ClickType.MIDDLE) {
					//Tout vendre
					if(clickedItem.getSell() > 0) {
						int amount = Shop.getInstance().getTransactionManager().getSellAllAmount(clickedItem, player);
						if(amount > 0) {
							Shop.getInstance().getTransactionManager().sellItem(p, clickedItem, amount);
							super.open();
						}
					}
				}else if(e.getClick() == ClickType.SHIFT_RIGHT && player.hasPermission("shop.admin")) {
					new ShopItemEditMenu(p, clickedItem).open();
				}

			}
		}else if(e.getClick() == ClickType.SHIFT_RIGHT && player.hasPermission("shop.admin")) {
			for (ShopItem shopItem : shopItems) {
				if(shopItem.getItem().isSimilar(e.getCurrentItem())) return;
			}
			ShopItem item = new ShopItem(
					sm.getNewID(category),
					0, 0, new ItemBuilder(e.getCurrentItem().clone()).setAmount(1).build(), category, 0, 0);
			sm.saveShop(item);
			sm.reloadShops();
		}
	}

	@Override
	public void open() {
		ShopManager sm = Shop.getInstance().getShopManager();

		if(!sm.getShopItems().containsKey(category)) {
			player.sendMessage("§cLa catégorie " + category + " n'éxiste pas.");
			return;
		}
		super.open();
	}

	@Override
	public void setMenuItems() {
		ShopManager sm = Shop.getInstance().getShopManager();

		addMenuBorder();

		List<ShopItem> shopItems = sm.getShopItems().get(category).values().stream().collect(Collectors.toList());

		if(shopItems != null && !shopItems.isEmpty()) {
			for(int i = 0; i < getMaxItemsPerPage(); i++) {
				index = getMaxItemsPerPage() * page + i;
				if(index >= shopItems.size()) break;
				if (shopItems.get(index) != null){
					ShopItem shopItem = shopItems.get(index);
					inventory.addItem(getShopDisplayItem(shopItem));
				}
			}
		}

	}

	private ItemStack getShopDisplayItem(ShopItem shopItem) {
		ItemStack it = new ItemBuilder(shopItem.getItem().clone())
				.addLore(shopItem.getMaxStock() == 0 ? "§f§lStock: §7illimité" : "§f§lStock: §7" + shopItem.getStock()+"§f/§8"+shopItem.getMaxStock())
				.build();
		if(shopItem.getBuy() != 0) {
			it = new ItemBuilder(it).addLore("§f§lAchat: §b" + shopItem.calculatePrice(1, ShopAction.BUY) + Shop.SYMBOLE + shopItem.getPriceVariationString(ShopAction.BUY) + "§7 (clic gauche)").build();
		}
		if(shopItem.getSell() != 0) {
			int amount = Shop.getInstance().getTransactionManager().getSellAllAmount(shopItem, player);
			it = new ItemBuilder(it).addLore("§f§lVente: §b" + shopItem.calculatePrice(1, ShopAction.SELL) + Shop.SYMBOLE + shopItem.getPriceVariationString(ShopAction.SELL) + "§7 (clic droit)").build();
			if(amount != 0) {
				it = new ItemBuilder(it).addLore("§f§lVente rapide : §b×" + amount + "➪"+ shopItem.calculatePrice(amount, ShopAction.SELL)+ Shop.SYMBOLE + " §7(clic molette)").build();
			}
		}
		return it;
	}

	private ShopItem getMatch(List<ShopItem> fromlist, ItemStack item) {
		for (ShopItem shopItem : fromlist) {
			if(shopItem.getItem().getType() == item.getType() && shopItem.getItem().getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
				return shopItem;
			}
		}
		return null;
	}

}
