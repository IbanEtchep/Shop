package fr.iban.shop.menu.menus;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.iban.shop.Shop;
import fr.iban.shop.manager.ShopItem;
import fr.iban.shop.manager.ShopManager;
import fr.iban.shop.menu.PaginatedMenu;
import fr.iban.shop.utils.ItemBuilder;

public class CategoryMenu extends PaginatedMenu{

	private String category;

	public CategoryMenu(Player player, String category) {
		super(player);
		this.category = category;
	}

	@Override
	public String getMenuName() {
		return "§6Shop §8> §e" + category;
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
			if(e.getClick() == ClickType.RIGHT) {
				//Vendre
			}else if(e.getClick() == ClickType.LEFT) {
				//Acheter
			}else if(e.getClick() == ClickType.CREATIVE) {
				//Tout vendre
			}
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
					inventory.addItem(new ItemBuilder(shopItem.getItem().clone())
							.addLore("§cAchat : §b" + shopItem.getBuy() + "$§7 (clic gauche)")
							.addLore("§aVente : §b" + shopItem.getSell() + "$§7 (clic droit)")
							.addLore("§7Clic molette pour tout vendre.")
							.build());
				}
			}
		}

	}

}
