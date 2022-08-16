package fr.iban.shop.manager;

import fr.iban.shop.ShopItem;
import fr.iban.shop.ShopPlugin;
import fr.iban.shop.events.ShopActionEvent;
import fr.iban.shop.utils.ItemBuilder;
import fr.iban.shop.utils.ShopAction;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TransactionManager {

    private final ShopPlugin plugin;
    private final ShopManager shopManager;

    public TransactionManager(ShopPlugin plugin) {
        this.plugin = plugin;
        this.shopManager = plugin.getShopManager();
    }


    public void buyItem(Player player, ShopItem shopItem, int amount) {
        if (shopItem.getItemStack() == null || shopItem.getItemStack().getType() == Material.AIR) return;
        if (shopItem.getMaxStock() != 0 && shopItem.getStock() - amount <= 1) {
            player.sendMessage("§cCet item n'est plus en stock.");
            return;
        }
        Economy econ = plugin.getEconomy();
        double price = shopItem.calculatePrice(amount, ShopAction.BUY);
        if (econ.has(player, price)) {
            EconomyResponse r = econ.withdrawPlayer(player, price);
            if (r.transactionSuccess()) {
                ItemStack item = new ItemBuilder(shopItem.getItemStack().clone()).setAmount(amount).build();
                if (!isInventoryFull(player)) {
                    player.getInventory().addItem(item);
                } else {
                    player.getWorld().dropItem(player.getLocation(), item);
                }
                shopItem.setStock(shopItem.getStock() - amount);
                shopManager.addTransactionLog(shopItem, player.getUniqueId(), amount, price, ShopAction.BUY);
                shopManager.saveStock(shopItem);
                player.sendMessage("§aVous avez acheté " + amount + " " + item.getType().name() + " pour " + price + "$");
                String log = player.getName() + " a acheté " + amount + " " + item.getType().toString() + " pour " + price + "$";
                plugin.getLogger().info(log);
                Bukkit.getPluginManager().callEvent(new ShopActionEvent(ShopAction.BUY));
            }
        } else {
            player.sendMessage("§cVous n'avez pas les fonds nécessaires.");
        }
    }

    public void sellItem(Player player, ShopItem shopItem, int amount) {
        sellItem(player, shopItem, amount, player.getInventory());
    }

    public void sellItem(Player player, ShopItem shopItem, int amount, Inventory inventory) {
        if (shopItem.getItemStack() == null || shopItem.getItemStack().getType() == Material.AIR) return;
        int remainingStock = shopItem.getMaxStock() - shopItem.getStock();
        if (shopItem.getMaxStock() != 0 && amount > remainingStock) {
            if (remainingStock > 0) {
                amount = remainingStock;
            } else {
                player.sendMessage("§cLes réserves sont pleines, impossible de vendre davantage de cet item.");
                return;
            }
        }

        Economy econ = plugin.getEconomy();
        double price = shopItem.calculatePrice(amount, ShopAction.SELL);
        ItemStack item = new ItemBuilder(shopItem.getItemStack().clone()).setAmount(amount).build();
        if (item == null || item.getType() == Material.AIR) return;

        if (inventory.containsAtLeast(item, amount)) {
            EconomyResponse r = econ.depositPlayer(player, price);
            if (r.transactionSuccess()) {
                inventory.removeItem(item);
                player.updateInventory();
                player.sendMessage("§aVous avez vendu " + amount + " " + item.getType().name() + " pour " + price + "$");
                String log = player.getName() + " a vendu " + amount + " " + item.getType().toString() + " pour " + price + "$";
                plugin.getLogger().info(log);
                shopManager.addTransactionLog(shopItem, player.getUniqueId(), amount, price, ShopAction.SELL);
                shopItem.setStock(shopItem.getStock() + amount);
                shopManager.saveStock(shopItem);
                Bukkit.getPluginManager().callEvent(new ShopActionEvent(ShopAction.SELL));
            }
        } else {
            player.sendMessage("§cVous n'avez pas les items que vous voulez vendre.");
        }


    }

    public int getSellAllAmount(ShopItem item, Player player, ItemStack[] storage) {
        int amount = 0;
        for (ItemStack it : storage) {
            if (it != null && it.isSimilar(item.getItemStack())) {
                amount += it.getAmount();
            }
        }
        return amount;
    }

    public int getSellAllAmount(ShopItem item, Player player) {
        return getSellAllAmount(item, player, player.getInventory().getStorageContents());
    }

    private boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public List<ShopItem> getItemsToSell(Inventory inventory) {
        List<ShopItem> shopItems = new ArrayList<>();
        for (ItemStack itemStack : inventory.getStorageContents()) {
            for (ShopItem shopItem : shopManager.getShopItems()) {
                if (shopItem.getItemStack().isSimilar(itemStack) && !shopItems.contains(shopItem)) {
                    shopItems.add(shopItem);
                }
            }
        }
        return shopItems;
    }

    public boolean sellShopItems(Player player, Inventory inventory) {
        List<ShopItem> shopItems = getItemsToSell(inventory);
        if (shopItems.isEmpty()) {
            player.sendMessage("§cCe coffre ne contient rien qui puisse être vendu.");
            return false;
        }
        for (ShopItem shopItem : shopItems) {
            sellItem(player, shopItem, getSellAllAmount(shopItem, player, inventory.getStorageContents()), inventory);
        }
        return true;
    }
}
