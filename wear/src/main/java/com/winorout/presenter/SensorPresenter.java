package com.winorout.presenter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.winorout.interfaces.OnStepChange;


public class SensorPresenter{
    private Context context;
    private OnStepChange onStepChange;
    private static final Uri STEP_URI = Uri.parse("content://com.mobvoi.ticwear.steps");
    private ContentResolver mResolver;
    private ContentObserver mObserver;
    private int mSteps;

    public SensorPresenter(Context context){
        this.context=context;
        mResolver = context.getContentResolver();
        mObserver = new ContentObserver(null) {
            @Override
            public boolean deliverSelfNotifications() {
                return super.deliverSelfNotifications();
            }
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                mSteps = fetchSteps();
                Log.d("ryzhang","当前步数"+mSteps);
                onStepChange.getStep(mSteps);
            }
        };

    }
    public void setOnStepChange(OnStepChange onStepChange){
        this.onStepChange=onStepChange;
    }

    public void registerListener(){
        mResolver.registerContentObserver(STEP_URI, true, mObserver);
    }
    public void unregisterListener(){
        mResolver.unregisterContentObserver(mObserver);
    }
    public int fetchSteps() {
        int steps = 0;
        Cursor cursor = mResolver.query(STEP_URI, null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    steps = cursor.getInt(0);
                }
            } finally {
                cursor.close();
            }
        }
        return steps;
    }
}