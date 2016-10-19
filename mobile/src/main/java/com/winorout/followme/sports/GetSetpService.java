package com.winorout.followme.sports;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GetSetpService extends Service {
	private SensorManager sensorManager;
	private StepDetector stepDetector;
	// 用于更新步数的接口
	private StepListencer progress;
	private Message msg;
	int total_step = 0;// 总步数
	int total_time = 1;// 总时间
	long older_time = 0;// 上一次检测到运动的时间
	long current_time = 0;// 当前的时间
	double height;// 身高
	double weight;// 体重
	double stride;// 步幅
	PedometerDB database;// 数据库
	LocalBinder local;// 用于返回给activity的对象

	/**
	 * 注册接口的方法，供外部调用
	 * 
	 * @param progress
	 */
	public void setOnprogressListener(StepListencer progress) {
		this.progress = progress;
	}

	@Override
	public IBinder onBind(Intent intent) {
		local = new LocalBinder();
		Log.d("GetStepService", "返回local");
		return local;
	}

	@Override
	public void onCreate() {
		Log.d("GetStepService", "创建服务");
		super.onCreate();
		new Thread() {
			@Override
			public void run() {
				// 设置初始化数据
				setInitData();
				// 开启步数检测器
				startStepDetector();
				// 检测是否停止运动
				onStopListencer();
			}
		}.start();
	}

	/**
	 * 监听运动是否停止的方法
	 */
	private void onStopListencer() {
		new Timer(true).schedule(new TimerTask() {
			@Override
			public void run() {
				current_time = System.currentTimeMillis();
				if(older_time!=0&&current_time-older_time>5000){
					total_time-=5;
					setMessage();
					database.insertNow(new DateTimeData(msg,getDate()));
					Log.d("GetStepService", "保存数据到数据库:"+total_time);
					older_time=0;
				}
				if(current_time-older_time<=5000&&current_time-older_time>0){
					total_time++;
				}
			}
		}, 0, 1000);
	}

	/**
	 * 获取传感器数据
	 */
	private void startStepDetector() {
		Log.d("GetStepService", "获取传感器实例");
		stepDetector = new StepDetector(this);
		// 获取传感器管理器的实例
		sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		// 获得传感器的类型，这里获得的类型是加速度传感器
		// 此方法用来注册，只有注册过才会生效，参数：SensorEventListener的实例，Sensor的实例，更新速率
		Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_UI);
		stepDetector.setOnSensorChangeListener(new StepDetector.OnSensorChangeListener() {
			@Override
			public void onChange() {
				total_step++;
				older_time = System.currentTimeMillis();
				Log.d("GetStepService", "总步数：" + total_step);
				// 当数据变化时通知activity
				if (progress != null) {
					// 给message对象赋值
					setMessage();
					progress.onprogress(msg);
				}
			}
		});
	}

	/**
	 * 设置Message对象中的数据
	 */
	private void setMessage() {
		msg = new Message();
		Bundle data = new Bundle();
		data.putInt("step", total_step);
		data.putInt("time", total_time);
		data.putDouble("calories", getCalories());
		data.putDouble("kile", getKil());
		msg.setData(data);
	}

	/**
	 * 设置初始数据
	 */
	private void setInitData() {
		database = PedometerDB.getInstance(GetSetpService.this);
		DateTimeData data = database.queryNow(new DateTimeData());
		total_step = data.step;
		total_time = data.time;
		height = 1.8;
		weight = 70;
		stride = 0.8;
		Log.d("GetStepService", "初始化总步数：" + total_step);
	}

	/**
	 * 计算当前运动生成的热量
	 * 
	 * @return 热量
	 */
	public double getCalories() {
		double c = weight * total_step * stride / 100;
		return Double.parseDouble(new DecimalFormat("#.0").format(c));
	}

	/**
	 * 计算当前里程
	 * 
	 * @return 里程
	 */
	private double getKil() {
		double k = total_step * stride;
		return Double.parseDouble(new DecimalFormat("#.0").format(k));
	}

	/**
	 * 计算当前时间
	 * 
	 * @return 时间
	 */
	public String getDate() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(now);
		return time;
	}

	public class LocalBinder extends Binder {
		public GetSetpService getservice() {
			return GetSetpService.this;
		}
		public Message getMessage(){
			setMessage();
			return msg;
		}
	}

}
