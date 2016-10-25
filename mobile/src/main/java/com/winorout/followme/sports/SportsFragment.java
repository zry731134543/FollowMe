package com.winorout.followme.sports;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.winorout.followme.R;
import com.winorout.tools.Logg;

/**
 * Created by Mr_Yan on 2016/10/3.
 */

public class SportsFragment extends Fragment{
    private static final String RECEIVER="com.winorout.followme.sport";
    View view;
    CircleBar circleBar;
    TextView total_kilor;
    TextView total_cal;
    TextView total_time;
    int goal=10000;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sportsfragment, container, false);
        bindView();
        init();
        getData();
        registerReceiver();
        return view;
    }

    /**
     * 绑定视图
     */
    private void bindView() {
        total_kilor = (TextView) view.findViewById(R.id.total_kile);
        total_cal = (TextView) view.findViewById(R.id.total_cal);
        total_time = (TextView) view.findViewById(R.id.total_time);
        circleBar = (CircleBar) view.findViewById(R.id.circle);
        circleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleBar.startCustomAnimation();
            }
        });
    }
    private void init(){
        setCircleBar(1000);
    }

    @Override
    public void onResume() {
        getData();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
        super.onDestroy();
    }

    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logg.d("onReceive");
            DateTimeData dateTimeData=(DateTimeData)intent.getSerializableExtra("sport");
            int step=dateTimeData.step;
            setCircleBar(step);
            showView(dateTimeData);
        }
    };

    /**
     * 注册广播
     */
    private void registerReceiver(){
        IntentFilter intentFilter=new IntentFilter(RECEIVER);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,intentFilter);
    }

    private void getData(){
        PedometerDB db=PedometerDB.getInstance(getContext());
        goal= db.selectGoals();
        DateTimeData dateTimeData=db.queryNow();
        int step=dateTimeData.step;
        setCircleBar(step);
        showView(dateTimeData);
    }

    private void setCircleBar(int step){
        circleBar.max = goal;
        circleBar.setText(step);
        circleBar.startCustomAnimation();
    }

    private void showView(DateTimeData dateTimeData){
        total_kilor.setText(dateTimeData.kilometer/1000+"m");
        total_cal.setText(dateTimeData.calorimetry/1000+"kj");
        total_time.setText(dateTimeData.time+"s");
    }
}
