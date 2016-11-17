package com.winorout.followme.barrage;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.speech.VoiceRecognitionService;
import com.winorout.followme.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Mr_Yan on 2016/10/3.
 */

public class BarrageFragment extends Fragment implements View.OnClickListener, RecognitionListener {

    private View view;
    private EditText barrageContent;
    private ImageView voice;
    private Button sendBarrage;
    private Button clearBarrage;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.barragefragment, container, false);
        initView();
        initEvent();
        return view;
    }

    private void initEvent() {
        voice.setOnClickListener(this);
        sendBarrage.setOnClickListener(this);
        clearBarrage.setOnClickListener(this);
    }

    private void initView() {
        barrageContent = (EditText) view.findViewById(R.id.barrageContent);
        voice = (ImageView) view.findViewById(R.id.voice);
        sendBarrage = (Button) view.findViewById(R.id.sendBarrage);
        clearBarrage = (Button) view.findViewById(R.id.clearBarrage);

        //创建识别器
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext(), new ComponentName(getContext(), VoiceRecognitionService.class));
        //注册监听器
        speechRecognizer.setRecognitionListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.voice:
                start();
                break;
            case R.id.sendBarrage:
                if (barrageContent.length() == 0 || barrageContent.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "弹幕发送失败，你得写点什么", Toast.LENGTH_SHORT).show();
                } else {
                    barrageContent.setText("");
                    Toast.makeText(getContext(), "弹幕已发送", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.clearBarrage:
                barrageContent.setText("");
                break;
        }
    }

    @Override
    public void onDestroy() {
        speechRecognizer.destroy();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            onResults(data.getExtras());
        }
    }

    private void start() {
        print("点击了“开始”");
        Intent intent = new Intent();
        intent.putExtra("sound_start", R.raw.bdspeech_recognition_start);
        intent.putExtra("sound_end", R.raw.bdspeech_speech_end);
        intent.putExtra("sound_success", R.raw.bdspeech_recognition_success);
        intent.putExtra("sound_error", R.raw.bdspeech_recognition_error);
        intent.putExtra("sound_cancel", R.raw.bdspeech_recognition_cancel);
        intent.setAction("com.baidu.action.RECOGNIZE_SPEECH");
        startActivityForResult(intent, REQUEST_UI);
        barrageContent.setText("");
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        status = STATUS_Ready;
        print("准备就绪，可以开始说话");
    }

    @Override
    public void onBeginningOfSpeech() {
        status = STATUS_Speaking;
        print("检测到用户的已经开始说话");
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
        print("检测到用户的已经停止说话");
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
        print("识别失败：" + sb.toString());
    }

    @Override
    public void onResults(Bundle results) {
        long end2finish = System.currentTimeMillis() - speechEndTime;
        status = STATUS_None;
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        print("识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])));
        String json_res = results.getString("origin_result");
        try {
            print("origin_result=\n" + new JSONObject(json_res).toString(4));
        } catch (Exception e) {
            print("origin_result=[warning: bad json]\n" + json_res);
        }
        String strEnd2Finish = "";
        if (end2finish < 60 * 1000) {
            strEnd2Finish = "(waited " + end2finish + "ms)";
        }
        barrageContent.setText(nbest.get(0) + strEnd2Finish);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest.size() > 0) {
            print("~临时识别结果：" + Arrays.toString(nbest.toArray(new String[0])));
            barrageContent.setText(nbest.get(0));
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        switch (eventType) {
            case EVENT_ERROR:
                String reason = params.get("reason") + "";
                print("EVENT_ERROR, " + reason);
                break;
            case VoiceRecognitionService.EVENT_ENGINE_SWITCH:
                int type = params.getInt("engine_type");
                print("*引擎切换至" + (type == 0 ? "在线" : "离线"));
                break;
        }
    }

    private void print(String msg) {
        Log.d(TAG, msg);
        Log.e(TAG, msg);
    }

}
