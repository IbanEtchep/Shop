package fr.iban.shop.menu.menus;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.iban.shop.Shop;
import fr.iban.shop.manager.ShopItem;
import fr.iban.shop.manager.ShopManager;
import fr.iban.shop.menu.PaginatedMenu;

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
		
		List<ShopItem> shopItems = sm.getShopItems().values().stream().filter(i -> i.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList());
		
		shopItems.forEach(item -> {
			Bukkit.broadcastMessage(item.toString());
		});
		
		if(shopItems != null && !shopItems.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= shopItems.size()) break;
                if (shopItems.get(index) != null){

                    inventory.addItem(shopItems.get(index).getItem());

                }
            }
        }

	}

}
