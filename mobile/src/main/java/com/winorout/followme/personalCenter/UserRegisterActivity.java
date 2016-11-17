package com.winorout.followme.personalCenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Mr_Yan on 2016/11/11.
 */

public class UserRegisterActivity extends Activity {

    private String TAG = "qmyan";
    private EditText register_username;
    private EditText register_passwd;
    private EditText reregister_passwd;
    private Button register_submit;
    private RequestQueue mQueue;
    private StringRequest mRequest;
    private JSONObject mJsonObject;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.user_register);
        register_username=(EditText)findViewById(R.id.register_username);
        register_passwd=(EditText)findViewById(R.id.register_passwd);
        reregister_passwd=(EditText)findViewById(R.id.reregister_passwd);
        register_submit=(Button)findViewById(R.id.register_submit);
        register_username.setOnFocusChangeListener(new OnFocusChangeListener()
        {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(register_username.getText().toString().trim().length()<4){
                        Toast.makeText(UserRegisterActivity.this, "用户名不能小于4个字符", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
        register_passwd.setOnFocusChangeListener(new OnFocusChangeListener()
        {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(register_passwd.getText().toString().trim().length()<6){
                        Toast.makeText(UserRegisterActivity.this, "密码不能小于6个字符", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
        reregister_passwd.setOnFocusChangeListener(new OnFocusChangeListener()
        {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(!reregister_passwd.getText().toString().trim().equals(register_passwd.getText().toString().trim())){
                        Toast.makeText(UserRegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
        register_submit.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {

                if(!checkEdit()){
                    return;
                }
                mQueue = Volley.newRequestQueue(UserRegisterActivity.this);
                mRequest = new StringRequest(Request.Method.POST, "http://123.57.209.45:8080/followMe/UserServlet", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            mJsonObject = new JSONObject(response);
                            String code = mJsonObject.getString("code");
                            Log.e(TAG, "code:" + code);
                            if (code.equals("1")) {
                                Toast.makeText(UserRegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                Intent intent = getIntent();
                                intent.putExtra("userName", mJsonObject.getJSONObject("dataList").getString("userName"));
                                setResult(1, intent);
                                finish();
                            }else {
                                Toast.makeText(UserRegisterActivity.this, mJsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, mJsonObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e(TAG, volleyError.getMessage());
                        Toast.makeText(UserRegisterActivity.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("type", "register");
                        map.put("userName", register_username.getText().toString());
                        map.put("userPassword", register_passwd.getText().toString());
                        return map;
                    }
                };
                mQueue.add(mRequest);
            }
        });
    }

    private boolean checkEdit(){
        if(register_username.getText().toString().trim().equals("")){
            Toast.makeText(UserRegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
        }else if(register_passwd.getText().toString().trim().equals("")){
            Toast.makeText(UserRegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
        }else if(!register_passwd.getText().toString().trim().equals(reregister_passwd.getText().toString().trim())){
            Toast.makeText(UserRegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
        }else{
            return true;
        }
        return false;
    }

}
