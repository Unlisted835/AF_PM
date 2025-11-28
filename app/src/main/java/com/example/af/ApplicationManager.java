package com.example.af;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

public class ApplicationManager extends Application
{
    public static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "default";
    public static final String DEFAULT_NOTIFICATION_CHANNEL_NAME = "default";

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(
               DEFAULT_NOTIFICATION_CHANNEL_ID, DEFAULT_NOTIFICATION_CHANNEL_NAME, importance);
            channel.setDescription("Canal de Notificação");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationService = new Intent(this, NotificationService.class);
        startService(notificationService);
    }
}

