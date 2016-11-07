package com.winorout.followme.personalCenter;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import com.winorout.followme.R;

public class Exercise extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.exercise);
	}
}

