package com.winorout.connect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

/**
 *动态注册本地广播
 * @author  ryzhang
 * @data 2016/10/25 16:41
 */
public class RegisterReceiver {
    private Context context;
    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager localBroadcastManager;
    public RegisterReceiver(Context context,BroadcastReceiver broadcastReceiver){
        this.context=context;
        this.broadcastReceiver=broadcastReceiver;
    }
    public void sendMessage(){

    }


}
