package com.winorout.connect;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.mobvoi.android.wearable.MessageEvent;
import com.winorout.entity.SportData;
import com.winorout.followme.sports.DateTimeData;
import com.winorout.followme.sports.PedometerDB;
import com.winorout.tools.Logg;
import com.winorout.tools.Tools;

import java.text.DateFormat;
import java.text.DecimalFormat;


/**
 * 过滤MessageEvent,通过广播发送到指定界面
 * @author  ryzhang
 * @data 2016/10/24 20:14
 */
public class MessageEventFilter {
    private static final String SPORT_PATH = "/sport";
    private static final String BARRAGE_PATH="/barrage";
    private static final String RECEIVER="com.winorout.followme.sport";

    private MessageEvent messageEvent;
    private Context context;
    public MessageEventFilter(MessageEvent messageEvent,Context context){
        this.messageEvent=messageEvent;
        this.context=context;
    }

    /**
     * 通过广播发送给指定的界面
     */
    public void sendMessage(){
        Logg.d(messageEvent.getPath());
        switch (messageEvent.getPath()){
            case SPORT_PATH:
                Logg.d("益动数据");
                sendSport();
                break;
            case BARRAGE_PATH:
                Logg.d("弹幕数据");
                sendBararge();
                break;
        }
    }

    /**
     * 发送到益动界面
     */
    private void sendSport(){
        String json =new String(messageEvent.getData());
        Logg.d("运动数据："+json);
        Gson gson=new Gson();
        SportData sportData=gson.fromJson(json,SportData.class);//解析数据
        saveDB(sportData);
    }

    /**
     * 发送到弹幕界面
     */
    private void sendBararge(){
        String content= new String(messageEvent.getData());
        Logg.d("弹幕："+content);
    }

    /**
     * 保存运动数据到数据库
     */
    private void saveDB(SportData sportData){
        PedometerDB db=PedometerDB.getInstance(context);
        DateTimeData dateTimeData=new DateTimeData(Tools.getDate(),0,sportData.getStep()
                ,getCalories(sportData.getStep()),sportData.getDistance());
        db.insertGoals(sportData.getGoal());//保存目标步数
        db.insertNow(dateTimeData);
        sendBroadcast(RECEIVER,dateTimeData);
    }

    /**
     * 计算当前运动生成的热量
     *
     * @return 热量
     */
    public double getCalories(int step) {
        double c = 65 * step * 0.7 / 100;
        return Double.parseDouble(new DecimalFormat("#.0").format(c));
    }

    /**
     *  发送广播
     * @param action
     */
    private void sendBroadcast(String action,DateTimeData dateTimeData){
        Intent intent=new Intent();
        intent.putExtra("sport",dateTimeData);
        intent.setAction(action);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
