package com.winorout.followme;

import android.os.Bundle;
import android.support.wearable.view.WearableListView;

import com.winorout.adapter.FollowRVAdapter;
import com.winorout.base.BaseActivity;

public class FollowActivity extends BaseActivity {


    private WearableListView mListView;    //主界面lv

    @Override
    public void initVariables() {

    }

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_follow);

        mListView = (WearableListView) findViewById(R.id.follow_lv);
    }

    @Override
    public void loadData() {
        mListView.setAdapter(new FollowRVAdapter(this));
    }
}
