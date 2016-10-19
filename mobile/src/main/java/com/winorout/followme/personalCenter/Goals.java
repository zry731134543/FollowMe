package com.winorout.followme.personalCenter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.winorout.followme.R;

import java.util.ArrayList;
import java.util.List;

public class Goals extends Activity implements OnClickListener {
    Button btn_five;
    Button btn_eight;
    Button btn_ten;
    Button btn_twelve;
    Button btn_fifteen;
    Button btn_twenty;
    Button save_goals;
    TextView tetx_data;
    String step = null;
    PedometerDB db = PedometerDB.getInstance(this);
    List<Button> list = new ArrayList<Button>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.goals);
        bindView();
        initData();
    }

    /**
     * 绑定视图
     */
    private void bindView() {
        btn_five = (Button) findViewById(R.id.btn_five);
        btn_eight = (Button) findViewById(R.id.btn_eight);
        btn_ten = (Button) findViewById(R.id.btn_ten);
        btn_twelve = (Button) findViewById(R.id.btn_twelve);
        btn_fifteen = (Button) findViewById(R.id.btn_fifteen);
        btn_twenty = (Button) findViewById(R.id.btn_twenty);
        save_goals = (Button) findViewById(R.id.save_goals);
        tetx_data = (TextView) findViewById(R.id.tetx_data);
        list.add(btn_five);
        list.add(btn_eight);
        list.add(btn_ten);
        list.add(btn_twelve);
        list.add(btn_fifteen);
        list.add(btn_twenty);
        btn_five.setOnClickListener(this);
        btn_eight.setOnClickListener(this);
        btn_twelve.setOnClickListener(this);
        btn_fifteen.setOnClickListener(this);
        btn_twenty.setOnClickListener(this);
        btn_ten.setOnClickListener(this);
        save_goals.setOnClickListener(this);
    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tetx_data.setText(step);
        }

        ;
    };

    /**
     * 初始化数据
     */
    private void initData() {
        //获取数据库运动目标
        step = db.selectGoals();
        if (step == null) {
            db.insertGoals(10000 + "");
        }
        handle.sendEmptyMessage(0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.save_goals) {
            // 修改数据库数据
            db.insertGoals(tetx_data.getText() + "");
            Toast.makeText(Goals.this, "保存成功", Toast.LENGTH_SHORT).show();
        } else {
            for (Button btn : list)
                if (btn.getId() == id) {
                    btn.setBackgroundColor(Color.parseColor("#129cee"));
                    tetx_data.setText(btn.getText());
                } else {
                    btn.setBackgroundColor(Color.parseColor("#ffffff"));
                }
        }
    }

}
