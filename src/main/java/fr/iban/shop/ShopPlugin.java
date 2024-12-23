package fr.iban.shop;

import fr.iban.bukkitcore.CoreBukkitPlugin;
import fr.iban.bukkitcore.commands.CoreCommandHandlerVisitor;
import fr.iban.shop.commands.ShopCommands;
import fr.iban.shop.listeners.*;
import fr.iban.shop.manager.FluctuationManager;
import fr.iban.shop.manager.ShopManager;
import fr.iban.shop.manager.TransactionManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

import java.io.File;
import java.io.IOException;

public final class ShopPlugin extends JavaPlugin {

    private static ShopPlugin instance;

    private File shopsFile;
    private FileConfiguration shopsConfig;
    private ShopManager shopManager;
    private TransactionManager transactionManager;
    private FluctuationManager fluctuationManager;
    private static Economy econ = null;
    public static final String SYMBOLE = " §e⛃§r";
    public static String STOCK_SYNC_CHANNEL = "StockSyncChannel";

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        createShopsConfig();
        shopManager = new ShopManager(this);
        transactionManager = new TransactionManager(this);
        fluctuationManager = new FluctuationManager(this);
        PluginManager pm = getServer().getPluginManager();

        //Vault setup
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (getConfig().getBoolean("fluctuation.enabled", false)) {
            fluctuationManager.scheduleFluctuation(20L * 3600, 0.1D);
        }
        /*
         * Register listeners :
         */
        pm.registerEvents(new InventoryListener(), this);
        pm.registerEvents(new ShopListeners(), this);
        pm.registerEvents(new ServiceListeners(this), this);
        pm.registerEvents(new InteractListener(this), this);
        pm.registerEvents(new SyncMessageListener(this), this);
        /*
         * Register Commands:
         */
        registerCommands();
    }

    private void registerCommands() {
        Lamp<BukkitCommandActor> lamp =  BukkitLamp.builder(this)
                .accept(new CoreCommandHandlerVisitor(CoreBukkitPlugin.getInstance()).visitor())
                .build();

        lamp.register(new ShopCommands(this));
    }

    public FileConfiguration getShopsConfig() {
        return this.shopsConfig;
    }

    private void createShopsConfig() {
        shopsFile = new File(getDataFolder(), "shops.yml");
        if (!shopsFile.exists()) {
            shopsFile.getParentFile().mkdirs();
            saveResource("shops.yml", false);
        }
        shopsConfig = new YamlConfiguration();
        try {
            shopsConfig.load(shopsFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public static ShopPlugin getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return econ;
    }

    public boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public FluctuationManager getFluctuationManager() {
        return fluctuationManager;
    }

    public File getShopsFile() {
        return shopsFile;
    }

}
