package com.winorout.followme.personalCenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.winorout.connect.MobileMessageService;
import com.winorout.followme.R;
import com.winorout.followme.common.bean.BatteryInfo;
import com.winorout.followme.common.bean.DeviceInfo;

public class DeviceInfoActivity extends Activity {

    private static final String TAG = "DeviceInfoActivity";

    private ImageView batteryLevelImg;//电池电量图标
    private TextView batteryLevel;    //电池当前电量
    private TextView batteryStatus;   //电池使用状态
    private TextView batteryHealth;   //电池健康状况
    private TextView androidVersion;  //安卓版本
    private TextView deviceName;      //设备名称
    private Button serchDevice;       //查找设备
    private Button domSteal;          //手机防盗
    private DeviceInfo mDeviceInfo;
    private BatteryInfo mBatteryInfo;

    private static BroadcastReceiver deviceInfoBroadcastReceiver;
    private static IntentFilter deviceInfoIntentFilter;
    public static final String ACTION_DEVICE_INFO_CHANGED = "com.winorout.followme.deviceInfo.changed";
    public static final String ACTION_BATTERY_INFO_CHANGED = "com.winorout.followme.batteryInfo.changed";

    private Handler mHandler;
    public static final int UPDATE_DEVICE_INFO = 0x185;
    public static final int UPDATE_BATTERY_INFO = 0x186;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        initView();
        Intent intent = new Intent(this, MobileMessageService.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(deviceInfoBroadcastReceiver);
    }

    private void initView() {
        batteryLevelImg = (ImageView) findViewById(R.id.batteryLevelImg);
        batteryLevel = (TextView) findViewById(R.id.batteryLevel);
        batteryStatus = (TextView) findViewById(R.id.batteryStatus);
        batteryHealth = (TextView) findViewById(R.id.batteryHealth);
        androidVersion = (TextView) findViewById(R.id.androidVersion);
        deviceName = (TextView) findViewById(R.id.deviceName);
        serchDevice = (Button) findViewById(R.id.serchDevice);
        domSteal = (Button) findViewById(R.id.domSteal);

        deviceInfoBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ACTION_DEVICE_INFO_CHANGED.equals(action)) {
                    mDeviceInfo = (DeviceInfo) intent.getExtras().getSerializable("DeviceInfo");
                    Message msg = new Message();
                    msg.what = UPDATE_DEVICE_INFO;
                    mHandler.sendMessage(msg);
                    Log.e(TAG, "onReceive: " + mDeviceInfo.getDeviceName());
                }
                if (ACTION_BATTERY_INFO_CHANGED.equals(action)) {
                    mBatteryInfo = (BatteryInfo) intent.getExtras().getSerializable("BatteryInfo");
                    Message msg = new Message();
                    msg.what = UPDATE_BATTERY_INFO;
                    mHandler.sendMessage(msg);
                }
            }


        };
        deviceInfoIntentFilter = new IntentFilter();
        deviceInfoIntentFilter.addAction(ACTION_DEVICE_INFO_CHANGED);
        deviceInfoIntentFilter.addAction(ACTION_BATTERY_INFO_CHANGED);
        //注册广播设置可相应的Action
        registerReceiver(deviceInfoBroadcastReceiver, deviceInfoIntentFilter);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_DEVICE_INFO:
                        initDeviceInfo();
                        break;
                    case UPDATE_BATTERY_INFO:
                        initBatteryInfo();
                        break;
                }
            }
        };
    }

    private void initBatteryInfo() {
        batteryLevel.setText(mBatteryInfo.getBatteryLevel() + "%");
        batteryStatus.setText(mBatteryInfo.getBatteryStatus());
        batteryHealth.setText(mBatteryInfo.getBatteryHealth());
        //设置电池电量等级图标
        setBatteryLevelImg(mBatteryInfo.getBatteryLevel());
    }

    private void initDeviceInfo() {
        androidVersion.setText(mDeviceInfo.getAndroidVersion());
        deviceName.setText(mDeviceInfo.getDeviceName());
    }

    /**
     * 设置电池电量等级图标
     *
     * @param batteryLevel
     */
    private void setBatteryLevelImg(int batteryLevel) {
        switch (batteryLevel % 25) {
            case 4:
                batteryLevelImg.setImageResource(R.drawable.battery_level5);
                break;
            case 3:
                batteryLevelImg.setImageResource(R.drawable.battery_level4);
                break;
            case 2:
                batteryLevelImg.setImageResource(R.drawable.battery_level3);
                break;
            case 1:
                batteryLevelImg.setImageResource(R.drawable.battery_level2);
                break;
            default:
                batteryLevelImg.setImageResource(R.drawable.battery_level1);
                break;
        }
    }
}
