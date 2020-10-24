package fr.iban.shop.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import fr.iban.shop.Shop;
import fr.iban.shop.menu.menus.CategoryMenu;

public class ShopCMD implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			
			Player player = (Player)sender;
			
			if(args.length == 0) {
				//TODO help
			}else {
				switch (args[0]) {
				case "":
					
					break;

				default:
					if(args.length == 1) {
						new CategoryMenu(player, args[0]).open();
					}
					break;
				}
			}
			
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		if(args.length == 1) {
			list.addAll(Shop.getInstance().getShopManager().getShopItems().keySet());
			return list;
		}
		return null;
	}

}
