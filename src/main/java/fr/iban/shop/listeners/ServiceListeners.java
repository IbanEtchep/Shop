package fr.iban.shop.listeners;

import fr.iban.shop.Shop;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;

public class ServiceListeners implements Listener {

    private Shop plugin;

    public ServiceListeners(Shop plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServiceRegister(ServiceRegisterEvent event) {
        if (!event.getProvider().getService().getName().equals("net.milkbowl.vault.economy.Economy")) {
            return;
        }
        plugin.setupEconomy();
    }

    @EventHandler
    public void onServiceUnregister(ServiceUnregisterEvent event) {
        if (!event.getProvider().getService().getName().equals("net.milkbowl.vault.economy.Economy")) {
            return;
        }
        plugin.setupEconomy();
    }

}
