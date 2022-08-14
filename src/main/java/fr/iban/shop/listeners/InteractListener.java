package fr.iban.shop.listeners;

import fr.iban.lands.LandManager;
import fr.iban.lands.LandsPlugin;
import fr.iban.lands.objects.Land;
import fr.iban.shop.ShopPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.awt.*;

public class InteractListener implements Listener {

    private final ShopPlugin plugin;

    public InteractListener(ShopPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = e.getClickedBlock();
        if (block == null || e.getItem() == null) {
            return;
        }

        if (!plugin.getShopManager().isSellWand(e.getItem())) {
            return;
        }

        if (e.getItem().getAmount() != 1) {
            player.sendMessage("§cLes bâtons de vente ne peuvent pas se stacker.");
            return;
        }

        try {
            Land land = LandsPlugin.getInstance().getLandManager().getLandAt(block.getLocation());
            if (!land.isBypassing(player, fr.iban.lands.enums.Action.OPEN_CONTAINER)) {
                return;
            }
        } catch (Exception ignore) {
        }


        if (block.getState() instanceof Chest chest) {
            Inventory inventory = chest.getBlockInventory();
            if (plugin.getTransactionManager().sellShopItems(player, inventory)) {
                plugin.getShopManager().consumeSellWand(e.getItem(), player);
            }
            e.setCancelled(true);
        }
    }

}
