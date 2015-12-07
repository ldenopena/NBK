package com.nbk.util;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.nbk.screens.FragmentInventoryForm;
import com.nbk.screens.FragmentInventoryManagement;
import com.nbk.screens.FragmentPointOfSaleScreen;
import com.nbk.screens.FragmentWelcomeScreen;

public class ScreenFactory {

	private Context mContext;
	
	public ScreenFactory(Context context){
		mContext = context;
	}
	
	public Fragment getScreen(int screenId){
		Fragment screen = null;
		
		switch (screenId){
		case Constants.ID_WELCOME_SCREEN:
			screen = FragmentWelcomeScreen.newInstance();
			break;
		case Constants.ID_POINT_OF_SALE:
			screen = FragmentPointOfSaleScreen.newInstance(mContext);
			break;		
		case Constants.ID_INVENTORY_FORM:
			screen = FragmentInventoryForm.newInstance(mContext);
			break;
		case Constants.ID_INVENTORY_MANAGEMENT:
			screen = FragmentInventoryManagement.newInstance(mContext);
			break;
			
		default:
			break;
		}
		
		return screen;
	}
}
