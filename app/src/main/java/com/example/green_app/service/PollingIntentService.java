package com.example.green_app.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

/**
 * <p>轮询</p>
 */
public abstract class PollingIntentService extends Service {
    /**
     * Log头
     */
    public static final String TAG = "polling.log";
    /**
     * 主线程服务
     */
    private volatile Looper serviceLooper;
    /**
     * UI
     */
    private volatile ServiceHandler serviceHandler;
    /**
     * 服务名称
     */
    private String name;
    /**
     * 重新发出
     */
    private boolean redelivery;

    /**
     * 处理UI
     */
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            onHandleIntent((Intent) msg.obj);
        }
    }

    public PollingIntentService(String name) {
        this.name = name;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        HandlerThread thread = new HandlerThread("IntentService[" + this.name + "]");
        thread.start();
        this.serviceLooper = thread.getLooper();
        this.serviceHandler = new ServiceHandler(this.serviceLooper);
    }

    /**
     * 返回UI处理数据
     *
     * @param intent
     * @param startId
     */
    public void enterPollingQueue(@Nullable Intent intent, int startId) {
        Log.d(TAG, "enterPollingQueue");
        Message msg = this.serviceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        this.serviceHandler.sendMessage(msg);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        enterPollingQueue(intent, startId);
        return this.redelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
    }

    public void onStopService() {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        this.serviceLooper.quit();
        Log.d(TAG, "onDestroy");
    }

    @Override
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    @WorkerThread
    protected abstract void onHandleIntent(@Nullable Intent intent);
}
