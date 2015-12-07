package com.nbk.screens;

import com.nbk.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentWelcomeScreen extends Fragment{

	public static FragmentWelcomeScreen newInstance(){
		FragmentWelcomeScreen fragment = new FragmentWelcomeScreen();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}
	
	public FragmentWelcomeScreen(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_welcome, container, false);
//		return super.onCreateView(inflater, container, savedInstanceState);
	}
}
