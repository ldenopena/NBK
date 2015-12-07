package com.nbk.util.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.TableLayout.LayoutParams;

import com.google.zxing.Result;
import com.nbk.R;
import com.nbk.barcodescanner.core.ZXingScannerView;


public class FragmentScanner extends Fragment implements ZXingScannerView.ResultHandler {

    public interface ResultListener {
    	public void onCameraScanResult(Result result);
    }
    
    private ZXingScannerView mScannerView;
    private static Fragment _bottomView;
    
    private static ResultListener _listener;
    
    private static Context _context;
    
    public FragmentScanner(){}

    public static FragmentScanner newInstance(Context context, ResultListener listener, Fragment bottomFragment){
    	FragmentScanner fragment = new FragmentScanner();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		
		_context = context;
		_listener = listener;
		
		if (_listener == null){
			_listener = new ResultListener(){

				@Override
				public void onCameraScanResult(Result result) {
					
				}
				
			};
		}
		_bottomView = bottomFragment;
		
		return fragment;
    }
    
    public static FragmentScanner newInstance(){
    	FragmentScanner fragment = new FragmentScanner();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		
		_listener = new ResultListener(){

			@Override
			public void onCameraScanResult(Result result) {
				
			}
			
		};
		
		return fragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	View rootView = inflater.inflate(R.layout.fragment_scanner, container, false);
    	
    	FrameLayout cameraLayout = (FrameLayout) rootView.findViewById(R.id.nbk_cameraContainer);
    	
    	mScannerView = new ZXingScannerView(_context);
		mScannerView.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					if (!mScannerView.isStarted()){
						mScannerView.startCamera();
					}
			}
		});
		
    	cameraLayout.addView(mScannerView);
    	
    	FrameLayout bottomView = (FrameLayout) rootView.findViewById(R.id.nbk_resultsContainer);
    	setupBottomView(bottomView, 0.45);
    	
    	if (_bottomView != null){
    		getFragmentManager().beginTransaction().replace(R.id.nbk_resultsContainer, _bottomView).commit();
    	}
    	return rootView;
    }
    
    private void setupBottomView(FrameLayout layout, double percentage){
    	android.view.Display display = ((android.view.WindowManager) _context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
    	FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (display.getHeight()*percentage));
    	params.gravity = Gravity.BOTTOM;
    	
    	layout.setLayoutParams(params);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
//		Toast.makeText(_context, "Captured!", Toast.LENGTH_SHORT).show();
        _listener.onCameraScanResult(rawResult);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
    
    public void stopCamera(){
        if (mScannerView.isStarted()){
            mScannerView.stopCamera();
    	}
    }
    
    public void startCamera(){
    	if (!mScannerView.isStarted()){
    		mScannerView.startCamera();
    	}
    }
    
    public boolean isStarted(){
    	return mScannerView.isStarted();
    }
}
