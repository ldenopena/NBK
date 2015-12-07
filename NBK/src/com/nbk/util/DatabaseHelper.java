package com.nbk.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;

import com.nbk.models.Item;

public class DatabaseHelper {

	private SQLiteDatabase db;
	
	public static final String TABLE_INVENTORY = "inventory";
	
	private static final String DATABASE_NAME = "inventory2.db";
	private static final String DATABASE_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

	public DatabaseHelper() {
		try{
			db = SQLiteDatabase.openDatabase(DATABASE_DIR + "/" + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		}catch(SQLiteException sqliteException){
			sqliteException.printStackTrace();
			ScreenManager.getInstance().toastLong("Database not found");
		}
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}

	public int getStockCount(String barcode){
		int stock = 0;
		
		Cursor row = getInventoryRows(barcode);
		
		List<String> columnNames = Arrays.asList(row.getColumnNames());
		
		stock = row.getInt(columnNames.indexOf("stock"));
		
		return stock;
	}
	
	public Cursor getInventoryRows(String barcode){
		String tableName = DatabaseHelper.TABLE_INVENTORY;
		String[] columns = null;
		String selection = "barcode = ?";
		String[] selectionArgs = {barcode};
		String groupBy = null;
		String having = null;
		String orderBy = null;
		
		Cursor cursor = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
		
		return cursor;
	}
	
	public Cursor getInventoryRows(Item item){
		String tableName = DatabaseHelper.TABLE_INVENTORY;
		String[] columns = null;
		String selection = "id = ? AND barcode = ? AND name = ? AND price = ?";
		String[] selectionArgs = {Integer.toString(item.getId()), item.getBarcode(), item.getName(), Double.toString(item.getPrice())};
		String groupBy = null;
		String having = null;
		String orderBy = null;
		
		Cursor cursor = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
		
		return cursor;
	}
	
	public List<Item> getInventoryItems(){
		List<Item> itemList = new ArrayList<Item>();
		
		String tableName = DatabaseHelper.TABLE_INVENTORY;
		String[] columns = {"id", "barcode", "name", "price", "stock"};
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;
		
		Cursor row = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
		Item item = null;
		if (row.moveToFirst()){
			do{
				item = new Item();
				item.setId(row.getInt(row.getColumnIndex(columns[0])));
				item.setBarcode(row.getString(row.getColumnIndex(columns[1])));
				item.setName(row.getString(row.getColumnIndex(columns[2])));
				item.setPrice(row.getDouble(row.getColumnIndex(columns[3])));
				item.setQuantity(row.getInt(row.getColumnIndex(columns[4])));
				
				itemList.add(item);
			}while (row.moveToNext());
		}
//		else{
//			return null;
//		}
		
		return itemList;
	}
	
	public Item getInventoryItem(String barcode){
//		String tableName = DatabaseHelper.TABLE_INVENTORY;
//		String[] columns = null;
//		String selection = "barcode = ?";
//		String[] selectionArgs = {barcode};
//		String groupBy = null;
//		String having = null;
//		String orderBy = null;
//		
//		Cursor row = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
		
		Cursor row = getInventoryRows(barcode);
		List<String> columnNames = Arrays.asList(row.getColumnNames());
		Item item = new Item();
		
		if (row.moveToFirst()){
			do{
				item.setId(row.getInt(columnNames.indexOf("id")));
				item.setBarcode(row.getString(columnNames.indexOf("barcode")));
				item.setName(row.getString(columnNames.indexOf("name")));
				item.setPrice(row.getDouble(columnNames.indexOf("price")));
				item.setQuantity(row.getInt(columnNames.indexOf("stock")));
			}while (row.moveToNext());
		}else{
			return null;
		}
		return item;
	}
	
//	public int sellItem(String barcode, int quantity){
//		String tableName = DatabaseHelper.TABLE_INVENTORY;
//		
//		Cursor row = getInventoryRows(barcode);
//		
//		List<String> columns = Arrays.asList(row.getColumnNames());
////		int stockIndex = columns.indexOf("stock");
//		int itemsCount = row.getCount();
//		
//		int stock = 1;
//		
//		if (row.moveToFirst()){
//			do{
//				stock = row.getInt(columns.indexOf("stock"));
//			}while (row.moveToNext());
//		}
//		
//		
//		stock -= quantity;
//		
//    	ContentValues values = new ContentValues();
//    	values.put("barcode", barcode);
//    	values.put("stock", stock);
//
//		String whereClause = "barcode = ?";
//		
//		String[] whereArgs = {barcode.toString()};
//		
//		int updated = db.update(tableName, values, whereClause, whereArgs);
//		
//		return updated;
//	}

	public int sellItem(Item item, int quantity){
		String tableName = DatabaseHelper.TABLE_INVENTORY;
		
		Cursor row = getInventoryRows(item);
		
		List<String> columns = Arrays.asList(row.getColumnNames());
//		int stockIndex = columns.indexOf("stock");
//		int itemsCount = row.getCount();
		
		int stock = 1;
		
		if (row.moveToFirst()){
			do{
				stock = row.getInt(columns.indexOf("stock"));
			}while (row.moveToNext());
		}
		
		
		stock -= quantity;
		
    	ContentValues values = new ContentValues();
    	values.put("stock", stock);

		String whereClause = "id = ? AND barcode = ? AND name = ? AND price = ?";

		String[] whereArgs = {Integer.toString(item.getId()), item.getBarcode(), item.getName(), Double.toString(item.getPrice())};
		
		int updated = db.update(tableName, values, whereClause, whereArgs);
		
		return updated;
	}
	
	public long insertItem(Item item){
		boolean isSuccess = false;
		
    	ContentValues values = new ContentValues();
    	values.put("barcode", item.getBarcode());
    	values.put("name", item.getName());
    	values.put("price", item.getPrice());
    	values.put("stock", item.getQuantity());
    	
    	long rowNum = 0;
    	
    	try{
    	
    		rowNum = db.insertOrThrow(DatabaseHelper.TABLE_INVENTORY, null, values);    		
    		isSuccess = true;
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		
		return rowNum;
	}
	
	public int updateItem(Item item){
		
		String tableName = DatabaseHelper.TABLE_INVENTORY;
		
    	ContentValues values = new ContentValues();
    	values.put("barcode", item.getBarcode());
    	values.put("name", item.getName());
    	values.put("price", item.getPrice());
    	values.put("stock", item.getQuantity());

		String whereClause = "id = ?";

		String[] whereArgs = {Integer.toString(item.getId())};
//		String[] whereArgs = null;
//		String[] whereArgs = {item.getBarcode(), item.getPrice()};
		
		int affectedRows = db.update(tableName, values, whereClause, whereArgs);
		
		return affectedRows;
	}
}
