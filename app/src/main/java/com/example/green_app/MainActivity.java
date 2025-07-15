package com.example.green_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSONObject;
import com.example.green_app.constant.EventBusConstant;
import com.example.green_app.http.Constant;
import com.example.green_app.http.HttpUtils;
import com.example.green_app.http.OnHttpCallback;
import com.example.green_app.model.Location;
import com.example.green_app.model.Result;
import com.example.green_app.service.PollingService;
import com.example.green_app.utils.CoordinateConversionUtils;
import com.example.green_app.utils.PollingUtil;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnHttpCallback {
    private Button start;
    private Button finish;

    private static final int LOCATION_START = 1;
    private static final int LOCATION_FINISH = 2;
    // 定义位置管理器
    private LocationManager locationManager;

    // 定义双精度类型的经纬度
    private Double longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLocation();
        initView();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        start = findViewById(R.id.start);
        finish = findViewById(R.id.finish);
        start.setOnClickListener(this);
        finish.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        // 获取当前位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getAllProviders();
        for (String provider : providers) {
            Log.e("location", "getLocation: "+provider );
        }
        // 启动位置请求
        // LocationManager.GPS_PROVIDER GPS定位
        // LocationManager.NETWORK_PROVIDER 网络定位
        // LocationManager.PASSIVE_PROVIDER 被动接受定位信息
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000L, (float) 0, (LocationListener) MainActivity.this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.start) {
            runScheduleTask();
        } else {
            //
            PollingUtil.stopPollingServices();
        }
    }

    @Override
    public void onFailure(IOException e, int id, int error) {
        if(error == HttpUtils.ERROR_LOGIN){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onResponse(Result result, int id) {
        if(result.getCode() == '0'){

        }
        Log.e("Location", "onResponse: " + result.toString());
    }

    private void runScheduleTask() {
        Log.e("Location", "runScheduleTask: ");
        // 加载准备中的手术信息
        PollingUtil.startPollingService(this, PollingService.ACTION_CURRENT_SURGERY);
    }

    @Subscribe
    public void loadLocation(String msg) {
        if (EventBusConstant.loadScheduler.equals(msg) && !isDestroyed()) {
            if (longitude == null && latitude == null) {
                return;
            }
            JSONObject gcjGps = CoordinateConversionUtils.wgs84ToGcj02(longitude, latitude);
            String strLat = gcjGps.getString("lat");
            String strLng = gcjGps.getString("lng");
            Location location = Location.Instance();
            location.setPatrolId(747L);
            location.setLatitude(new BigDecimal(strLat));
            location.setLongitude(new BigDecimal(strLng));
            location.setCreateTime(new Date());
            HttpUtils.Instance().postJson( Constant.API.APP_PATROL_POSITION.getUrl(this), location, this, LOCATION_START, MainActivity.this);
        }
    }

}