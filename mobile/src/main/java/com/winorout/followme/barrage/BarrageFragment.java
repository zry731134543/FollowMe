package com.winorout.followme.barrage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.winorout.followme.R;

/**
 * Created by Mr_Yan on 2016/10/3.
 */

public class BarrageFragment extends Fragment {
    private WebView webview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.barragefragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setView();
        setListener();
        super.onActivityCreated(savedInstanceState);
    }

    private void setListener() {
        webview.loadUrl("file:///android_asset/barrage.html");
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    private void setView() {
        webview = (WebView) getView().findViewById(R.id.webView);
    }
}
