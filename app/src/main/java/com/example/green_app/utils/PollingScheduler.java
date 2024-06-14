package com.example.green_app.utils;

import android.app.PendingIntent;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * <p>轮询计划任务</p>
 */
public class PollingScheduler {
    /**
     * Log头
     */
    public static final String TAG = "scheduler.log";
    /**
     * 计划任务实例
     */
    private static PollingScheduler instance;
    /**
     * 任务线程池
     */
    private ScheduledExecutorService scheduler;

    private PollingScheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public static synchronized PollingScheduler getInstance() {
        if (instance == null) {
            instance = new PollingScheduler();
        }
        if (instance.scheduler.isShutdown()) {
            instance.scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        return instance;
    }

    /**
     * 增加任务
     *
     * @param pendingIntent
     * @param initialDelay
     * @param period
     */
    public void addScheduleTask(final PendingIntent pendingIntent, long initialDelay, long period) {
        Runnable command = () -> {
            try {
                Log.d(TAG, "pendingIntent run: ");
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        };

        scheduler.scheduleAtFixedRate(command, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * 清除任务
     */
    public void clearScheduleTasks() {
        scheduler.shutdownNow();
    }
}
