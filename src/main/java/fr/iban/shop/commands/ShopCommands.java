package fr.iban.shop.commands;

import fr.iban.shop.ShopPlugin;
import fr.iban.shop.manager.ShopManager;
import fr.iban.shop.menu.menus.CategoryMenu;
import fr.iban.shop.menu.menus.ShopTypeSelectMenu;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.command.CommandActor;

@Command("shop")
public class ShopCommands {

    private final ShopPlugin plugin;
    private final ShopManager shopManager;

    public ShopCommands(ShopPlugin plugin) {
        this.plugin = plugin;
        this.shopManager = plugin.getShopManager();
    }

    @Command("shop")
    @CommandPermission("shop.opencategory")
    @Default
    public void openCategory(Player sender, String category) {
        if(!sender.getName().startsWith(".")) {
            new CategoryMenu(sender, category).open();
        }else {
            new ShopTypeSelectMenu(sender, action -> new CategoryMenu(sender, category, action).open()).open();
        }
    }

    @Subcommand("reload")
    @CommandPermission("shop.admin")
    public void reload(CommandActor sender) {
        shopManager.loadShopsFromDB();
        sender.reply("§aReload des shops effectué.");
    }

    @Subcommand("addcategory")
    @CommandPermission("shop.admin")
    public void addCategory(CommandActor sender, String name) {
        if (!shopManager.getCategories().contains(name)) {
            shopManager.addCategory(name);
            sender.reply("§aCatégorie " + name + " ajoutée.");
        } else {
            sender.reply("§cCette catégorie existe déjà.");
        }
    }

    @Subcommand("migrate")
    @CommandPermission("shop.admin")
    public void migrate(CommandActor sender) {
        shopManager.migrate();
        sender.reply("§aMigration effectuée.");
    }

    @Subcommand("givesellwand")
    @CommandPermission("shop.admin")
    public void giveSellWand(CommandActor sender, Player player, @Named("durability") @Range(min = 1, max = 99999) int durability) {
        ItemStack wand = shopManager.getSellWand(durability);
        if (!isInventoryFull(player)) {
            player.getInventory().addItem(wand);
        } else {
            player.getWorld().dropItem(player.getLocation(), wand);
        }
        player.sendMessage("§aVous avez reçu un bâton de vente.");
        sender.reply("§aVous envoyé un bâton de vente à " + player.getName());
    }

    private boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    @Subcommand("stats purchases")
    @Cooldown(10)
    public void purchasesTopPlayer(BukkitCommandActor sender, @Optional OfflinePlayer offlinePlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (offlinePlayer == null) {
                if(sender.isPlayer()) {
                    sender.reply("§7Voici vos meilleurs achats au marché : ");
                    for (String line : shopManager.getDbAccess().getPlayerTopPurchases(sender.getUniqueId())) {
                        sender.reply(line);
                    }
                }else {
                    sender.reply("Veuillez spécifier le nom d'un joueur.");
                }
            } else if(sender.isConsole() || sender.getAsPlayer() != null && sender.getAsPlayer().hasPermission("shopstats.others")){
                sender.reply("§7Voici les meilleurs achats au marché de " + offlinePlayer.getName());
                for (String line : shopManager.getDbAccess().getPlayerTopPurchases(offlinePlayer.getUniqueId())) {
                    sender.reply(line);
                }
            }
        });
    }

    @Subcommand("stats sales")
    @Cooldown(10)
    public void salesTopPlayer(BukkitCommandActor sender, @Optional OfflinePlayer offlinePlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (offlinePlayer == null) {
                if(sender.isPlayer()) {
                    sender.reply("§7Voici vos meilleurs ventes au marché : ");
                    for (String line : shopManager.getDbAccess().getPlayerTopSales(sender.getUniqueId())) {
                        sender.reply(line);
                    }
                }else {
                    sender.reply("Veuillez spécifier le nom d'un joueur.");
                }
            } else if(sender.isConsole() || sender.getAsPlayer() != null && sender.getAsPlayer().hasPermission("shopstats.others")){
                sender.reply("§7Voici les meilleurs ventes au marché de " + offlinePlayer.getName());
                for (String line : shopManager.getDbAccess().getPlayerTopSales(offlinePlayer.getUniqueId())) {
                    sender.reply(line);
                }
            }
        });
    }

    @Subcommand("stats purchases")
    @CommandPermission("shopstats.global")
    public void purchasesTop(CommandActor sender) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            sender.reply("§7Voici les meilleurs achats au marché:");
            for (String line : shopManager.getDbAccess().getTopPurchases()) {
                sender.reply(line);
            }
        });
    }

    @Subcommand("stats sales")
    @CommandPermission("shopstats.global")
    public void salesTop(CommandActor sender) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            sender.reply("§7Voici les meilleurs ventes au marché :");
            for (String line : shopManager.getDbAccess().getTopSales()) {
                sender.reply(line);
            }
        });
    }


}
