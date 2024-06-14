package com.example.green_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.green_app.http.Constant;
import com.example.green_app.http.HttpUtils;
import com.example.green_app.http.OnHttpCallback;
import com.example.green_app.model.LoginBody;
import com.example.green_app.model.Result;
import com.example.green_app.utils.SPUtils;
import com.example.green_app.utils.ToastUtils;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, OnHttpCallback {

    private EditText name;
    private EditText password;

    private Button login;

    private static final String TAG = "LoginActivity";
    private static final int LOGIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        name = findViewById(R.id.name);
        password= findViewById(R.id.password);
        login = findViewById(R.id.login);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String name = this.name.getText().toString();
        String password = this.password.getText().toString();
        if(TextUtils.isEmpty(name)){
            ToastUtils.toast(this,"请输入您的账号");
        } else if (TextUtils.isEmpty(password)) {
            ToastUtils.toast(this,"请输入您的密码");
        }else {
            ToastUtils.toast(this,"登录中，请耐心等待...");
            Login(name.trim(), password.trim());
        }
    }

    private void Login(String name, String password) {
        LoginBody loginBody = new LoginBody();
        loginBody.setUsername(name);
        loginBody.setPassword(password);
        HttpUtils.Instance().login(this, loginBody,LOGIN, this);
    }

    @Override
    public void onFailure(IOException e, int id, int error) {
        Log.e(TAG, "onFailure: "+e );
    }

    @Override
    public void onResponse(Result result, int id) {
        if(200 == (Integer) result.get("code")){
            SPUtils.setBoolean(Constant.isLogin, true, this);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}