package com.winorout.followme.personalCenter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.winorout.followme.R;

import com.winorout.followme.personalCenter.alarmclock.AlarmActivity;
import java.util.Calendar;

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
//				Intent intent = new Intent(Exercise.this, ClockActivity.class);
//				startActivity(intent);
				Calendar currentTime = Calendar.getInstance();

			new TimePickerDialog(Exercise.this, 0, new TimePickerDialog.OnTimeSetListener() {

				public void onTimeSet(TimePicker tp, int hourOfday, int minute) {
					Intent intent = new Intent(Exercise.this, AlarmActivity.class);
					PendingIntent pi = PendingIntent.getActivity(Exercise.this, 0, intent, 0);
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(System.currentTimeMillis());

					c.set(Calendar.HOUR, hourOfday);
					c.set(Calendar.MINUTE, minute);
					c.set(Calendar.SECOND,0);
					c.set(Calendar.MILLISECOND,0);
					AlarmManager aManager = (AlarmManager) getSystemService(ALARM_SERVICE);
					aManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);

					Toast.makeText(Exercise.this, "闹铃设置成功啦", Toast.LENGTH_SHORT).show();

				}

			}, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), false).show();
			}




	});
	}

}