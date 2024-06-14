package com.example.green_app.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.green_app.service.PollingService;


/**
 * <p>轮询工具类</p>
 *
 */
public class PollingUtil {
    /**
     * 开始轮询服务
     *
     * @param context
     * @param action
     */
    public static void startPollingService(final Context context, String action) {
        //包装需要执行Service的Intent
        Intent intent = new Intent(context, PollingService.class);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PollingScheduler.getInstance().addScheduleTask(pendingIntent, 0, PollingService.DEFAULT_MIN_POLLING_INTERVAL);

    }

    /**
     * 停止轮询服务,清空任务
     */
    public static void stopPollingServices() {
        PollingScheduler.getInstance().clearScheduleTasks();
    }
}
