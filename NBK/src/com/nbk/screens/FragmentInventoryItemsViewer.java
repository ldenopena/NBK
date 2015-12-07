package com.nbk.screens;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.nbk.R;
import com.nbk.custom.adapter.InventoryItemListAdapter;
import com.nbk.custom.adapter.POSItemListAdapter;
import com.nbk.models.Item;
import com.nbk.util.DatabaseHelper;

public class FragmentInventoryItemsViewer extends Fragment{

	private static Context _context;
	private static OnItemClickListener _listener;
	private ListView lv_items;
	private static InventoryItemListAdapter itemsAdapter;
	private static DatabaseHelper dbHelper = new DatabaseHelper();
	
	public static FragmentInventoryItemsViewer newInstance(Context context, OnItemClickListener listener){
		FragmentInventoryItemsViewer fragment = new FragmentInventoryItemsViewer();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		
		_context = context;
		_listener = listener;
		
		itemsAdapter = new InventoryItemListAdapter(_context, new ArrayList<Item>());
		
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_inventory_item_list, container, false);
		
		lv_items = (ListView) rootView.findViewById(R.id.nbk_lv_items_list);
		lv_items.setOnItemClickListener(_listener);
		lv_items.setAdapter(itemsAdapter);

		populateAdapter();
		
		return rootView;
	}
	
	public InventoryItemListAdapter getItemsAdapter() {
		return itemsAdapter;
	}

	public void populateAdapter(){
		List<Item> items = dbHelper.getInventoryItems();
		
		itemsAdapter.clear();
		itemsAdapter.notifyDataSetChanged();
		
		if (items != null) {
			itemsAdapter.addAll(items);
			itemsAdapter.notifyDataSetChanged();
		}
	}	
}
