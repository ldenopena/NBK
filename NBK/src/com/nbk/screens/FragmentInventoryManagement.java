package com.nbk.screens;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.nbk.R;
import com.nbk.custom.adapter.InventoryItemListAdapter;
import com.nbk.custom.adapter.POSItemListAdapter;
import com.nbk.custom.adapter.ViewPagerAdapter;
import com.nbk.custom.view.CustomViewPager;
import com.nbk.models.Item;
import com.nbk.util.ScreenManager;

public class FragmentInventoryManagement extends Fragment {

	private static Context _context;
	private CustomViewPager mViewPager;
	private List<Fragment> mPages;
	private ViewPagerAdapter mViewPagerAdapter;
	
	private FragmentInventoryForm inventoryForm;
	private FragmentInventoryItemsViewer inventoryList;
	
	public static FragmentInventoryManagement newInstance(Context context){
		FragmentInventoryManagement fragment = new FragmentInventoryManagement();
		Bundle args = new Bundle();
		fragment.setArguments(args);	
		
		_context = context;
				
		return fragment;
	}
	
	public FragmentInventoryManagement(){ }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_inventory_management, container, false);
		
		if (savedInstanceState != null){
			inventoryForm = (FragmentInventoryForm) getChildFragmentManager().getFragment(savedInstanceState, "inventoryForm");
		}else{
			inventoryForm = FragmentInventoryForm.newInstance(_context);
		}		
		
		inventoryList = FragmentInventoryItemsViewer.newInstance(_context, new InventoryItemClickListener());
		
		mViewPager = (CustomViewPager) rootView.findViewById(R.id.nbk_viewPager_inventory_management);
		setupPages();
		setupViewPager();
		setupViewPagerListener();
		
		return rootView;
	}

	private void setupPages(){
		mPages = new ArrayList<Fragment>();  
		mPages.add(inventoryForm);
		mPages.add(inventoryList);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
//		getChildFragmentManager().putFragment(outState, "inventoryForm", inventoryForm);
		
		
//		getChildFragmentManager().putFragment(outState, "inventoryForm", inventoryForm);
//		getChildFragmentManager().putFragment(outState, "inventoryList", inventoryList);
	}
	
	private void setupViewPager(){
		mViewPagerAdapter = new ViewPagerAdapter(_context, getChildFragmentManager(), mPages);
		mViewPagerAdapter.notifyDataSetChanged();
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setPagingEnabled(true);
		mViewPager.setCurrentItem(0);
	}
	
	private void setupViewPagerListener(){
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub

				ScreenManager.getInstance().hideKeybord();
				
				switch(position){
				case 0:
					inventoryList.getItemsAdapter().clear();
					break;
					
				case 1:
					inventoryList.populateAdapter();					
					break;
					
					default:
						break;
				}
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private class InventoryItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			InventoryItemListAdapter adapter = (InventoryItemListAdapter) parent.getAdapter();
			
			Item item = adapter.getItem(position);
			
			mViewPager.setCurrentItem(0);
			inventoryForm.openItem(item);
		}
		
	}
}
