package fr.iban.shop.commands;

import fr.iban.shop.ShopPlugin;
import fr.iban.shop.manager.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.command.CommandActor;

public class ShopStatsCommands {

    private final ShopPlugin plugin;
    private final ShopManager shopManager;

    public ShopStatsCommands(ShopPlugin plugin) {
        this.plugin = plugin;
        this.shopManager = plugin.getShopManager();
    }

    @Command("statsmarché achats")
    @Cooldown(10)
    public void purchasesTopPlayer(Player sender) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            sender.sendMessage("§7Voici vos meilleurs achats au marché : ");
            for (String line : shopManager.getDbAccess().getPlayerTopPurchases(sender.getUniqueId())) {
                sender.sendMessage(line);
            }
        });
    }

    @Command("statsmarché ventes")
    @Cooldown(10)
    public void salesTopPlayer(Player sender) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            sender.sendMessage("§7Voici vos meilleurs achats au marché : ");
            for (String line : shopManager.getDbAccess().getPlayerTopSales(sender.getUniqueId())) {
                sender.sendMessage(line);
            }
        });
    }

    @Command("shopstats purchases")
    @CommandPermission("shopstats.others")
    public void purchasesTop(CommandActor sender, @Optional OfflinePlayer offlinePlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (offlinePlayer == null) {
                sender.reply("§7Voici les meilleurs achats au marché:");
                for (String line : shopManager.getDbAccess().getTopPurchases()) {
                    sender.reply(line);
                }
            } else {
                sender.reply("§7Voici les meilleurs achats au marché de " + offlinePlayer.getName());
                for (String line : shopManager.getDbAccess().getPlayerTopPurchases(offlinePlayer.getUniqueId())) {
                    sender.reply(line);
                }
            }
        });
    }

    @Command("shopstats sales")
    @CommandPermission("shopstats.others")
    public void salesTop(CommandActor sender, @Optional OfflinePlayer offlinePlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (offlinePlayer == null) {
                sender.reply("§7Voici les meilleurs ventes au marché :");
                for (String line : shopManager.getDbAccess().getTopSales()) {
                    sender.reply(line);
                }
            } else {
                sender.reply("§7Voici les meilleurs ventes au marché de " + offlinePlayer.getName());
                for (String line : shopManager.getDbAccess().getPlayerTopSales(offlinePlayer.getUniqueId())) {
                    sender.reply(line);
                }
            }
        });
    }


}
