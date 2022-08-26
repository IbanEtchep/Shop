package fr.iban.shop.manager;

import fr.iban.shop.ShopItem;
import fr.iban.shop.ShopPlugin;
import fr.iban.shop.events.ShopFluctuateEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class FluctuationManager {

    private final ShopPlugin plugin;
    private final ShopManager shopmanager;
    private final Random random = new Random();
    private double modifier = 1;


    public FluctuationManager(ShopPlugin plugin) {
        this.plugin = plugin;
        this.shopmanager = plugin.getShopManager();
    }

    public void fluctuate(double maxModifier) {
        for (ShopItem item : shopmanager.getShopItems()) {
            if (item.getMaxStock() == 0) continue;

            final double modifier = item.getModifier(item.getStock());
            int add = (int) (item.getMaxStock() * nextDouble(0, maxModifier));
            if (modifier > 1) {
                item.setStock(item.getStock() + add);
            } else if (modifier < 1) {
                item.setStock(item.getStock() - add);
            } else {
                int nextint = new Random().nextInt(2);
                if (nextint == 1) {
                    add *= -1;
                }
                item.setStock(item.getStock() + add);
            }
			shopmanager.saveStock(item);
        }
        Bukkit.getPluginManager().callEvent(new ShopFluctuateEvent());
    }

    public void scheduleFluctuation(long period, double max) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (modifier != 1 + 0.05 * Bukkit.getOnlinePlayers().size()) {
                    modifier = 1 + 0.05 * Bukkit.getOnlinePlayers().size();
                    scheduleFluctuation(period, max);
                    cancel();
                }
                fluctuate(max);
            }
        }.runTaskTimer(plugin, 0L, (long) (period / modifier));
    }

    public double nextDouble(double minimum, double maximum) {
        return random.nextDouble() * (maximum - minimum) + minimum;
    }

}
