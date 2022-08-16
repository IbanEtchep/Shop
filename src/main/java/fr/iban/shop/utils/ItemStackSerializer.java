package fr.iban.shop.utils;

import org.bukkit.inventory.ItemStack;

import java.util.Base64;

public class ItemStackSerializer {

    public static String toBase64(ItemStack itemStack) {
        return Base64.getEncoder().encodeToString(itemStack.serializeAsBytes());
    }

    public static ItemStack fromBase64(String base64) {
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(base64));
    }

}
