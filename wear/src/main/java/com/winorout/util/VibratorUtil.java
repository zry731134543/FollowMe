package com.winorout.util;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

/**
 * Created by wangchaohu on 2016/10/20.
 *
 *
 * 手表震动工具类
 */

public class VibratorUtil {

    //震动一次，milliseconds代表震动时间
    public static void Vibrate(Activity activity, long milliseconds){
        Vibrator vibrator = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(milliseconds);
    }

    //isRepeat表示是否重复震动
    public static void Vibrate(Activity activity, long[] pattern, boolean isRepeat){
        Vibrator vibrator = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, isRepeat ? 1 : -1);
    }

    //取消震动
    public static void Vibrate(Activity activity, boolean isCancle){
        Vibrator vibrator = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.cancel();
    }


}
