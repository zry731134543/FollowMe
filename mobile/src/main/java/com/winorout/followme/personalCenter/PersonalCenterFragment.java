package com.winorout.followme.personalCenter;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Wearable;
import com.winorout.followme.R;
import com.winorout.followme.common.bean.BatteryInfo;
import com.winorout.followme.common.bean.DeviceInfo;
import com.winorout.followme.common.utils.TransformBytesAndObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mr-x on 2016/10/03.
 */

public class PersonalCenterFragment extends Fragment implements View.OnClickListener, MobvoiApiClient.ConnectionCallbacks, MobvoiApiClient.OnConnectionFailedListener {

    private static final String TAG = "qmyan";
    private View view;
    private LinearLayout exerciseLayout;
    private LinearLayout goalsLayout;
    private LinearLayout historyLayout;
    private LinearLayout deviceLayout;

    private ImageView deviceImg;
    private TextView deviceInfo;

    private boolean isConnected;
    private MobvoiApiClient mClient;
    private Handler handler;

    private TextView step;
    private String count;

    private DeviceInfo mDeviceInfo;
    private BatteryInfo mBatteryInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.centerfragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        exerciseLayout = (LinearLayout) view.findViewById(R.id.exercise);
        goalsLayout = (LinearLayout) view.findViewById(R.id.goals);
        historyLayout = (LinearLayout) view.findViewById(R.id.history);
        deviceLayout = (LinearLayout) view.findViewById(R.id.device);
//		settingLayout=(LinearLayout) view.findViewById(R.id.setting);

        exerciseLayout.setOnClickListener(this);
        goalsLayout.setOnClickListener(this);
        historyLayout.setOnClickListener(this);
        deviceLayout.setOnClickListener(this);
        deviceLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent3 = new Intent(getActivity(), DeviceInfoActivity.class);
                startActivity(intent3);
                return true;
            }
        });
//		settingLayout.setOnClickListener(this);

        deviceImg = (ImageView) view.findViewById(R.id.deviceImg);
        deviceInfo = (TextView) view.findViewById(R.id.deviceInfo);

        //调用API
        mClient = new MobvoiApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x124) {
                    //检查设备是否连接
                    checkConnectState();
                    //设置我的设备显示信息
                    initMyDevice();
                }
                if (msg.what == 0x126) {
                    count = String.valueOf(msg.obj);
                    step.setText(count);
                    SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("loginUser", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString("user_mobile", count);
                    editor.commit();
                }
            }
        };

        //启动一个新的线程定时更新状态
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 0x124;
                handler.sendMessage(msg);
            }
        }, 0, 300);

    }

    @Override
    public void onResume() {
        super.onResume();
        //连接API
        mClient.connect();
        //运动目标
        step = (TextView) view.findViewById(R.id.step);
        SharedPreferences sp = getActivity().getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        String numbers = sp.getString("user_mobile", "");
        step.setText(numbers);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.history:
                Intent intent = new Intent(getActivity(), Historcal.class);
                startActivity(intent);
                break;
            case R.id.exercise:
                Intent intent1 = new Intent(getActivity(), Exercise.class);
                startActivity(intent1);
                break;
            case R.id.goals:
                MyDialog myDialog = new MyDialog(getContext(), handler);
                myDialog.show();
                break;
            case R.id.device:
                if (isConnected) {
                    //如果已经和手表连接设备,打开设备信息界面
                    Intent intent3 = new Intent(getActivity(), DeviceInfoActivity.class);
                    startActivity(intent3);
                } else {
                    //如果已经和手表连接设备,打开TicWear助手连接设备界面
                    doStartApplicationWithPackageName("");
                }
                break;
        }
    }

    // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
    private void doStartApplicationWithPackageName(String packagename) {
        PackageInfo packageinfo = null;
        try {
            packageinfo = getActivity().getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            Toast.makeText(getContext(), "请先安装TicWear助手", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = getActivity().getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            startActivity(intent);
        }
    }

    //更新我的设备显示信息
    private void initMyDevice() {
        if (isConnected) {
            deviceImg.setImageResource(R.drawable.watch);
            deviceInfo.setText("智能手表");
        } else {
            deviceImg.setImageResource(R.drawable.connect);
            deviceInfo.setText("连接设备");
        }
    }

    //检查手表与手机是否连接
    public void checkConnectState() {
        Wearable.MessageApi.sendMessage(
                mClient, "default", "CHECK_CONNECT_STATE", new byte[0]).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            isConnected = false;
                        } else {
                            isConnected = true;
                        }
                    }
                }
        );
    }

    //API连接成功时回调
    @Override
    public void onConnected(Bundle bundle) {
        Log.e(TAG, "onConnected: " + bundle);
    }

    //API连接中断时回调
    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended: " + i);
    }

    //API连接失败时调用
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult);
    }

}
