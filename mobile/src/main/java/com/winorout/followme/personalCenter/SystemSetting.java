package com.winorout.followme.personalCenter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.winorout.followme.R;

/**
 * Created by tom on 2016/11/12.
 */

public class SystemSetting extends Activity {
    private LinearLayout Update;
    private LinearLayout Feedback;
    private LinearLayout About;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.systemsetting);
        Update = (LinearLayout)findViewById(R.id.update);
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SystemSetting.this,"已经是最新版",Toast.LENGTH_SHORT).show();
            }
        });



    }

}
