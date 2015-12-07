package com.nbk.custom.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nbk.R;
import com.nbk.custom.adapter.POSItemListAdapter.ViewHolder;
import com.nbk.models.Item;

public class InventoryItemListAdapter extends ArrayAdapter<Item> {

	private List<Item> mValues = new ArrayList<Item>();
	private LinearLayout previousControlBox = null;
	private Context mContext;
	private int[] colors = new int[] {0x40C4C4C4, 0x40E5E5E5};
	private int[] visibility = new int[] {View.GONE, View.VISIBLE};
	
	private int visibilityDelete = 1;
	
	public InventoryItemListAdapter (Context context, List<Item> values) {
		super(context, R.layout.adapter_pos_item_list, values);
		
		mValues = values;
		mContext = context;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder;
		
		if (convertView == null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.adapter_inventory_item_list, parent, false);
			
			viewHolder = new ViewHolder();
			viewHolder.textItemName = (TextView) convertView.findViewById(R.id.nbk_itemName);
			viewHolder.textBarcode = (TextView) convertView.findViewById(R.id.nbk_tv_barcode);
			viewHolder.textQuantity = (TextView) convertView.findViewById(R.id.nbk_tv_quantity);
			viewHolder.textPrice = (TextView) convertView.findViewById(R.id.nbk_tv_price);
			viewHolder.buttonRemove = (ImageButton) convertView.findViewById(R.id.nbk_adapterControlDelete);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Item item = mValues.get(position);
		
		DecimalFormat formatter = new DecimalFormat("###,###,###.00");
		
		viewHolder.textItemName.setText(item.getName());
		viewHolder.textBarcode.setText(item.getBarcode());
		viewHolder.textQuantity.setText(Integer.toString(item.getQuantity()));
		viewHolder.textPrice.setText(formatter.format(item.getPrice()));

		viewHolder.buttonRemove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mValues.remove(position);
				previousControlBox.setVisibility(View.GONE);
				previousControlBox = null;
				notifyDataSetChanged();
			}
		});
		
		convertView.setBackgroundColor(colors[position % colors.length]);
		
		return convertView;
	}

	
	static class ViewHolder{
		TextView textItemName, textBarcode, textPrice, textQuantity;
		ImageButton buttonRemove;
	}
	
	public void showControls(View v){
		
		LinearLayout containerCurrent = (LinearLayout) v.findViewById(R.id.nbk_adapterControlBox);
				
		ImageButton delete = (ImageButton) containerCurrent.findViewById(R.id.nbk_adapterControlDelete);
		delete.setVisibility(visibility[visibilityDelete]);
		
		if (containerCurrent != previousControlBox){
			if (previousControlBox !=null){
				LinearLayout containerPrevious = previousControlBox;
				containerPrevious.setVisibility(View.GONE);
			}

			containerCurrent.setVisibility(View.VISIBLE);
			previousControlBox = containerCurrent;
			
			
		    ObjectAnimator mover = ObjectAnimator.ofFloat(containerCurrent, "translationX", 200f, 0f);
		    mover.setDuration(200);
		    mover.start();
		    
		}
		
	}
	
	public void hideControls(){
		
		if (previousControlBox !=null){
			LinearLayout containerPrevious = previousControlBox;
			containerPrevious.setVisibility(View.GONE);
		}
		
	}
	
	public List<Item> getItems(){
		return mValues;
	}
}
