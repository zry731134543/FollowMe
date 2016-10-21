package com.winorout.followme;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.wearable.view.WearableListView;
import android.util.Log;

import com.winorout.adapter.FollowLVAdapter;
import com.winorout.base.BaseActivity;
import com.winorout.interfaces.FollowViewInterface;
import com.winorout.services.DomStealService;

public class FollowActivity extends BaseActivity implements FollowViewInterface{


    private WearableListView mListView;    //主界面lv
    private DomStealService services;

    @Override
    public void initVariables() {
        Intent intent = new Intent(this, DomStealService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_follow);

        mListView = (WearableListView) findViewById(R.id.follow_lv);
    }

    @Override
    public void loadData() {
        mListView.setAdapter(new FollowLVAdapter(this, this));
    }

    @Override
    public void toSportActivity(Class otherActivity) {
        Intent intent = new Intent(this, otherActivity);
        startActivity(intent);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            services = ((DomStealService.DomStealBinder) service).getService();
            Log.d("wch", "service 连接成功");
            services.excute();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            services = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
