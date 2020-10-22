package fr.iban.shop.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShopCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			//TODO help
		}else {
			switch (args[0]) {
			case "":
				
				break;

			default:
				break;
			}
		}
		return false;
	}

}
