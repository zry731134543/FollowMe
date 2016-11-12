package com.winorout.followme.personalCenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.winorout.followme.R;

public class Exercise extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.exercise);
		Button exercise = (Button)findViewById(R.id.add_time);
		exercise.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Exercise.this, SetTime.class);
				startActivity(intent);
			}
		});

	}
}

