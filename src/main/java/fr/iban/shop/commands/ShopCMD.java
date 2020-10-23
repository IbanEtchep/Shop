package fr.iban.shop.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.iban.shop.menu.menus.CategoryMenu;

public class ShopCMD implements CommandExecutor {

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

}
