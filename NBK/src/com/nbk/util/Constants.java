package com.nbk.util;

public class Constants {

	public static final int ID_WELCOME_SCREEN = 0;
	public static final int ID_POINT_OF_SALE = 1;
	public static final int ID_INVENTORY_MANAGEMENT = 2;
	public static final int ID_INVENTORY_FORM = 3;
	
	public enum Screens{
		
		Welcome(ID_WELCOME_SCREEN, "NBK Enterprises"),
		POS(ID_POINT_OF_SALE, "Point of Sale"),
		InventoryManagement(ID_INVENTORY_MANAGEMENT, "Inventory Management"),
		InventoryForm(ID_INVENTORY_FORM, "Inventory Entry Form");
		
		public int id;
		public String tag;
		
		Screens(int id, String tag){
			this.id = id;
			this.tag = tag;
		}		
		
	}
}
