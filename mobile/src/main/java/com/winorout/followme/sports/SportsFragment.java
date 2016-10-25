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
    }

    @Override
    public void onResume() {
        Logg.d("SportsFragment");
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
            DateTimeData dateTimeData=(DateTimeData)intent.getSerializableExtra("sport");
            setCircleBar(dateTimeData.getStep());
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
        goal=goal==0?10000:goal;
        DateTimeData data=db.queryNow();
        Logg.d("startTime:"+data.getDate());
        Logg.d("totalTime:"+data.getTime());
        Logg.d("totalStep:"+data.getStep());
        Logg.d("totalKilometer:"+data.getKilometer());
        Logg.d("totalCalorimetry:"+data.getCalorimetry());
        setCircleBar(data.getStep());
        showView(data);
    }

    private void setCircleBar(int step){
        circleBar.max = goal;
        circleBar.setText(step);
        circleBar.startCustomAnimation();
    }

    private void showView(DateTimeData dateTimeData){
        total_kilor.setText(dateTimeData.getKilometer()+"m");
        total_cal.setText(dateTimeData.getCalorimetry()+"j");
        total_time.setText(dateTimeData.getTime()+"s");
    }
}
