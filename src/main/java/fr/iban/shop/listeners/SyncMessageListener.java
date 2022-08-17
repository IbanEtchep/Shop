package fr.iban.shop.listeners;

import com.google.gson.Gson;
import fr.iban.bukkitcore.event.CoreMessageEvent;
import fr.iban.common.messaging.Message;
import fr.iban.lands.LandsPlugin;
import fr.iban.shop.ShopItem;
import fr.iban.shop.ShopPlugin;
import fr.iban.shop.manager.ShopManager;
import fr.iban.shop.utils.StockSyncMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SyncMessageListener implements Listener {

    private final ShopPlugin plugin;
    private final ShopManager shopManager;

    public SyncMessageListener(ShopPlugin plugin) {
        this.plugin = plugin;
        this.shopManager = plugin.getShopManager();
    }

    @EventHandler
    public void onCoreMessage(CoreMessageEvent e) {
        Message message = e.getMessage();

        if(message.getChannel().equals(ShopPlugin.STOCK_SYNC_CHANNEL)){
            StockSyncMessage stockSyncMessage = message.getMessage(StockSyncMessage.class);
            ShopItem shopItem = shopManager.getShopItemByID(stockSyncMessage.getShopID());
            shopItem.setStock(stockSyncMessage.getNewStock());
        }

    }


}
