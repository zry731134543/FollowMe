package com.winorout.connect;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Wearable;
import com.winorout.followme.sports.DateTimeData;
import com.winorout.followme.sports.GetSetpService;
import com.winorout.followme.sports.PedometerDB;
import com.winorout.followme.sports.StepListencer;
import com.winorout.interfaces.OnMessgaeChange;
import com.winorout.tools.Logg;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 手机端接收消息的Service
 * @author  ryzhang
 * @data 2016/10/20 16:34
 */

public class MobileMessageService extends Service implements MobvoiApiClient.ConnectionCallbacks
        ,MessageApi.MessageListener,MobvoiApiClient.OnConnectionFailedListener{
    private static final String START_ACTIVITY_PATH = "/step_count";
    private static final String TAG="ryzhang";
    private OnMessgaeChange onMessgaeChange;
    private MobvoiApiClient mMobvoiApiClient;

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

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder(MobileMessageService.this);
    }

    public class MyBinder extends Binder {
        public MobileMessageService mobileMessageService;

        public MyBinder(MobileMessageService mobileMessageService){
            this.mobileMessageService = mobileMessageService;
        }
        public Message getMessage(){
            setMessage();
            return msg;
        }
    }
    /**
     * 注册接口的方法，供外部调用
     *
     * @param progress
     */
    public void setOnprogressListener(StepListencer progress) {
        this.progress = progress;
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
    /**
     * 设置初始数据
     */
    private void setInitData() {
        database = PedometerDB.getInstance(MobileMessageService.this);
        DateTimeData data = database.queryNow(new DateTimeData());
        total_step = data.step;
        total_time = data.time;
        height = 1.8;
        weight = 70;
        stride = 0.8;
        Log.d("GetStepService", "初始化总步数：" + total_step);
    }
    @Override
    public void onCreate() {
        new Thread() {
            @Override
            public void run() {
                // 设置初始化数据
                setInitData();
                // 检测是否停止运动
                onStopListencer();
            }
        }.start();
        mMobvoiApiClient = new MobvoiApiClient.Builder(MobileMessageService.this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mMobvoiApiClient.connect();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
        mMobvoiApiClient.disconnect();
        super.onDestroy();
    }

    /**
     * 设置接收消息的接口
     * @param onMessgaeChange
     */
    public void setOnMessgaeChange(OnMessgaeChange onMessgaeChange){
        this.onMessgaeChange=onMessgaeChange;
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
    @Override
    public void onConnected(Bundle bundle) {
    Wearable.MessageApi.addListener(mMobvoiApiClient, this);
    Log.d(TAG, "连接成功");
}

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "连接中断");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "连接失败");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived");
        if(START_ACTIVITY_PATH.equals(messageEvent.getPath())){
            total_step=Integer.parseInt(new String(messageEvent.getData()));
            older_time = System.currentTimeMillis();
            Log.d("GetStepService", "总步数：" + total_step);
            // 当数据变化时通知activity
            if (progress != null) {
                // 给message对象赋值
                setMessage();
                progress.onprogress(msg);
            }
//            Logg.d("getData:"+new String(messageEvent.getData()));
//            int step=Integer.parseInt(new String(messageEvent.getData()));
        }
    }
}
