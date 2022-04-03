package fr.iban.shop;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import fr.iban.shop.commands.ShopCMD;
import fr.iban.shop.listeners.InventoryListener;
import fr.iban.shop.listeners.ShopListeners;
import fr.iban.shop.manager.FluctuationManager;
import fr.iban.shop.manager.ShopManager;
import fr.iban.shop.manager.TransactionManager;
import net.milkbowl.vault.economy.Economy;

public final class Shop extends JavaPlugin {
	
	private static Shop instance;
    
    private File shopsFile;
    private FileConfiguration shopsConfig;
    private ShopManager shopManager;
    private TransactionManager transactionManager;
    private FluctuationManager fluctuationManager;
    private static Economy econ = null;
    
    public static final String SYMBOLE = " §e⛃§r";

    @Override
    public void onEnable() {
    	instance = this;
        createShopsConfig();
        shopManager = new ShopManager(this);
        transactionManager = new TransactionManager(this);
        fluctuationManager = new FluctuationManager(shopManager);
        PluginManager pm = getServer().getPluginManager();
        
        //Vault setup
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        shopManager.loadShops();
        fluctuationManager.scheduleFluctuation(20L*3600, 0.1D);
        /*
         * Register listeners :
         */
        pm.registerEvents(new InventoryListener(), this);
        pm.registerEvents(new ShopListeners(), this);
        /*
         * Register Commands:
         */
        getCommand("shop").setExecutor(new ShopCMD(this));
        
        getCommand("shop").setTabCompleter(new ShopCMD(this));
    }

    @Override
    public void onDisable() {
    	shopManager.saveShops();
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

	public static Shop getInstance() {
		return instance;
	}
	
    public static Economy getEconomy() {
        return econ;
    }
	
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public File getShopsFile() {
		return shopsFile;
	}

}
