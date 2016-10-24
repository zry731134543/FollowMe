package com.winorout.followme;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.mobvoi.android.speech.SpeechRecognitionApi;

public class BarrageActivity extends SpeechRecognitionApi.SpeechRecogActivity{

    private static final String DEFAULT_NODE = "default_node";
    private static final String TAG = "VoiceInputActivity";
    private Button mStartVoiceBtn;
    private TextView mVoiceTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(savedInstanceState);
        initVariables();
        startService(new Intent(BarrageActivity.this,BarrageActivity.class));
    }
    public  void initView(Bundle savedInstanceState){
        setContentView(R.layout.activity_voice_input);
        ((TextView)findViewById(R.id.title_tv)).setText("弹幕");
        mStartVoiceBtn = (Button) findViewById(R.id.test_button);
        mVoiceTv = (TextView) findViewById(R.id.speak_tip);
    }

    public  void initVariables(){
        mStartVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

        mVoiceTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(mVoiceTv.getText()+"");
                mVoiceTv.setText("");
            }
        });

    }

    @Override
    public void onRecognitionSuccess(String text) {
        mVoiceTv.setText(text);
    }

    @Override
    public void onRecognitionFailed() {
        mVoiceTv.setText("识别失败");
    }

    /**
     * 通过广播发送消息到服务
     * @param content
     */
    private void sendMessage(String content){
        Log.d("ryzhang","sendMessage:"+content);
        Intent intent = new Intent();
        intent.setAction("com.winorout.followme.receiver");
        intent.putExtra("message", content);
        sendBroadcast(intent);
    }

}
