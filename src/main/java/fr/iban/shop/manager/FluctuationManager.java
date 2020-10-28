package fr.iban.shop.manager;

import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.iban.shop.Shop;
import fr.iban.shop.events.ShopFluctuateEvent;

public class FluctuationManager {
	
	private ShopManager shopmanager;
	private Random random = new Random();

	
	public FluctuationManager(ShopManager shopManager) {
		this.shopmanager = shopManager;
	}

	public void fluctuate(double maxModifier) {
		for(Map<Integer, ShopItem> category : shopmanager.getShopItems().values()) {
			for(ShopItem item : category.values()) {
				if(item.getMaxStock() == 0) continue;
				
				final double modifier = item.getModifier(item.getStock());
				int add = (int) (item.getMaxStock()* nextDouble(0, maxModifier));
				if(modifier > 1) {
					item.setStock(item.getStock() + add);
				}else if (modifier < 1) {
					item.setStock(item.getStock() - add);
				}else {
					int nextint = new Random().nextInt(2);
					if(nextint == 1) {
						add *= -1;
					}
					item.setStock(item.getStock() + add);
				}
			}
		}
		Bukkit.getPluginManager().callEvent(new ShopFluctuateEvent());
	}
	
	private double modifier = 1;
	
	public void scheduleFluctuation(long period, double max) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(modifier != 1+0.05*Bukkit.getOnlinePlayers().size()) {
					modifier = 1+0.05*Bukkit.getOnlinePlayers().size();
					scheduleFluctuation(period, max);
					cancel();
				}
				fluctuate(max);
			}
		}.runTaskTimer(Shop.getInstance(), 0, (long) (period/modifier));
	}
	
	public double nextDouble(double minimum, double maximum)
	{ 
	    return random.nextDouble() * (maximum - minimum) + minimum;
	}

}
