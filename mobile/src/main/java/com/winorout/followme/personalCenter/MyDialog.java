package com.winorout.followme.personalCenter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.winorout.followme.R;


/**
 * Created by Mr-x on 2016/10/26.
 */

public class MyDialog extends AlertDialog implements View.OnClickListener {
    private Context mContext;
    private TextView mBtnOk;
    private TextView mBtnCancel;
    private Handler mHandler;
    private static final String TAG = "MyDialog";

    MyView myView;

    public MyDialog(Context context, Handler handler) {
        super(context);
        mContext = context;
        mHandler = handler;
        myView = new MyView(getContext(), null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mydialog);
        initView();
        initData();
    }

    private void initView() {
        mBtnOk = (TextView) findViewById(R.id.ok);
        mBtnCancel = (TextView) findViewById(R.id.cancel);
    }

    private void initData() {
        mBtnOk.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                doOk();
                break;
            case R.id.cancel:
                doCancel();
                break;
            default:
                break;
        }
    }

    private void doOk() {
        Message msg = new Message();
        msg.what = 0x123;
        msg.obj = myView.getCount();
        mHandler.sendMessage(msg);
        dismiss();
    }

    private void doCancel() {
        dismiss();
    }
}
