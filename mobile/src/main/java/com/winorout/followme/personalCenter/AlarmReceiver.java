package com.winorout.followme.personalCenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class AlarmReceiver extends BroadcastReceiver {
		private Vibrator vibrator;
		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(context, "FollowMe提醒您，运动时间到了", Toast.LENGTH_LONG).show();
			vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			long[] pattern = { 100, 400, 100, 400 }; 
			vibrator.vibrate(pattern, 0);
			new Timer(true).schedule(new TimerTask(){
				@Override
				public void run() {
					vibrator.cancel();
				}
			},5000);
		}
}
