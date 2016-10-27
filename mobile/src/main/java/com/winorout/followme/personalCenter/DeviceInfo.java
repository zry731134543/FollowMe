package com.winorout.followme.personalCenter;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.winorout.followme.R;

public class DeviceInfo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
//        WebSettings webSettings =webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);  //支持js
    }

    @Override
    protected void onResume() {
        super.onResume();
//        webView.loadUrl("file:///android_asset/video.html");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        webView.destroy();
    }
}
