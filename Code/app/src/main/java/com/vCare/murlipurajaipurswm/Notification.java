package com.vCare.murlipurajaipurswm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class Notification extends FirebaseMessagingService {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        sendNotification(message);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification(RemoteMessage message) {
        NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        android.app.Notification notify = null;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.swachh);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("MAN", "vCare_Alert", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setSound(soundUri,audioAttributes);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert notif != null;
            mBuilder.setChannelId("MAN").setContentTitle("vCare द्वारा नोटिफ़िकेशन")
                    .setContentText("कचरे वाली गाड़ी जल्द ही आपके द्वार पर पहुंचने वाली है।   कृप्या कचरा गाडी में डाले।")
                    .setSmallIcon(R.drawable.vcarelogo).setColor(Color.GREEN);
            notif.createNotificationChannel(notificationChannel);
            assert notif != null;
            notif.notify(0 /* Request Code */, mBuilder.build());
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                notify = new android.app.Notification.Builder
                        (getApplicationContext()).setContentTitle("vCare द्वारा नोटिफ़िकेशन")
                        .setContentText("कचरे वाली गाड़ी जल्द ही आपके द्वार पर पहुंचने वाली है।   कृप्या कचरा गाडी में डाले।")
                        .setSmallIcon(R.drawable.vcarelogo).setColor(Color.GREEN)
                        .setSound(soundUri,audioAttributes).build();
            }

            notify.flags |= android.app.Notification.FLAG_AUTO_CANCEL;
            notif.notify(0, notify);
        }
    }
}
