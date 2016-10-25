package com.winorout.followme.personalCenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;


import com.winorout.followme.R;
import com.winorout.followme.sports.PedometerDB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SettingTime extends Activity implements OnClickListener, OnValueChangeListener {
	NumberPicker hours;
	NumberPicker time;
	Button ok_add;
	Button center_add;
	Button btn_repeat;
	TextView repeat_exaplain;
	Intent intent;
	String explain;//说明数据
	PedometerDB db;
	String data_time;//新提醒时间
	String oder_time;//过去提醒时间
	boolean is_insert = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_time);
		intent = getIntent();
		bindView();
		initData();
	}

	/**
	 * 绑定视图
	 */
	private void bindView() {
		hours = (NumberPicker) findViewById(R.id.hours);
		time = (NumberPicker) findViewById(R.id.time);
		ok_add = (Button) findViewById(R.id.save_add);
		center_add = (Button) findViewById(R.id.center_add);
		btn_repeat = (Button) findViewById(R.id.btn_repeat);
		repeat_exaplain = (TextView) findViewById(R.id.repeat_exaplain);
		ok_add.setOnClickListener(this);
		center_add.setOnClickListener(this);
		btn_repeat.setOnClickListener(this);
		hours.setOnValueChangedListener(this);
		time.setOnValueChangedListener(this);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		db = PedometerDB.getInstance(this);
		hours.setMaxValue(23);
		time.setMaxValue(59);
		if (intent.getStringExtra("time") == null) {
			Calendar c = Calendar.getInstance();
			hours.setValue(c.get(Calendar.HOUR_OF_DAY));
			time.setValue(c.get(Calendar.MINUTE));
			explain = "每天";
		} else {
			String[] times = intent.getStringExtra("time").split(":");
			hours.setValue(Integer.parseInt(times[0]));
			time.setValue(Integer.parseInt(times[1]));
			explain = intent.getStringExtra("date");
			is_insert = false;
		}
		data_time = hours.getValue() + ":" + time.getValue();
		oder_time = hours.getValue() + ":" + time.getValue();
		repeat_exaplain.setText(explain);
	}

	/**
	 * Button点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.save_add: {
				// 保存到数据库
				if (is_insert) {
					db.insertRemind(data_time, repeat_exaplain.getText() + "");
				} else {
					db.updateRemind(oder_time,data_time, repeat_exaplain.getText() + "");
				}
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(System.currentTimeMillis());
				int hour=hours.getValue();
				int minute=time.getValue();
				int curr_hour=calendar.get(Calendar.HOUR_OF_DAY);
				int curr_minute=calendar.get(Calendar.MINUTE);
				int s=calendar.get(Calendar.SECOND);
				Log.d("GetStepService", "提醒时间"+hour+":"+minute);
				Intent intent = new Intent(SettingTime.this, AlarmReceiver.class);
				PendingIntent pi = PendingIntent.getBroadcast(SettingTime.this, 0, intent, 0);
				if(hour>=curr_hour){
					int m=minute-curr_minute;
					int h=m<0?hour-curr_hour-1:hour-curr_hour;
					calendar.add(Calendar.HOUR, h);
					calendar.add(Calendar.MINUTE, m-1<0?0:m-1);
					calendar.add(Calendar.SECOND, 60-s);
				}
				//进行闹铃注册
				AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
				manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
				
			}
			case R.id.center_add: {
				onBackPressed();
				break;
			}
			case R.id.btn_repeat: {
				ChoiceDialog dialog = new ChoiceDialog(SettingTime.this);
				dialog.show();
				break;
			}
		}
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		data_time = hours.getValue() + ":" + time.getValue();
	}

	Handler handle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String exmplain = msg.getData().getString("exmplain");
			repeat_exaplain.setText(exmplain);
		};
	};

	/**
	 * 重复对话框
	 * 
	 * @author zhangruyi
	 *
	 */
	class ChoiceDialog extends Dialog implements OnClickListener {
		private Button btn_one;
		private Button btn_day;
		private Button btn_week;
		private List<Button> list;

		public ChoiceDialog(Context context) {
			super(context);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// 设置不显示对话框标题栏
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			// 设置对话框显示的布局文件
			setContentView(R.layout.choice_dialog);
			list = new ArrayList<Button>();
			btn_one = (Button) findViewById(R.id.btn_one);
			btn_day = (Button) findViewById(R.id.btn_day);
			btn_week = (Button) findViewById(R.id.btn_week);
			list.add(btn_one);
			list.add(btn_day);
			list.add(btn_week);
			btn_one.setOnClickListener(this);
			btn_day.setOnClickListener(this);
			btn_week.setOnClickListener(this);
			initColor();
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public void onClick(View v) {
			setColor(v.getId());
			Bundle data = new Bundle();
			data.putString("exmplain",explain );
			Message msg = new Message();
			msg.setData(data);
			handle.sendMessage(msg);
		}

		/**
		 * 初始化颜色
		 */
		private void initColor() {
			for (Button btn : list) {
				if (explain.equals(btn.getText())) {
					btn.setTextColor(Color.RED);
					return;
				}
			}
		}

		/**
		 * 设置颜色
		 */
		@SuppressLint("ResourceAsColor")
		private void setColor(int id) {
			String exmplain = null;
			for (Button btn : list) {
				if (btn.getId() == id) {
					btn.setTextColor(Color.RED);
					explain = btn.getText().toString();
				} else {
					btn.setTextColor(Color.argb(255, 206, 206, 206));
				}
			}
		}

	}
}
