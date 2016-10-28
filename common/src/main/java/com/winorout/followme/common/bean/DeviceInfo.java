package com.winorout.followme.common.bean;

import java.io.Serializable;

/**
 * Created by Mr_Yan on 2016/10/28.
 */

public class DeviceInfo implements Serializable {
    private String deviceName;      //设备名称
    private String androidVersion;  //安卓版本

    public DeviceInfo(String deviceName, String androidVersion) {
        this.deviceName = deviceName;
        this.androidVersion = androidVersion;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

}
