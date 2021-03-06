package com.winorout.base;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.winorout.followme.R;

/**
 * Created by xwangch on 16/10/5.
 *
 */

public abstract class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariables();
        initView(savedInstanceState);
        loadData();
    }

    /**标题栏*/
    public void initTopbar(String title){
        ((TextView)findViewById(R.id.title_tv)).setText(title);
    }

    /**将onCreate分解为3个子方法,完成整个逻辑
     *
     * 1、initVariables()   初始化变量
     * 2、initView()   加载Layout布局文件
     * 3、loadData()    加载数据
     * */

    public abstract void initVariables();
    public abstract void initView(Bundle savedInstanceState);
    public abstract void loadData();
}
