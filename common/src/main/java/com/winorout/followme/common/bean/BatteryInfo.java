package com.winorout.followme.common.bean;

import java.io.Serializable;

/**
 * Created by Mr_Yan on 2016/10/28.
 */

public class BatteryInfo implements Serializable {
    private int batteryLevel;       //目前电量
    private String batteryStatus;   //电池状态
    private String batteryHealth;     //电池使用情况

    public BatteryInfo(int batteryLevel, String batteryStatus, String batteryHealth) {
        this.batteryLevel = batteryLevel;
        this.batteryStatus = batteryStatus;
        this.batteryHealth = batteryHealth;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public String getBatteryStatus() {
        return batteryStatus;
    }

    public String getBatteryHealth() {
        return batteryHealth;
    }
}
