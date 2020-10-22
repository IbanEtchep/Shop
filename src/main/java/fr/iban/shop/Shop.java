package fr.iban.shop;

import org.bukkit.plugin.java.JavaPlugin;

public final class Shop extends JavaPlugin {

    private static Shop instance;

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Shop getInstance() {
        return instance;
    }

}
