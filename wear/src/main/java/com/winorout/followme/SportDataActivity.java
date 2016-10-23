package com.winorout.followme;

import android.os.Bundle;

import com.winorout.base.BaseActivity;
import com.winorout.interfaces.OnStepChange;
import com.winorout.presenter.SensorPresenter;
import com.winorout.view.CircleProgressBar;



public class SportDataActivity extends BaseActivity {
    private CircleProgressBar mCircleBar;
    SensorPresenter sensorPresenter;

    @Override
    public void initVariables() {
        sensorPresenter = new SensorPresenter(this);
        sensorPresenter.setOnStepChange(new OnStepChange() {
            @Override
            public void getStep(int step) {
                mCircleBar.setProgressNotInUiThread(step);
            }
        });
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sportdata);
        initTopbar("数据");
        mCircleBar = (CircleProgressBar) findViewById(R.id.circleProgressbar);
        mCircleBar.setProgress(sensorPresenter.fetchSteps());
    }

    @Override
    public void loadData() {
    }

    protected void onResume() {
        sensorPresenter.registerListener();
        super.onResume();
    }

    protected void onPause() {
        sensorPresenter.unregisterListener();
        super.onPause();
    }


}
