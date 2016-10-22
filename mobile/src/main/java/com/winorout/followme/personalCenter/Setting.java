package com.winorout.followme.personalCenter;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.winorout.followme.R;


public class Setting extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting);
 	
	}
	

}
