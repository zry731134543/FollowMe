package com.winorout.tools;

import android.util.Log;
/**
 * 输出log
 * @author  ryzhang
 * @data 2016/10/21 18:35
 */
public class Logg {
    private static final String TAG="ryzhang";
    public static void d(String value){
        Log.d(TAG,value);
    }
    public static void e(String value){
        Log.e(TAG,value);
    }
}
