package fr.iban.shop.utils;

public enum ShopAction {
	
	BUY("Acheter"),
	SELL("Vendre");
	
	private String string;
	
	private ShopAction(String string) {
		this.string = string;
	}

	public String getString() {
		return string;
	}


}
