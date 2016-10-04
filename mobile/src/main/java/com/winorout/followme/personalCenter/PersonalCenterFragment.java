package com.winorout.followme.personalCenter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.winorout.followme.R;

/**
 * Created by Mr-x on 2016/10/03.
 */

public class PersonalCenterFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.centerfragment, container, false);
    }
}
