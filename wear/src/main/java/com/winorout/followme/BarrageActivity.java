package com.winorout.followme;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.speech.VoiceRecognitionService;
import com.mobvoi.android.speech.SpeechRecognitionApi;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class BarrageActivity extends SpeechRecognitionApi.SpeechRecogActivity implements RecognitionListener {

    public static final String SERVIC_ACTION = "com.aa.START";
    private Button mStartVoiceBtn;
    private TextView mVoiceTv;

    private String[] listItems={"问问语音","百度语音"};

    private static final String TAG = "qmyan";
    private static final int REQUEST_UI = 1;
    public static final int STATUS_None = 0;
    public static final int STATUS_WaitingReady = 2;
    public static final int STATUS_Ready = 3;
    public static final int STATUS_Speaking = 4;
    public static final int STATUS_Recognition = 5;
    private SpeechRecognizer speechRecognizer;  //语音识别器
    private int status = STATUS_None;
    private long speechEndTime = -1;
    private static final int EVENT_ERROR = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_input);
        initView();
        initVariables();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVoiceTv.setText("FolloMe\n点击文字发送弹幕");
        //弹出对话框选择语音识别方式
//        AlertDialog dialog = new AlertDialog.Builder(BarrageActivity.this)
//                .setTitle("请选择您喜欢的语音识别方式")
//                .setSingleChoiceItems(listItems, 0, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == 0) {
//                            mStartVoiceBtn.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    // 按下按钮，启动语音识别
//                                    startRecognition();
//                                }
//                            });
//                        }
//                        if (which == 1) {
//                            mStartVoiceBtn.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    switch (status) {
//                                        case STATUS_None:
//                                            start();
//                                            mStartVoiceBtn.setText("取消");
//                                            status = STATUS_WaitingReady;
//                                            break;
//                                        case STATUS_WaitingReady:
//                                            cancel();
//                                            status = STATUS_None;
//                                            mStartVoiceBtn.setText("语音识别");
//                                            break;
//                                        case STATUS_Ready:
//                                            cancel();
//                                            status = STATUS_None;
//                                            mStartVoiceBtn.setText("语音识别");
//                                            break;
//                                        case STATUS_Speaking:
//                                            stop();
//                                            status = STATUS_Recognition;
//                                            mStartVoiceBtn.setText("识别中");
//                                            break;
//                                        case STATUS_Recognition:
//                                            cancel();
//                                            status = STATUS_None;
//                                            mStartVoiceBtn.setText("语音识别");
//                                            break;
//                                    }
//                                }
//                            });
//                        }
//                        dialog.dismiss();
//                    }
//                })
//                .show();
    }

    @Override
    protected void onDestroy() {
        speechRecognizer.destroy();
        super.onDestroy();
    }

    public void initView() {
        ((TextView) findViewById(R.id.title_tv)).setText("弹幕");
        mStartVoiceBtn = (Button) findViewById(R.id.test_button);
        mVoiceTv = (TextView) findViewById(R.id.speak_tip);
        //创建识别器
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
        //注册监听器
        speechRecognizer.setRecognitionListener(this);
    }

    public void initVariables() {
        mStartVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status) {
                    case STATUS_None:
                        start();
                        mStartVoiceBtn.setText("取消");
                        status = STATUS_WaitingReady;
                        break;
                    case STATUS_WaitingReady:
                        cancel();
                        status = STATUS_None;
                        mStartVoiceBtn.setText("语音识别");
                        break;
                    case STATUS_Ready:
                        cancel();
                        status = STATUS_None;
                        mStartVoiceBtn.setText("语音识别");
                        break;
                    case STATUS_Speaking:
                        stop();
                        status = STATUS_Recognition;
                        mStartVoiceBtn.setText("识别中");
                        break;
                    case STATUS_Recognition:
                        cancel();
                        status = STATUS_None;
                        mStartVoiceBtn.setText("语音识别");
                        break;
                }
            }
        });
        mVoiceTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(mVoiceTv.getText() + "");
                Toast.makeText(BarrageActivity.this, "弹幕已发送", Toast.LENGTH_SHORT).show();
                mVoiceTv.setText("");
            }
        });
    }

    private void start() {
        mVoiceTv.setText("");
        mVoiceTv.setText("点击了“语音识别”");
        Intent intent = new Intent();
        speechEndTime = -1;
        speechRecognizer.startListening(intent);
        mVoiceTv.setText("");
    }

    private void stop() {
        speechRecognizer.stopListening();
        mVoiceTv.setText("点击了“说完了”");
    }

    private void cancel() {
        speechRecognizer.cancel();
        status = STATUS_None;
        mVoiceTv.setText("点击了“取消”");
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        status = STATUS_Ready;
        mVoiceTv.setText("准备就绪，可以开始说话");
    }

    @Override
    public void onBeginningOfSpeech() {
        status = STATUS_Speaking;
        mStartVoiceBtn.setText("说完了");
        mVoiceTv.setText("检测到用户的已经开始说话");
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        speechEndTime = System.currentTimeMillis();
        status = STATUS_Recognition;
        mVoiceTv.setText("检测到用户的已经停止说话");
        mStartVoiceBtn.setText("识别中");
    }

    @Override
    public void onError(int error) {
        status = STATUS_None;
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        sb.append(":" + error);
        mVoiceTv.setText("识别失败：" + sb.toString());
        mStartVoiceBtn.setText("语音识别");
    }

    @Override
    public void onResults(Bundle results) {
        status = STATUS_None;
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        mVoiceTv.setText("识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])));
        String json_res = results.getString("origin_result");
        try {
            mVoiceTv.setText("origin_result=\n" + new JSONObject(json_res).toString(4));
        } catch (Exception e) {
            mVoiceTv.setText("origin_result=[warning: bad json]\n" + json_res);
        }
        mStartVoiceBtn.setText("语音识别");
        mVoiceTv.setText(nbest.get(0));
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
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
     * 通过广播消息
     *
     * @param content
     */
    private void sendMessage(String content) {
        Intent intent = new Intent();
        intent.setAction(SERVIC_ACTION);
        intent.putExtra("message", content);
        sendBroadcast(intent);
    }

}
