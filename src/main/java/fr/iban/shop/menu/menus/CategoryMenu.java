package fr.iban.shop.menu.menus;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
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
		// TODO Auto-generated method stub
	}

	@Override
	public void setMenuItems() {
        addMenuBorder();

		ShopManager sm = Shop.getInstance().getShopManager();
		
		List<ShopItem> shopItems = sm.getShopItems().get(category).values().stream().collect(Collectors.toList());
		
		if(shopItems != null && !shopItems.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= shopItems.size()) break;
                if (shopItems.get(index) != null){
                	ShopItem shopItem = shopItems.get(index);
                    inventory.addItem(new ItemBuilder(shopItem.getItem())
                    		.addLore("§cAchat : §b" + shopItem.getBuy() + "$§7 (clic gauche)")
                    		.addLore("§aVente : §b" + shopItem.getSell() + "$§7 (clic droit)")
                    		.addLore("§7Clic molette pour tout vendre.")
                    		.build());
                }
            }
        }

	}

}
