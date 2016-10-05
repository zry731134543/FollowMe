package com.winorout.followme;

import android.os.Bundle;
import android.support.wearable.view.WearableRecyclerView;

import com.winorout.adapter.FollowRVAdapter;
import com.winorout.base.BaseActivity;

public class FollowActivity extends BaseActivity {

    private WearableRecyclerView mRecyclerView;   //主界面rv

    @Override
    public void initVariables() {

    }

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_follow);

        mRecyclerView = (WearableRecyclerView) findViewById(R.id.follow_rv);
    }

    @Override
    public void loadData() {
        mRecyclerView.setAdapter(new FollowRVAdapter(this));
    }
}
