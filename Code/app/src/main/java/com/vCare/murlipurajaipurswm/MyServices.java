package com.vCare.murlipurajaipurswm;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyServices extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Notification notification = new Notification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1,notification );
        }
        super.onCreate();
    }
}
