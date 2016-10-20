package com.winorout.followme;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.winorout.base.BaseActivity;
import com.winorout.interfaces.DomStealViewInterface;
import com.winorout.presenter.DomStealPresenter;

/**
 * Created by xwangch on 16/10/5.
 */

public class DomStealActivity extends BaseActivity implements DomStealViewInterface{

    private ImageView phoneIv;
    private ImageView watchIv;
    private ImageView connectionIv;

    @Override
    public void initVariables() {

    }

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_domsteal);
        initTopbar("防盗");

        phoneIv = (ImageView) findViewById(R.id.phone_iv);
        watchIv = (ImageView) findViewById(R.id.watch_iv);
        connectionIv = (ImageView) findViewById(R.id.connection_iv);
    }

    @Override
    public void loadData() {
        new DomStealPresenter(this, this);
    }

    @Override
    public void connection() {
        phoneIv.setImageResource(R.drawable.connect_phone);
        watchIv.setImageResource(R.drawable.connect_watch);
        connectionIv.setImageResource(R.drawable.connection);
    }

    @Override
    public void disconnection() {
        phoneIv.setImageResource(R.drawable.disconnect_phone);
        watchIv.setImageResource(R.drawable.disconnect_watch);
        connectionIv.setImageResource(R.drawable.disconnection);
    }
}
