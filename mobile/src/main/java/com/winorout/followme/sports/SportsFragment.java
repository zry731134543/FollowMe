package com.winorout.followme.sports;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.common.data.FreezableUtils;
import com.mobvoi.android.wearable.DataApi;
import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.Wearable;
import com.winorout.connect.MobileMessageService;
import com.winorout.followme.R;
import com.winorout.interfaces.OnMessgaeChange;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Mr_Yan on 2016/10/3.
 */

public class SportsFragment extends Fragment implements View.OnClickListener,OnMessgaeChange {

    private static final String TAG = "w";

    View view;
    CircleBar circleBar;
    TextView total_kilor;
    TextView total_cal;
    TextView total_time;
    MobileMessageService getservice;
    private int _step;
    private int _time;
    private double _calories;
    private double _kile;
    int step;
    PedometerDB db;
    Intent intentRemind;
    Message message = null;

    // 模拟 消息传入 手表
    private Button mTextBtn;
    private MobileMessageService mMobileMessageService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() 11");
        view = inflater.inflate(R.layout.sportsfragment, container, false);
        db = PedometerDB.getInstance(getActivity());
        createService();
        bindView();
        show();

        return view;
    }

    /**
     * 创建获取步数服务
     */
    private void createService() {
        // 启动获取步数服务
//        Intent intent = new Intent(getActivity(), GetSetpService.class);
		Intent intent = new Intent(getActivity(), MobileMessageService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 绑定视图
     */
    private void bindView() {
        total_kilor = (TextView) view.findViewById(R.id.total_kile);
        total_cal = (TextView) view.findViewById(R.id.total_cal);
        total_time = (TextView) view.findViewById(R.id.total_time);
        circleBar = (CircleBar) view.findViewById(R.id.circle);
        circleBar.setOnClickListener(this);
    }

    /**
     * 显示数据
     */
    private void show() {
        total_kilor.setText(_kile + "m");
        total_cal.setText(_calories + "j");
        total_time.setText(_time + "s");
        circleBar.max = step;
        circleBar.setText(_step);
        circleBar.setProgress(_step, 1);
        circleBar.startCustomAnimation();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MobileMessageService.MyBinder binder = (MobileMessageService.MyBinder) service;
            setData(binder.getMessage());
            show();
            getservice = binder.mobileMessageService;
            getservice.setOnprogressListener(new StepListencer() {
                @Override
                public void onprogress(Message msg) {
                    msg.what = 1;
                    message = msg;
                    handler.sendMessage(msg);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                setData(msg);
                show();
            }
            if (msg.what == 0) {
                show();
            }
        };
    };

    private void setData(Message msg) {
        Bundle data = msg.getData();
        _step = data.getInt("step");
        _time = data.getInt("time");
        _calories = data.getDouble("calories");
        _kile = data.getDouble("kile");
        Log.e("zryservice", "设置步数：" + step);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onResume() {
        Log.e("zryservice", "重新回调Fragment" + db.selectGoals());
        step = db.selectGoals() != null ? Integer.parseInt(db.selectGoals()) : 10000;
        handler.sendEmptyMessage(0);
        super.onResume();
    }

    @Override
    public void onStart() {
        Log.e("w", "onStart()");
        super.onStart();

    }

    @Override
    public void onDestroy() {
        getActivity().unbindService(connection);
        super.onDestroy();
    }

    /**
     * As simple wrapper around Log.e
     */
    private static void LOGD(final String tag, String message) {
        Log.e(tag, message);
    }

    @Override
    public void receiveMessage(MessageEvent messageEvent) {

    }
}
