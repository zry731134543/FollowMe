package com.winorout.followme.personalCenter;

/**
 * Created by Mr_Yan on 2016/11/11.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.winorout.followme.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class UserLoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "qmyan";
    private EditText login_username;
    private EditText login_password;
    private Button user_login_button;
    private Button user_register_button;
    private RequestQueue mQueue;
    private StringRequest mRequest;
    private JSONObject mJsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.user_login);
        initWidget();
    }

    private void initWidget() {
        login_username = (EditText) findViewById(R.id.login_username);
        login_password = (EditText) findViewById(R.id.login_password);
        user_login_button = (Button) findViewById(R.id.user_login_button);
        user_register_button = (Button) findViewById(R.id.user_register_button);
        user_login_button.setOnClickListener(this);
        user_register_button.setOnClickListener(this);
        login_username.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String username = login_username.getText().toString().trim();
                    if (username.length() < 4) {
                        Toast.makeText(UserLoginActivity.this, "用户名不能小于4个字符", Toast.LENGTH_SHORT);
                    }
                }
            }

        });
        login_password.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String password = login_password.getText().toString().trim();
                    if (password.length() < 6) {
                        Toast.makeText(UserLoginActivity.this, "密码不能小于6个字符", Toast.LENGTH_SHORT);
                    }
                }
            }

        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_login_button:
                Log.e(TAG, "login_button_exe");
                if (checkEdit()) {
                    login();
                    Log.e(TAG, "login()exe");
                }
                break;
            case R.id.user_register_button:
                Intent intent2 = new Intent(UserLoginActivity.this, UserRegisterActivity.class);
                startActivity(intent2);
                break;
        }
    }

    private boolean checkEdit() {
        if (login_username.getText().toString().trim().equals("")) {
            Toast.makeText(UserLoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
        } else if (login_password.getText().toString().trim().equals("")) {
            Toast.makeText(UserLoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    private void login() {
        mQueue = Volley.newRequestQueue(UserLoginActivity.this);
        mRequest = new StringRequest(Request.Method.POST, "http://123.57.209.45:8080/followMe/UserServlet", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    mJsonObject = new JSONObject(response);
                    String code = mJsonObject.getString("code");
                    Log.e(TAG, "code:" + code);
                    if (code.equals("1")) {
                        Toast.makeText(UserLoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = getIntent();
                        intent.putExtra("userName", mJsonObject.getJSONObject("dataList").getString("userName"));
                        setResult(1, intent);
                        finish();
                    }else {
                        byte[] jiema = mJsonObject.getString("message").getBytes("utf-8");
                        String message = new String(jiema);
                        Toast.makeText(UserLoginActivity.this, "登录失败," + message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, volleyError.getMessage());
                Toast.makeText(UserLoginActivity.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("type", "login");
                map.put("userName", login_username.getText().toString());
                map.put("userPassword", login_password.getText().toString());
                return map;
            }
        };
        mQueue.add(mRequest);
    }
}
