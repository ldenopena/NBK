package com.nbk.screens;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.zxing.Result;
import com.nbk.R;
import com.nbk.custom.adapter.ViewPagerAdapter;
import com.nbk.custom.view.CustomViewPager;
import com.nbk.util.ScreenManager;
import com.nbk.util.fragment.FragmentPOSList;
import com.nbk.util.fragment.FragmentScanner;

public class FragmentPointOfSaleScreen extends Fragment implements FragmentScanner.ResultListener{

	private List<Fragment> mPages;
	private CustomViewPager mViewPager;
	private ViewPagerAdapter mViewPagerAdapter;
	private ImageButton imgBtn_toggleInputMethod;
	
	private FragmentScanner scannerFrag;
	private FragmentPOSList listFrag;
	
	private static Context _context;
	
	private ScreenManager screenManager;
	
	@SuppressLint("HandlerLeak") 
	private Handler inputDialogHandler = new Handler(){
		
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			int status = msg.arg1;
			
			screenManager.hideKeybord();
			
			switch(status){
			case ScreenManager.INPUT_DIALOG_OK:
				
				String input = data.getString(ScreenManager.INPUT_DIALOG_INPUT_VALUE);
				listFrag.addItem(input);

				scannerFrag.startCamera();
				
				break;
			case ScreenManager.INPUT_DIALOG_CANCEL:
				
				scannerFrag.startCamera();
				
				break;				
			}	
		};
		
	};
	
	public static FragmentPointOfSaleScreen newInstance(Context context){
		FragmentPointOfSaleScreen fragment = new FragmentPointOfSaleScreen();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		
		_context = context;
		
		return fragment;
	}
	
	public FragmentPointOfSaleScreen(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_point_of_sale, container, false);
		screenManager = ScreenManager.getInstance();
		
		listFrag = FragmentPOSList.newInstance(_context);
		scannerFrag = FragmentScanner.newInstance(_context, listFrag, listFrag);
		listFrag.setScanner(scannerFrag);
		
		imgBtn_toggleInputMethod = (ImageButton) rootView.findViewById(R.id.nbk_imgbtn_toggle_input_method);		
		imgBtn_toggleInputMethod.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				// avoid resizing or moving the layout on softkeyboard show
				((Activity) _context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
				
				if (scannerFrag.isStarted()){// camera is on
					
					scannerFrag.stopCamera();
					screenManager.showInputDialog("", inputDialogHandler, "Enter Barcode");
				}
			}
		});
		
		mViewPager = (CustomViewPager) rootView.findViewById(R.id.nbk_viewPager);
		setupPages();
		setupViewPager();
		setupViewPagerListener();
		
		return rootView;
	}
	
	private void setupPages(){
		mPages = new ArrayList<Fragment>();
		mPages.add(scannerFrag);
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
	
	@Override
	public void onCameraScanResult(Result result) {
		// TODO Auto-generated method stub
		Toast.makeText(_context, "Captured!", Toast.LENGTH_SHORT).show();
		listFrag.addItem(result.getText());
	}
	
}
