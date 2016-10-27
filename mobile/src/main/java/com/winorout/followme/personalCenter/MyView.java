package com.winorout.followme.personalCenter;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;


/**
 * Created by Mr-x on 2016/10/25.
 */

public class MyView extends RelativeLayout {
    private RelativeLayout.LayoutParams layoutParams;
    public static String count;
    ArrayList<String> list;
    private static boolean isScroll;

    public String getCount() {
        if (!isScroll) {
            count = "1000";
        }
        return count;
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        isScroll = false;
        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        LoopView loopView = new LoopView(getContext());
        list = new ArrayList();
        for (int i = 0; i < 40; i++) {
            if (i < 18 || i == 18) {
                list.add("" + (i * 500 + 1000));
            } else {
                list.add("" + (i * 1000 + 1000));
            }
        }
        //设置是否循环播放
        loopView.setNotLoop();
        //滚动监听
        loopView.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item) {
                Log.d("debug", "Item " + list.get(item));
                count = "" + list.get(item);
                isScroll = true;
            }

        });
        //设置原始数据
        loopView.setArrayList(list);
        //设置初始位置
        loopView.setPosition(0);
        //设置字体大小
        loopView.setTextSize(20);
        addView(loopView, layoutParams);//滚动监听
    }
    }
