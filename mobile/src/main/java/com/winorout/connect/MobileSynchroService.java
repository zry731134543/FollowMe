package com.winorout.connect;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.data.FreezableUtils;
import com.mobvoi.android.wearable.DataApi;
import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.Wearable;

import java.util.List;

/**
 * 手机端数据同步Service
 *
 * @author ryzhang
 * @data 2016/10/13 14:44
 */
public class MobileSynchroService extends Service implements MobvoiApiClient.ConnectionCallbacks,
        MobvoiApiClient.OnConnectionFailedListener {
    private static final String TAG = "ryzhang";
    public static final String COUNT_PATH = "/step_count";
    private boolean mResolvingError = false; //错误解决
    private MobvoiApiClient mMobvoiApiClient;//API的调用入口
    private DataApi.DataListener mDataListener;
    private Activity mActivity;

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder(MobileSynchroService.this);
    }

    public class MyBinder extends Binder{
        public MobileSynchroService mobileSynchroService;

        public MyBinder(MobileSynchroService mobileSynchroService){
            this.mobileSynchroService = mobileSynchroService;
        }
        public void setActivity(Activity activity){
            mActivity=activity;
        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "MobileSynchroService.onCreate");
        init();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "MobileSynchroService.onDestroy");
        if (!mResolvingError) {
            Wearable.DataApi.removeListener(mMobvoiApiClient, mDataListener);
            mMobvoiApiClient.disconnect();
        }
        super.onDestroy();

    }

    /**
     * 初始化
     */
    private void init() {
        mMobvoiApiClient = new MobvoiApiClient.Builder(MobileSynchroService.this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mDataListener = new DataApi.DataListener() {
            @Override
            public void onDataChanged(DataEventBuffer dataEvents) {//当数据改变时
                Log.d(TAG, "DataListener.onDataChanged");
                final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
                dataEvents.close();
                //提取数据
                for (DataEvent event : events) {
                    Uri uri = event.getDataItem().getUri();
                    String path = uri.getPath();
                    if (COUNT_PATH.equals(path)) {
                        String nodeId = uri.getHost();
                        byte[] payload = uri.toString().getBytes();
                        String data=new String(payload);
                        Log.d(TAG,data);
                        //// TODO: 2016/10/13 传递数据
                    }
                }

            }
        };
        if (!mResolvingError) {
            mMobvoiApiClient.connect();
        }

    }

    /**
     * -------------ConnectionCallbacks--------------
     */
    @Override
    public void onConnected(Bundle bundle) {//连接成功
        Log.d(TAG, "MobileSynchroService.onConnected");
        mResolvingError = false;
        Wearable.DataApi.addListener(mMobvoiApiClient, mDataListener);
    }

    @Override
    public void onConnectionSuspended(int i) {//连接中断
        Log.d(TAG, "MobileSynchroService.onConnectionSuspended");
    }

    /**
     * -------------OnConnectionFailedListener--------------
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {//连接失败
        Log.d(TAG, "MobileSynchroService.onConnectionFailed");
        if (mResolvingError) { // 已解决错误
            return;
        } else if (connectionResult.hasResolution()) {//尝试去解决问题
            // TODO: 2016/10/13 尝试去解决连接问题
            try {
                mResolvingError = true;
                if(mActivity!=null) {
                    connectionResult.startResolutionForResult(mActivity, 1000);
                }
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mMobvoiApiClient.connect();
            }
        }
    }

}
