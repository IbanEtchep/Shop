package fr.iban.shop.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.iban.shop.utils.ShopAction;

public class ShopActionEvent extends Event {
	
	private ShopAction action;

    public ShopActionEvent(ShopAction action) {
		this.action = action;
	}

	private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

	public ShopAction getAction() {
		return action;
	}
}