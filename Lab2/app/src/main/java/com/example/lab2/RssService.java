package com.example.lab2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.os.IBinder;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.Timer;
import java.util.TimerTask;

public class RssService extends Service{
    private static final int NOTIFICATION_ID = 234;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) {
            return START_STICKY;
        }

        Timer mTimer = new Timer();
        mTimer.schedule(
            new TimerTask() {
                @Override
                public void run() {
                    notifyUser();
                }
            },
            1000,
            10000
        );

        return START_STICKY;
    }

    private void notifyUser() {
        Intent mainActivityIntent = new Intent(RssService.this, MainActivity.class);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(RssService.this)
                .addParentStack(MainActivity.class)
                .addNextIntent(mainActivityIntent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification mNotification =
                new NotificationCompat.Builder(getBaseContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Lenta.ru RSS changed")
                        .setContentText("There are new posts!")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mNotification);
    }



    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

}
