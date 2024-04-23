package fr.iban.shop.listeners;

import fr.iban.bukkitcore.menu.ConfirmMenu;
import fr.iban.lands.LandsPlugin;
import fr.iban.lands.model.land.Land;
import fr.iban.shop.ShopPlugin;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

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
            Land land = LandsPlugin.getInstance().getLandRepository().getLandAt(block.getLocation());
            if (!land.isBypassing(player, fr.iban.lands.enums.Action.OPEN_CONTAINER)) {
                return;
            }
        } catch (Exception ignore) {
        }


        if (block.getState() instanceof Container container) {
            Inventory inventory = container.getInventory();
            double sellAllTotal = plugin.getTransactionManager().getSellWandPrice(inventory);
            if (sellAllTotal > 0) {
                new ConfirmMenu(player, "§8Tout vendre pour : " + plugin.getEconomy().format(sellAllTotal),
                        "§fTout vendre le contenu du coffre pour " + plugin.getEconomy().format(sellAllTotal), result -> {
                    if (result) {
                        if (plugin.getTransactionManager().sellShopItems(player, inventory)) {
                            plugin.getShopManager().consumeSellWand(e.getItem(), player);
                        }
                    }
                    player.closeInventory();
                }).open();
            } else {
                player.sendMessage("§cIl n'y a rien à vendre dans ce coffre ou le stock du marché est plein.");
            }
            e.setCancelled(true);
        }

    }

}
