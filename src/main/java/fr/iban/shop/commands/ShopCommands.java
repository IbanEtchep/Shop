package fr.iban.shop.commands;

import fr.iban.shop.ShopPlugin;
import fr.iban.shop.manager.ShopManager;
import fr.iban.shop.menu.menus.CategoryMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.*;
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
    @Default
    public void openCategory(Player sender, String category) {
        new CategoryMenu(sender, category).open();
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


}
