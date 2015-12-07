package com.nbk.util.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.zxing.Result;
import com.nbk.R;
import com.nbk.custom.adapter.POSItemListAdapter;
import com.nbk.interfaces.ListViewControlsListener;
import com.nbk.interfaces.impl.POSListSwipeMenuCreator;
import com.nbk.models.Item;
import com.nbk.util.DatabaseHelper;
import com.nbk.util.ScreenManager;

public class FragmentPOSList extends Fragment implements FragmentScanner.ResultListener, ListViewControlsListener{
	
	private static Context _context;
	private SwipeMenuListView mItemList;
	private POSItemListAdapter itemsAdapter;
	
	private Button btn_sell;
	
	private DatabaseHelper dbHelper;
	
	private double totalAmountDue;

	private DecimalFormat formatter = new DecimalFormat("###,###,###.00");
	
	private FragmentScanner fragmentScanner;
	
	private int editItemPosition;
	
	private int selectedItem;
	
	@SuppressLint("HandlerLeak") 
	private Handler inputDialogHandler = new Handler(){
		
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			int status = msg.arg1;
			
			ScreenManager screenManager = ScreenManager.getInstance();
			
			screenManager.hideKeybord();
			
			switch(status){
			case ScreenManager.INPUT_DIALOG_OK:
				
				String input = data.getString(ScreenManager.INPUT_DIALOG_INPUT_VALUE);
				itemsAdapter.getItem(editItemPosition).setQuantity(Integer.parseInt(input));
				itemsAdapter.notifyDataSetChanged();
				editItemPosition = -1;
				
				updateButtonText();
//				listFrag.addItem(input);
//
				fragmentScanner.startCamera();
//				
				break;
			case ScreenManager.INPUT_DIALOG_CANCEL:
				
				fragmentScanner.startCamera();
//				
				break;				
			}	
		};
		
	};
	
	public void setScanner(FragmentScanner fs){
		this.fragmentScanner = fs;
	}
	
    public static FragmentPOSList newInstance(Context context){
    	FragmentPOSList fragment = new FragmentPOSList();
		Bundle args = new Bundle();
		fragment.setArguments(args);
				
		_context = context;
		
		return fragment;
    }
    
    public FragmentPOSList(){}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_point_of_sale_list, container, false);

		totalAmountDue = 0.0;
		editItemPosition = -1;
		selectedItem = -1;
		
        dbHelper = new DatabaseHelper();
        
        itemsAdapter = new POSItemListAdapter(_context, new ArrayList<Item>(), this);
        mItemList = (SwipeMenuListView) rootView.findViewById(R.id.nbk_items);
        btn_sell = (Button) rootView.findViewById(R.id.nbk_sell); 
        btn_sell.setEnabled(false);
        
        setupViews();
        
        return rootView;
    }
    
    private void setupViews(){
    	mItemList.setAdapter(itemsAdapter);
    	mItemList.setMenuCreator(new POSListSwipeMenuCreator(getActivity()));

        // step 2. listener item click event
    	mItemList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//                ApplicationInfo item = mAppList.get(position);

            	Item item = itemsAdapter.getItem(position);
            	
                switch (index) {
                    case 0:
                        // open
//                        open(item);
                    	editItemPosition = position;
                    	ScreenManager.getInstance().showAddMinusDialog(Integer.toString(item.getQuantity()), inputDialogHandler, "Quantity");
                    	fragmentScanner.stopCamera();

                        break;
                    case 1:
                        // delete
//					delete(item);
//                        mAppList.remove(position);
//                        mAdapter.notifyDataSetChanged();
                    	
                    	itemsAdapter.remove(position);
                    	totalAmountDue -= item.getPrice();
                    	itemsAdapter.notifyDataSetChanged();

                    	updateButtonText();
                        break;
                }
                return false;
            }
        });
    	

        // set SwipeListener
    	mItemList.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        // other setting
//		listView.setCloseInterpolator(new BounceInterpolator());

        // test item long click
    	mItemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
//                Toast.makeText(getActivity(), position + " long click", Toast.LENGTH_SHORT).show();
            	mItemList.smoothOpenMenu(position);
                return false;
            }
        });
        
        mItemList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				itemsAdapter.showControls(view);
				if (mItemList.smoothCloseMenu() == 0){
					mItemList.smoothOpenMenu(position);
				}
//				selectedItem = position;
			}
		});
        
        btn_sell.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sellItems();
			}
		});
    }
    
	@Override
	public void onCameraScanResult(Result result) {
		Item item = dbHelper.getInventoryItem(result.getText());

    	addItem(item);
	}

	public void addItem(String barcode){
    	Item item = dbHelper.getInventoryItem(barcode);

    	addItem(item);
	}
	
	private void addItem(Item item){
    	if (item != null){
    		
    		int position;
    		boolean itemExisted = false;
    		for (position = 0; position < itemsAdapter.getCount(); position++){
    			Item currentItem = itemsAdapter.getItem(position);
    			if (item.compareTo(currentItem) == 0){
    				// item is already in the list
    				currentItem.setQuantity(currentItem.getQuantity() + 1);
    				itemExisted = true;
    			}
    		}
    		
    		if (!itemExisted){
				// new item
	    		item.setQuantity(1); // initialize quantity
	    		itemsAdapter.add(item);
    		}
    		
        	itemsAdapter.notifyDataSetChanged();
        	totalAmountDue += item.getPrice();
        	updateButtonText();
    		
        	
    	}else{
        	ScreenManager.getInstance().toastLong("Item not found in database");
    	}
	}
	
	public void getItems(){
		
	}
	
	private void updateButtonText(){
		String newText = getString(R.string.btn_sell);
		totalAmountDue = itemsAdapter.getTotalAmountDue();
		if (totalAmountDue >= 0.01 || itemsAdapter.getCount() >= 1){
			// there are at least one item to sell
			btn_sell.setEnabled(true);
			btn_sell.setBackgroundColor(0xFF28AD0A);
			newText += " (Total: " + formatter.format(totalAmountDue) + ")";
		}else{
			// no items to sell
			btn_sell.setEnabled(false);			
			btn_sell.setBackgroundColor(android.R.drawable.btn_default);
		}
		btn_sell.setText(newText);
	}
	
	public void sellItems(){
		DatabaseHelper dbHelper = new DatabaseHelper();
		for (Item item : itemsAdapter.getItems()){
			dbHelper.sellItem(item, item.getQuantity());
		}
		itemsAdapter.clear();
		itemsAdapter.notifyDataSetChanged();

    	totalAmountDue = 0.0;
    	updateButtonText();
		
		Toast.makeText(_context, "Items sold", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onAction(Action action, Item item) {
    	totalAmountDue -= item.getPrice();
    	updateButtonText();
	}
}
