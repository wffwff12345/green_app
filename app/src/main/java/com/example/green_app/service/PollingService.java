package com.example.green_app.service;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;


import com.example.green_app.constant.EventBusConstant;

import org.greenrobot.eventbus.EventBus;

/**
 * <p>轮询任务</p>
 */
public class PollingService extends PollingIntentService {
    /**
     * Log头
     */
    public static final String TAG = "polling.log";
    public static final long DEFAULT_MIN_POLLING_INTERVAL = 3000;
    /**
     * 轮询Action
     */
    public static final String ACTION_CHECK_CIRCLE_UPDATE = "ACTION_CHECK_CIRCLE_UPDATE";
    /**
     * 当前手术
     */
    public static final String ACTION_CURRENT_SURGERY = "action_current_surgery";

    public PollingService() {
        super("PollingService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent");
        if (intent == null) {
            return;
        }
        Log.d(TAG, "action " + intent.getAction());
        final String action = intent.getAction();
        if (ACTION_CURRENT_SURGERY.equals(action)) {
            EventBus.getDefault().post(EventBusConstant.loadScheduler);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy............");
    }
}
