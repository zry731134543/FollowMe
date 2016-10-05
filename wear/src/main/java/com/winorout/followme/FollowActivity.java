package com.winorout.followme;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;

import com.winorout.adapter.FollowLVAdapter;
import com.winorout.base.BaseActivity;
import com.winorout.interfaces.FollowViewInterface;

public class FollowActivity extends BaseActivity implements FollowViewInterface{


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
        mListView.setAdapter(new FollowLVAdapter(this, this));
    }

    @Override
    public void toSportActivity(Class otherActivity) {
        Intent intent = new Intent(this, otherActivity);
        startActivity(intent);
    }
}
