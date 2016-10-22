package com.winorout.followme.sports;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.common.data.FreezableUtils;
import com.mobvoi.android.wearable.DataApi;
import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.Wearable;
import com.winorout.followme.R;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Mr_Yan on 2016/10/3.
 */

public class SportsFragment extends Fragment implements View.OnClickListener, DataApi.DataListener,
        MessageApi.MessageListener, NodeApi.NodeListener, MobvoiApiClient.ConnectionCallbacks, MobvoiApiClient.OnConnectionFailedListener {

    private static final String TAG = "w";

    View view;
    CircleBar circleBar;
    TextView total_kilor;
    TextView total_cal;
    TextView total_time;
    GetSetpService.LocalBinder binder;
    GetSetpService getservice;
    private int _step;
    private int _time;
    private double _calories;
    private double _kile;
    int step;
    PedometerDB db;
    Intent intentRemind;
    Message message = null;

    // 模拟 消息传入 手表
    private Button mTextBtn;

    /*********** 测试环境 ************/
    private static final int REQUEST_RESOLVE_ERROR = 1000;

    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String COUNT_PATH = "/count";
    private static final String COUNT_KEY = "count";

    private Handler mHandler;
    private View mStartActivityBtn;
    private boolean mResolvingError = false;
    private MobvoiApiClient mMobvoiApiClient;

    // Send DataItems.
    private ScheduledExecutorService mGeneratorExecutor;
    private ScheduledFuture<?> mDataItemGeneratorFuture;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() 11");
        view = inflater.inflate(R.layout.sportsfragment, container, false);
        db = PedometerDB.getInstance(getActivity());
        createService();
        bindView();
        show();

        mHandler = new Handler();
        Log.e(TAG, "onCreate() 1");
        // Stores DataItems received by the local broadcaster or from the paired
        // watch.
        mGeneratorExecutor = new ScheduledThreadPoolExecutor(1);

        mMobvoiApiClient = new MobvoiApiClient.Builder(getActivity()).addApi(Wearable.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        Log.e(TAG, "onCreate() 111");

        return view;
    }

    /**
     * 创建获取步数服务
     */
    private void createService() {
        // 启动获取步数服务
        Intent intent = new Intent(getActivity(), GetSetpService.class);
//		Intent intent = new Intent(getActivity(), TicWearService.class);
        // intentRemind = new Intent(getActivity(),RemindService.class);
        // getActivity().startService(intentRemind);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 绑定视图
     */
    private void bindView() {
        total_kilor = (TextView) view.findViewById(R.id.total_kile);
        total_cal = (TextView) view.findViewById(R.id.total_cal);
        total_time = (TextView) view.findViewById(R.id.total_time);
        circleBar = (CircleBar) view.findViewById(R.id.circle);
        circleBar.setOnClickListener(this);
    }

    /**
     * 显示数据
     */
    private void show() {
        total_kilor.setText(_kile + "m");
        total_cal.setText(_calories + "j");
        total_time.setText(_time + "s");
        circleBar.max = step;
        circleBar.setText(_step);
        circleBar.setProgress(_step, 1);
        circleBar.startCustomAnimation();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (GetSetpService.LocalBinder) service;
            setData(binder.getMessage());
            show();
            getservice = binder.getservice();
            getservice.setOnprogressListener(new StepListencer() {
                @Override
                public void onprogress(Message msg) {
                    msg.what = 1;
                    message = msg;
                    handler.sendMessage(msg);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                setData(msg);
                show();
            }
            if (msg.what == 0) {
                show();
            }
        };
    };

    private void setData(Message msg) {
        Bundle data = msg.getData();
        _step = data.getInt("step");
        _time = data.getInt("time");
        _calories = data.getDouble("calories");
        _kile = data.getDouble("kile");
        Log.e("zryservice", "设置步数：" + step);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onResume() {
        Log.e("zryservice", "重新回调Fragment" + db.selectGoals());
        step = db.selectGoals() != null ? Integer.parseInt(db.selectGoals()) : 10000;
        handler.sendEmptyMessage(0);
        // mDataItemGeneratorFuture =
        // mGeneratorExecutor.scheduleWithFixedDelay(new DataItemGenerator(), 1,
        // 5,
        // TimeUnit.SECONDS);
        super.onResume();
    }

    @Override
    public void onStart() {
        Log.e("w", "onStart()");
        super.onStart();
        if (!mResolvingError) {
            mMobvoiApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        Log.e("w", "onPause()");
        super.onPause();
//		mDataItemGeneratorFuture.cancel(true /* mayInterruptIfRunning */);
    }

    @Override
    public void onStop() {
        Log.e("w", "onStop()");
        if (!mResolvingError) {
            Wearable.DataApi.removeListener(mMobvoiApiClient, this);
            Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
            Wearable.NodeApi.removeListener(mMobvoiApiClient, this);
            mMobvoiApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    // ConnectionCallbacks
    public void onConnected(Bundle connectionHint) {
        Log.e("w", "onConnected()");
        mResolvingError = false;
        mStartActivityBtn.setEnabled(true);
        Wearable.DataApi.addListener(mMobvoiApiClient, this);
        Wearable.MessageApi.addListener(mMobvoiApiClient, this);
        Wearable.NodeApi.addListener(mMobvoiApiClient, this);
    }

    @Override
    // ConnectionCallbacks
    public void onConnectionSuspended(int cause) {
        Log.e("w", "onConnectionSuspended()");
        mStartActivityBtn.setEnabled(false);
    }

    @Override
    // OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult result) {
        Log.e("w", "onConnectionFailed()");
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            // try {
            // mResolvingError = true;
            // result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            // } catch (IntentSender.SendIntentException e) {
            // // There was an error with the resolution intent. Try again.
            // mMobvoiApiClient.connect();
            // }
        } else {
            mResolvingError = false;
            mStartActivityBtn.setEnabled(false);
            Wearable.DataApi.removeListener(mMobvoiApiClient, this);
            Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
            Wearable.NodeApi.removeListener(mMobvoiApiClient, this);
        }
    }

    @Override
    // DataListener
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.e("w", "onDataChanged()");
        LOGD(TAG, "onDataChanged: " + dataEvents);
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (DataEvent event : events) {
                    if (event.getType() == DataEvent.TYPE_CHANGED) {
                        Log.e("w", "event.getDataItem().toString() = " + event.getDataItem().toString());
                    } else if (event.getType() == DataEvent.TYPE_DELETED) {
                        Log.e("w", "event.getDataItem().toString() = " + event.getDataItem().toString());
                    }
                }
            }
        });
    }

    @Override
    // MessageListener
    public void onMessageReceived(final MessageEvent messageEvent) {
        LOGD(TAG, "onMessageReceived() A message from watch was received:" + messageEvent.getRequestId() + " "
                + messageEvent.getPath());
        Log.e("w", "messageEvent.getRequestId() = " + messageEvent.getRequestId());
        Log.e("w", "messageEvent.getPath() = " + messageEvent.getPath());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
            }
        });

    }

    @Override
    // NodeListener
    public void onPeerConnected(final Node peer) {
        Log.e("w", "onPeerConnected()");
        LOGD(TAG, "onPeerConnected: " + peer);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
            }
        });

    }

    @Override
    // NodeListener
    public void onPeerDisconnected(final Node peer) {
        Log.e("w", "onPeerDisconnected()");
        LOGD(TAG, "onPeerDisconnected: " + peer);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    // ???
    private Collection<String> getNodes() {
        Log.e("w", "getNodes()");
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mMobvoiApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }

        return results;
    }

    private void sendStartActivityMessage(String node) {
        Log.e("w", "sendStartActivityMessage()");
        Wearable.MessageApi.sendMessage(mMobvoiApiClient, node, START_ACTIVITY_PATH, new byte[0])
                .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to send message with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                        }
                    }
                });
    }

    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendStartActivityMessage(node);
            }
            return null;
        }
    }

    /** Sends an RPC to start a fullscreen Activity on the wearable. */
    public void onStartWearableActivityClick(View view) {

        // Trigger an AsyncTask that will query for a list of connected nodes
        // and send a
        // "start-activity" message to each connected node.
        new StartWearableActivityTask().execute();
    }

    /**
     * As simple wrapper around Log.e
     */
    private static void LOGD(final String tag, String message) {
        Log.e(tag, message);
    }
}
