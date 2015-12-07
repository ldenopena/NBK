package com.nbk.screens;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.Result;
import com.nbk.R;
import com.nbk.custom.view.CustomViewPager;
import com.nbk.custom.view.NumberTextWatcher;
import com.nbk.models.Item;
import com.nbk.util.DatabaseHelper;
import com.nbk.util.ScreenManager;
import com.nbk.util.fragment.FragmentScanner;

public class FragmentInventoryForm extends Fragment implements FragmentScanner.ResultListener{

	private EditText et_barcode, et_name, et_price, et_stock;
	private ImageButton btn_openCamera;
	private Button btn_add_update, btn_clear;
	
	private Item currentItem;
	
	private static Context _context;
	
	private static Map<String, String> valueHolder = new HashMap<String,String>();
	private static Map<String, View> viewHolder = new HashMap<String, View>();
	
	private DatabaseHelper dbHelper;
	
	private ScreenManager screenManager;
	
	private static FragmentManager fragmentManager;
	
	DecimalFormat formatter = new DecimalFormat("###,###,###.00");
	
	@SuppressLint("HandlerLeak") 
	private Handler inputDialogHandler = new Handler(){
		
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			int status = msg.arg1;
			
			switch(status){
			case ScreenManager.INPUT_DIALOG_OK:
				
				String input = data.getString(ScreenManager.INPUT_DIALOG_INPUT_VALUE);
				Item item = dbHelper.getInventoryItem(input);
				clearFields();

				valueHolder.put("barcode", input);

//				openItem(item);
				
				if (item != null){
					/*
					// item existed in the database
					valueHolder.put("name", item.getName());
					valueHolder.put("price", item.getPrice());
					valueHolder.put("stock", Integer.toString(item.getStock()));

					et_barcode.setText(valueHolder.get("barcode"));
					et_name.setText(valueHolder.get("name"));
					et_price.setText(valueHolder.get("price"));
					et_stock.setText(valueHolder.get("stock"));
					*/
					
					openItem(item);
				}else{
					// item not existed in the database
					et_barcode.setText(valueHolder.get("barcode"));
//					Toast.makeText(_context, "Item not found", Toast.LENGTH_SHORT).show();
				}
				
				break;
			case ScreenManager.INPUT_DIALOG_CANCEL:
				
				
				break;				
			}	
		};
		
	};
	
	public static FragmentInventoryForm newInstance(Context context){

		_context = context;
		fragmentManager = ((ActionBarActivity)_context).getSupportFragmentManager();
		
		FragmentInventoryForm fragment = new FragmentInventoryForm();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_inventory_form, container, false);
		
		currentItem = new Item();

		dbHelper = new DatabaseHelper();
		
		et_barcode = (EditText) rootView.findViewById(R.id.nbk_et_barcode);
		et_name = (EditText) rootView.findViewById(R.id.nbk_et_name);
		et_price = (EditText) rootView.findViewById(R.id.nbk_et_price);
		et_stock = (EditText) rootView.findViewById(R.id.nbk_et_stock);
		btn_openCamera = (ImageButton) rootView.findViewById(R.id.nbk_openScanner);
		btn_add_update = (Button) rootView.findViewById(R.id.nbk_add_update);
		btn_clear = (Button) rootView.findViewById(R.id.nbk_btn_clear);

		viewHolder.put("et_barcode", et_barcode);
		viewHolder.put("et_name", et_name);
		viewHolder.put("et_price", et_price);
		viewHolder.put("et_stock", et_stock);
		
		screenManager = ScreenManager.getInstance();
		
		setupViews();
		
		return rootView;
	}
	
	private void setupViews(){

		btn_openCamera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				screenManager.hideKeybord();
				Fragment scanner = FragmentScanner.newInstance(_context, FragmentInventoryForm.this, null);
				fragmentManager.beginTransaction().replace(R.id.nbk_pageContainer, scanner).addToBackStack(getTag()).commit();
			}
		});
		
		btn_add_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if ((et_barcode.getText().toString() != null 	&& !et_barcode.getText().toString().equals("")) &&
					(et_name.getText().toString() != null 		&& !et_name.getText().toString().equals("")) &&
					(et_price.getText().toString() != null 				&& !et_price.getText().toString().equals("")) &&
					(et_stock.getText().toString() != null)				&& !et_stock.getText().toString().equals("")){
					
					/*
					Item item = new Item();
					item.setBarcode(et_barcode.getText().toString());
					item.setName(et_name.getText().toString());
					item.setPrice(et_price.getText().toString());
					item.setStock(Integer.parseInt(et_stock.getText().toString()));
					*/
					try {
						insertOrUpdate();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}else{
					Toast.makeText(_context, "Please complete all fields.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		btn_clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clearFields();
			}
		});
		
		et_barcode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				screenManager.showInputDialog(et_barcode.getText().toString(), inputDialogHandler, "Enter Barcode");
			}
		});

		et_price.addTextChangedListener(new NumberTextWatcher(et_price));
		et_price.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (!hasFocus){
					try {
						double n = formatter.parse(et_price.getText().toString()).doubleValue();
						et_price.setText(formatter.format(n));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	private void insertOrUpdate() throws ParseException{
//		boolean isSuccess = false;
		Item item = new Item();
		item.setId(Integer.parseInt(valueHolder.get("id")));
		item.setBarcode(et_barcode.getText().toString());
		item.setName(et_name.getText().toString());
//		item.setPrice(Double.parseDouble(formatter.format(Double.parseDouble(et_price.getText().toString()))));
		item.setPrice(formatter.parse(et_price.getText().toString()).doubleValue());
		item.setQuantity(Integer.parseInt(et_stock.getText().toString()));

//		if (dbHelper.getInventoryRows(et_barcode.getText().toString()).getCount() >= 1 ){
		if (item.getId() >= 1){
			// do update
			if (dbHelper.updateItem(item) >= 1){
	    		clearFields();
				screenManager.hideKeybord();
				Toast.makeText(_context, "Update successful", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(_context, "Update failed.", Toast.LENGTH_SHORT).show();
			}
			return;
		}
		
		if (dbHelper.insertItem(item) >= 0){
    		clearFields();
			screenManager.hideKeybord();
    		Toast.makeText(_context, "Insert successful", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(_context, "Insert failed", Toast.LENGTH_SHORT).show();
		}
    }
	
	public void openItem(Item item){
		clearFields();
		
		if (item != null){
			// item existed in the database
			valueHolder.put("id", Integer.toString(item.getId()));
			valueHolder.put("barcode", item.getBarcode());
			valueHolder.put("name", item.getName());
			valueHolder.put("price", Double.toString(item.getPrice()));
			valueHolder.put("stock", Integer.toString(item.getQuantity()));

			et_barcode.setText(valueHolder.get("barcode"));
			et_name.setText(valueHolder.get("name"));
			et_price.setText(formatter.format(Double.parseDouble(valueHolder.get("price"))));
			et_stock.setText(valueHolder.get("stock"));
			
			currentItem.setId(Integer.parseInt(valueHolder.get("id")));
			currentItem.setBarcode(valueHolder.get("barcode"));
			currentItem.setName(valueHolder.get("name"));
			currentItem.setPrice(Double.parseDouble(valueHolder.get("price")));
			currentItem.setQuantity(Integer.parseInt(valueHolder.get("stock")));
		}else{
			// item not existed in the database
			et_barcode.setText(valueHolder.get("barcode"));
			Toast.makeText(_context, "Item not found", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void clearFields(){		
		
		currentItem = new Item();
		
		valueHolder.put("id", "0");
		valueHolder.put("barcode", "");
		valueHolder.put("name", "");
		valueHolder.put("price", "");
		valueHolder.put("stock", "");
		
		et_barcode = (EditText) viewHolder.get("et_barcode");
		et_name = (EditText) viewHolder.get("et_name");
		et_price = (EditText) viewHolder.get("et_price");
		et_stock = (EditText) viewHolder.get("et_stock");
		
		et_barcode.setText("");
		et_name.setText("");
		et_price.setText("");
		et_stock.setText("");
	}
	
	@Override
	public void onResume() {
		super.onResume();
//		if (valueHolder == null){
//			valueHolder = new HashMap<String,String>();
//		}
		
		et_barcode.setText(valueHolder.get("barcode"));
		et_name.setText(valueHolder.get("name"));
		et_price.setText(valueHolder.get("price"));
		et_stock.setText(valueHolder.get("stock"));
		
	}

	@Override
	public void onCameraScanResult(Result result) {
		fragmentManager.popBackStack();
		clearFields();
		valueHolder.put("barcode", result.getText());
		
		Item item = dbHelper.getInventoryItem(result.getText());
		
//		openItem(item);
		
		if (item != null){
			valueHolder.put("name", item.getName());
			valueHolder.put("price", Double.toString(item.getPrice()));
			valueHolder.put("stock", Integer.toString(item.getQuantity()));

			openItem(item);
		}else{
			// item not existed in the database
			et_barcode.setText(valueHolder.get("barcode"));
//			Toast.makeText(_context, "Item not found", Toast.LENGTH_SHORT).show();
		}
	}
	
}
