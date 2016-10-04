package com.winorout.followme.barrage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.winorout.followme.R;

/**
 * Created by Mr_Yan on 2016/10/3.
 */

public class BarrageFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.barragefragment, container, false);
    }
}
