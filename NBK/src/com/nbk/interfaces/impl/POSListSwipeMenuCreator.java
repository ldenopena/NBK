package com.nbk.interfaces.impl;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.nbk.R;

public class POSListSwipeMenuCreator implements SwipeMenuCreator {

	private Context mContext;
	
	public POSListSwipeMenuCreator (Context context){
		this.mContext = context;
	}
	
	@Override
	public void create(SwipeMenu menu) {
		// TODO Auto-generated method stub
		/*
		  // create "open" item
        SwipeMenuItem openItem = new SwipeMenuItem(
        		mContext);
        // set item background
        openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,0xCE)));
        // set item width
        openItem.setWidth(dp2px(90));
        // set item title
        openItem.setTitle("Open");
        // set item title fontsize
        openItem.setTitleSize(18);
        // set item title font color
        openItem.setTitleColor(Color.WHITE);
        // add to menu
        menu.addMenuItem(openItem);
*/
		
        // create "add/subtract" item
        SwipeMenuItem addItem = new SwipeMenuItem(mContext);
        // set item background
        addItem.setBackground(new ColorDrawable(Color.rgb(0x30, 0xB1,0xF5)));
        // set item width
        addItem.setWidth(dp2px(90));
        // set a icon
        addItem.setIcon(R.drawable.ic_add_subtract_colored);
        // add to menu
        menu.addMenuItem(addItem);
		
        // create "delete" item
        SwipeMenuItem deleteItem = new SwipeMenuItem(mContext);
        // set item background
        deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,0x3F, 0x25)));
        // set item width
        deleteItem.setWidth(dp2px(90));
        // set a icon
        deleteItem.setIcon(R.drawable.ic_delete);
        // add to menu
        menu.addMenuItem(deleteItem);
	}

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                mContext.getResources().getDisplayMetrics());
    }
}
