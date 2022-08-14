package fr.iban.shop.menu.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import fr.iban.shop.ShopPlugin;
import fr.iban.shop.ShopItem;
import fr.iban.shop.menu.Menu;
import fr.iban.shop.utils.ItemBuilder;
import fr.iban.shop.utils.ShopAction;

public class ShopItemEditMenu extends Menu{

	private ShopItem shopItem;

	private enum Selection {
		BUY,
		SELL,
		STOCK,
		MAXSTOCK
	}

	private Selection selection = null;

	private double value;

	public ShopItemEditMenu(Player player, ShopItem shopItem) {
		super(player);
		this.shopItem = shopItem;
	}

	@Override
	public String getMenuName() {
		return "§2Edition d'article";
	}

	@Override
	public int getSlots() {
		return 54;
	}

	@Override
	public void handleMenu(InventoryClickEvent e) {
		switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
		case "§f§lPrix d'achat":
			select(Selection.BUY);
			if(e.getClick() == ClickType.SHIFT_LEFT) {
				value = shopItem.getSell()*10;
				saveSelection();
			}
			break;
		case "§f§lPrix de vente":
			select(Selection.SELL);
			if(e.getClick() == ClickType.SHIFT_LEFT) {
				value = shopItem.getBuy()/10;
				saveSelection();
			}
			select(Selection.SELL);
			break;
		case "§f§lStock":
			select(Selection.STOCK);
			break;
		case "§f§lMaxStock":
			select(Selection.MAXSTOCK);
			break;
		case "§4§lSupprimer":
			ShopPlugin.getInstance().getShopManager().getShopItems().get(shopItem.getCategory()).remove(shopItem.getId());
			ShopPlugin.getInstance().getShopsConfig().set("shops."+shopItem.getCategory()+"."+shopItem.getId(), null);
			player.sendMessage("§cL'article a bien été supprimé.");
			player.closeInventory();
			return;
		case "§cRetour":
			new CategoryMenu(player, shopItem.getCategory()).open();
			return;
		case "§aConfirmer":
			saveSelection();
			break;
		default:
			modifValue(e.getCurrentItem().getItemMeta().getDisplayName());
			break;
		}
		open();
	}

	@Override
	public void setMenuItems() {
		/*
		 * Display de l'item 4
		 * 
		 * Achat / Vente / Maxstock / Stock : sélectionnables 10 12 14 16
		 * 
		 * fois/sur 10 : 36 44
		 *  +- 1 10 100 1000  : 27 28 29 30    32 33 34 35
		 * 
		 * Retour 48 Sauvegarder 50 Supprimer 45
		 */

		inventory.setItem(4, getShopDisplayItem(shopItem));

		inventory.setItem(10, new ItemBuilder(Material.GOLD_INGOT).setDisplayName("§f§lPrix d'achat").setGlow(selection == Selection.BUY).build());
		inventory.setItem(12, new ItemBuilder(Material.GOLD_INGOT).setDisplayName("§f§lPrix de vente").setGlow(selection == Selection.SELL).build());
		inventory.setItem(14, new ItemBuilder(Material.ACACIA_WOOD).setDisplayName("§f§lStock").setGlow(selection == Selection.STOCK).build());
		inventory.setItem(16, new ItemBuilder(Material.BIRCH_WOOD).setDisplayName("§f§lMaxStock").setGlow(selection == Selection.MAXSTOCK).build());

		inventory.setItem(30, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName("§c§l-1").build());
		inventory.setItem(29, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName("§c§l-10").build());
		inventory.setItem(28, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName("§c§l-100").build());
		inventory.setItem(27, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName("§c§l-1000").build());

		if(selection != null)
			inventory.setItem(31, new ItemBuilder(Material.NETHER_STAR).setName(selection + ": " + value).build());

		inventory.setItem(32, new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setName("§a§l+1").build());
		inventory.setItem(33, new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setName("§a§l+10").build());
		inventory.setItem(34, new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setName("§a§l+100").build());
		inventory.setItem(35, new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setName("§a§l+1000").build());

		inventory.setItem(36, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName("§c§l/10").build());
		inventory.setItem(44, new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setName("§a§lx10").build());

		inventory.setItem(45, new ItemBuilder(Material.RED_WOOL).setName("§4§lSupprimer").build());
		inventory.setItem(48, new ItemBuilder(Material.RED_STAINED_GLASS).setName("§cRetour").build());
		inventory.setItem(50, new ItemBuilder(Material.GREEN_STAINED_GLASS).setName("§aConfirmer").build());

	}

	private ItemStack getShopDisplayItem(ShopItem shopItem) {
		return new ItemBuilder(shopItem.getItem().clone())
				.addLore("§f§lStock: §7" + shopItem.getStock()+"§f/§8"+shopItem.getMaxStock())
				.addLore("§f§lAchat: §b" + shopItem.calculatePrice(1, ShopAction.BUY) + ShopPlugin.SYMBOLE + shopItem.getPriceVariationString(ShopAction.BUY))
				.addLore("§f§lVente: §b" + shopItem.calculatePrice(1, ShopAction.SELL) + ShopPlugin.SYMBOLE + shopItem.getPriceVariationString(ShopAction.SELL))
				.build();
	}

	public void select(Selection select) {
		saveSelection();
		selection = select;
		switch (selection) {
		case BUY:
			value = shopItem.getBuy();
			break;
		case SELL:
			value = shopItem.getSell();
			break;
		case STOCK:
			value = shopItem.getStock();
			break;
		case MAXSTOCK:
			value = shopItem.getMaxStock();
			break;
		default:
			break;
		}
		player.sendMessage("§aVous avez sélectionné : " + selection);
	}

	public void saveSelection() {
		if(selection != null) {
			switch (selection) {
			case BUY:
				shopItem.setBuy(value);
				player.sendMessage("§aPrix d'achat mis à : §2" + value);
				break;
			case SELL:
				shopItem.setSell(value);
				player.sendMessage("§aPrix de vente mis à : §2" + value);
				break;
			case STOCK:
				shopItem.setStock((int)value);
				player.sendMessage("§aStock mis à : §2" + (int)value);
				break;
			case MAXSTOCK:
				shopItem.setMaxStock((int)value);
				player.sendMessage("§aStock maximum mis à : §2" + (int)value);
				break;
			default:
				break;
			}
		}
	}

	public void modifValue(String clicked) {
		if(clicked.length() > 5) {
			String end = clicked.substring(4);
			try {
				double val = Double.parseDouble(end.substring(1));
				if(end.startsWith("+")) {
					value += val;
				}else if(end.startsWith("-")) {
					value -= val;
				}else if(end.startsWith("/")) {
					value /= val;
				}else if(end.startsWith("x")) {
					value *= val;
				}
				if(value < 0) value = 0;
			} catch (NumberFormatException e) {
				//Ignore
			}
		}
	}


}
