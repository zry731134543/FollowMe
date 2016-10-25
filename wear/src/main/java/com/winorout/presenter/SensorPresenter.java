package com.winorout.presenter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.winorout.entity.SportData;
import com.winorout.interfaces.OnStepChange;


public class SensorPresenter{
    private Context context;
    private OnStepChange onStepChange;
    private static final Uri STEP_URI = Uri.parse("content://com.mobvoi.ticwear.steps");
    private ContentResolver mResolver;
    private ContentObserver mObserver;
    private int mSteps;
    private SportData sportData;

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
                if(onStepChange!=null){
                    onStepChange.getStep(sportData);
                }
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
        double distance ;
        int goal;
        Cursor cursor = mResolver.query(STEP_URI, null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    steps = cursor.getInt(0);
                    distance = cursor.getDouble(1);
                    goal=cursor.getInt(2);
                    sportData=new SportData(steps,distance,goal);
                    String s3=cursor.getColumnName(3);
                    Log.d("ryzhang","enable:"+s3);
                }
            } finally {
                cursor.close();
            }
        }
        return steps;
    }
}
