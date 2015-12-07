package com.nbk.util;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nbk.R;
import com.nbk.util.Constants.Screens;

public class ScreenManager {

	public static final String INPUT_DIALOG_INPUT_VALUE = "input_value";
	public static final int INPUT_DIALOG_OK = 1;
	public static final int INPUT_DIALOG_CANCEL = 2;
	
	private static volatile ScreenManager sInstance = null;
	
	private Context mContext;
	private ScreenFactory mScreenFactory;
	private DrawerLayout mDrawerLayout;
	private List<Integer> screenStack = new ArrayList<Integer>();
	
	private Fragment mCurrentScreen;
	private String mCurrentScreenTag;
	
	private ScreenManager(){}
	
	public static ScreenManager getInstance() {

		ScreenManager instance = sInstance;
		if (instance == null) {
			synchronized (ScreenManager.class) {
				instance = sInstance = new ScreenManager();
			}

		}

		return instance;
	}
	
	public void setup(Context context, DrawerLayout drawerLayout) {
		
		this.mContext = context;		
		this.mScreenFactory = new ScreenFactory(context);
		this.mDrawerLayout = drawerLayout;
		
	}

	public void clearScreenStack(Screens screen){
		screenStack.clear();
		
		Fragment frag 	= mCurrentScreen = mScreenFactory.getScreen(screen.id);
		
		screenStack.add(screen.id);
		mCurrentScreenTag = screen.tag;
		
		((ActionBarActivity)mContext).getSupportFragmentManager().
						beginTransaction().replace(R.id.nbk_pageContainer, frag).commit();
	}
	
	public void replaceScreen(final Screens screen){
		hideKeybord();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub

				screenStack.remove(screenStack.size() - 1);
				addScreen(screen.id);
				mCurrentScreenTag = screen.tag;
				mDrawerLayout.closeDrawers();
			}
		}, 500);
	}
	
	public void addScreen(final int screenId){
		final Fragment frag = mCurrentScreen = mScreenFactory.getScreen(screenId);

		screenStack.add(screenId);

		((ActionBarActivity)mContext).getSupportFragmentManager().
						beginTransaction().replace(R.id.nbk_pageContainer, frag).commit();
	}
	
	public Fragment getCurrentScreen(){
		return mCurrentScreen;
	}
	
	public String getCurrentScreenTag(){
		return mCurrentScreenTag;
	}
	

    public void hideKeybord() {
    	final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mCurrentScreen.getView().getWindowToken(), 0);
    }
    
    public void showKeybord() {
    	final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(mCurrentScreen.getView().getWindowToken(), 0);
    }
    
    public void toastShort(String text){
    	Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }
    
    public void toastLong(String text){
    	Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
    }
    
    public void showAddMinusDialog(String initString, final Handler handler, String title){
    	LayoutInflater li = LayoutInflater.from(mContext);
		View promptsView = li.inflate(R.layout.input_dialog_add_minus, null);
		 
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView.findViewById(R.id.nbk_et_input_pos_quantity);
		userInput.setText(initString);

		OnClickListener buttonsListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int buttonId = v.getId();
				int currentCount = Integer.parseInt(userInput.getText().toString());
				
				switch(buttonId){
				case R.id.nbk_btn_plus:

					userInput.setText(Integer.toString(currentCount + 1));
					
					break;
					
				case R.id.nbk_btn_minus:

					int temp = currentCount - 1;
					
					if (temp <= 0){
						temp = 1;
					}
					
					userInput.setText(Integer.toString(temp));
					
					break;
					
				}
			}
		};
		
		final ImageButton add = (ImageButton) promptsView.findViewById(R.id.nbk_btn_plus);
		add.setOnClickListener(buttonsListener);

		final ImageButton minus = (ImageButton) promptsView.findViewById(R.id.nbk_btn_minus);
		minus.setOnClickListener(buttonsListener);
		
		// set dialog message
		alertDialogBuilder
			.setTitle(title)
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				// get user input and set it to result
				// edit text
			    	String input = userInput.getText().toString();
			    	
			    	Message msg = new Message();
			    	msg.arg1 = INPUT_DIALOG_OK;
			    	Bundle args = new Bundle();
			    	args.putString(INPUT_DIALOG_INPUT_VALUE, input);
			    	msg.setData(args);
			    	handler.sendMessage(msg);
//					Item item = dbHelper.getInventoryItem(input);
//					clearFields();
//
//					valueHolder.put("barcode", input);
//					if (item != null){
//						// item existed in the database
//						valueHolder.put("name", item.getName());
//						valueHolder.put("price", item.getPrice());
//						valueHolder.put("stock", Integer.toString(item.getStock()));
//
//						et_barcode.setText(valueHolder.get("barcode"));
//						et_name.setText(valueHolder.get("name"));
//						et_price.setText(valueHolder.get("price"));
//						et_stock.setText(valueHolder.get("stock"));
//					}else{
//						// item not existed in the database
//						et_barcode.setText(valueHolder.get("barcode"));
//						Toast.makeText(_context, "Item not found", Toast.LENGTH_SHORT).show();
//					}
			    }
			  })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    	Message msg = new Message();
			    	msg.arg1 = INPUT_DIALOG_CANCEL;
			    	handler.sendMessage(msg);
			    	dialog.cancel();
			    }
			  });

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
//		showKeybord();
		hideKeybord();
    }
    
    public void showInputDialog(String initString, final Handler handler, String title){
    	LayoutInflater li = LayoutInflater.from(mContext);
		View promptsView = li.inflate(R.layout.input_dialog_view, null);
		 
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView.findViewById(R.id.nbk_dialog_input);
		userInput.setText(initString);
		
		// set dialog message
		alertDialogBuilder
			.setTitle(title)
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				// get user input and set it to result
				// edit text
			    	String input = userInput.getText().toString();
			    	
			    	Message msg = new Message();
			    	msg.arg1 = INPUT_DIALOG_OK;
			    	Bundle args = new Bundle();
			    	args.putString(INPUT_DIALOG_INPUT_VALUE, input);
			    	msg.setData(args);
			    	handler.sendMessage(msg);
//					Item item = dbHelper.getInventoryItem(input);
//					clearFields();
//
//					valueHolder.put("barcode", input);
//					if (item != null){
//						// item existed in the database
//						valueHolder.put("name", item.getName());
//						valueHolder.put("price", item.getPrice());
//						valueHolder.put("stock", Integer.toString(item.getStock()));
//
//						et_barcode.setText(valueHolder.get("barcode"));
//						et_name.setText(valueHolder.get("name"));
//						et_price.setText(valueHolder.get("price"));
//						et_stock.setText(valueHolder.get("stock"));
//					}else{
//						// item not existed in the database
//						et_barcode.setText(valueHolder.get("barcode"));
//						Toast.makeText(_context, "Item not found", Toast.LENGTH_SHORT).show();
//					}
			    }
			  })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    	Message msg = new Message();
			    	msg.arg1 = INPUT_DIALOG_CANCEL;
			    	handler.sendMessage(msg);
			    	dialog.cancel();
			    }
			  });

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
		showKeybord();
    }
    
}
