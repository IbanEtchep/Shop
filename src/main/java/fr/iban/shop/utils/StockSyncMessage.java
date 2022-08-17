package fr.iban.shop.utils;

public class StockSyncMessage {

    private int shopID;
    private int newStock;

    public StockSyncMessage(int shopID, int newStock) {
        this.shopID = shopID;
        this.newStock = newStock;
    }

    public int getShopID() {
        return shopID;
    }

    public int getNewStock() {
        return newStock;
    }
}
