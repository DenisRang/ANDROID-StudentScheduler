package com.sansara.develop.studentscheduler.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.sansara.develop.studentscheduler.HomeActivity;
import com.sansara.develop.studentscheduler.R;
import com.sansara.develop.studentscheduler.alarm.AlarmHelper;

/**
 * Created by den on 01.03.2018.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra(AlarmHelper.EXTRA_TITLE);
        long timeStamp = intent.getLongExtra(AlarmHelper.EXTRA_TIME_STAMP,-1);

        Intent resultIntent = new Intent(context, HomeActivity.class);

        if (MyApplication.isActivityVisible()) {
            resultIntent = intent;
        }

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent= PendingIntent.getActivity(context,(int)timeStamp
                ,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(context);
        builder.setContentTitle("SShedul");
        builder.setContentText(title);
        builder.setSmallIcon(R.mipmap.ic_launcher);

        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setContentIntent(pendingIntent);

        Notification notification=builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager=(NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) timeStamp, notification);

    }
}
