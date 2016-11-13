package com.winorout.followme.sports;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.winorout.followme.R;

/**
 * Created by Mr_Yan on 2016/10/3.
 */

public class SportsFragment extends Fragment implements View.OnClickListener{
    LinearLayout bodybuilding;
    View view;
    TextView total_kilor;
    TextView total_cal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sportsfragment, container, false);
        bindView();
        bodybuilding = (LinearLayout)view.findViewById(R.id.bodybuilding);
        bodybuilding.setOnClickListener(this);
        return view;
    }

    /**
     * 绑定视图
     */
    private void bindView() {
        total_kilor = (TextView) view.findViewById(R.id.total_kile);
        total_cal = (TextView) view.findViewById(R.id.total_cal);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bodybuilding:
                Toast.makeText(getActivity(),"敬请期待！",Toast.LENGTH_SHORT).show(); break;
            default: break;
        }
    }
}