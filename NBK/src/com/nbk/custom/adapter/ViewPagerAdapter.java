package com.nbk.custom.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerAdapter extends FragmentPagerAdapter {
	
	private Context mContext;
	private List<Fragment> fragmentList;
	
	public ViewPagerAdapter(Context context, FragmentManager fm) {
		super(fm);	
		mContext=context;		
		fragmentList = new ArrayList<Fragment>();
	}	
	
	public ViewPagerAdapter(Context context, FragmentManager fm, List<Fragment> pageList) {
		super(fm);	
		mContext=context;
		fragmentList = pageList;
	}
	
	public void setFragmentList(List<Fragment> fragmentList) {
		this.fragmentList = fragmentList;
	}

	@Override
	public Fragment getItem(int position) {
		
		Fragment f = new Fragment();				
		f = fragmentList.get(position);
		
		return f;
	}
	@Override
	public int getCount() {
		return fragmentList.size();
	}
	
}
