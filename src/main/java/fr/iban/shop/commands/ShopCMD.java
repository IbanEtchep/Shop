package fr.iban.shop.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import fr.iban.shop.Shop;
import fr.iban.shop.manager.ShopManager;
import fr.iban.shop.menu.menus.CategoryMenu;

public class ShopCMD implements CommandExecutor, TabCompleter {

	private Shop plugin;
	private ShopManager sm;

	public ShopCMD(Shop plugin) {
		this.plugin = plugin;
		this.sm = plugin.getShopManager();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			//TODO help
		}else {
			switch (args[0]) {
			case "reload":
				if(args.length == 1 && sender.hasPermission("shop.admin")) {
					sm.reloadConfig();
					sender.sendMessage("§aReload des shops effectué.");
				}
				break;
			case "reloadconfig":
				if(args.length == 1 && sender.hasPermission("shop.admin")) {
					sm.loadShops();
					sender.sendMessage("§aReload de la configuration effectué.");
				}
				break;
			case "addcategory":
				if(args.length == 2 && sender.hasPermission("shop.admin")) {
					if(!sm.getShopItems().containsKey(args[1])) {
						sm.getShopItems().put(args[1], new HashMap<>());
						sender.sendMessage("§aCatégorie " + args[1] + " ajoutée.");
					}else {
						sender.sendMessage("§cCette catégorie existe déjà.");
					}
				}
				break;
//			case "changePrices":
//				ShopManager sm = Shop.getInstance().getShopManager();
//				for (Map<Integer, ShopItem> category : sm.getShopItems().values()) {
//					for (ShopItem shopItem : category.values()) {
//						shopItem.setBuy(shopItem.getSell()*10);
//					}
//				}
//				break;
			default:
				if(sender instanceof Player) {

					Player player = (Player)sender;
					if(args.length == 1 && player.hasPermission("shop.open")) {
						new CategoryMenu(player, args[0]).open();
					}else {
						player.sendMessage("§cVous n'avez pas la permission.");
					}
				}
				break;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		if(args.length == 1) {
			list.addAll(sm.getShopItems().keySet());
			return list;
		}
		if(args.length == 2 && args[1].equalsIgnoreCase("additem")) {
			list.addAll(sm.getShopItems().keySet());
			return list;
		}
		return null;
	}

}
