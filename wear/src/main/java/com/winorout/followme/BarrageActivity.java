package com.winorout.followme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.winorout.base.BaseActivity;

/**
 * Created by xwangch on 16/10/5.
 */

public class BarrageActivity extends BaseActivity {
    private Button mStartVoiceBtn;
    private TextView mVoiceTv;

    @Override
    public void initVariables() {
        Intent voiceIntent = new Intent(this, VoiceInputActivity.class);
        voiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(voiceIntent);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_barrage);
        initTopbar("弹幕");
        mStartVoiceBtn = (Button) findViewById(R.id.test_button);
        mVoiceTv = (TextView) findViewById(R.id.speak_tip);
    }

    @Override
    public void loadData() {

    }
}
