package fr.iban.shop;

import java.io.File;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.iban.shop.manager.ShopItem;
import fr.iban.shop.manager.ShopManager;

public final class Shop extends JavaPlugin {
    
    private File shopsFile;
    private FileConfiguration shopsConfig;
    private ShopManager shopManager;

    @Override
    public void onEnable() {
        createShopsConfig();
        shopManager = new ShopManager(this);
        PluginManager pm = getServer().getPluginManager();

        shopManager.saveShop(new ShopItem(1, 10.3, new ItemStack(Material.DIAMOND), "minerais"));
        //Listeners:
        
        
        //Commands:
    }

    @Override
    public void onDisable() {
        try {
			shopsConfig.save(shopsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

}
