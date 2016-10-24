package com.winorout.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.winorout.interfaces.OnMessgaeChange;

/**
 *
 * @author  ryzhang
 * @data 2016/10/24 18:55
 */
public class MyReceiver extends android.content.BroadcastReceiver {
    private OnMessgaeChange onMessageChang;
    public void setOnMessageChang(OnMessgaeChange onMessageChang){
        this.onMessageChang=onMessageChang;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String content=intent.getStringExtra("message");
        Log.d("ryzhang","message："+content);
        if(onMessageChang!=null){
            onMessageChang.receiveMessage(content);
        }
    }
}
