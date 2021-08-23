package com.ubaworld.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.ubaworld.R;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static com.ubaworld.utils.Constants.CONFIG_REMINDER;
import static com.ubaworld.utils.Constants.NOTIFICATION_TYPE;
import static com.ubaworld.utils.Utils.simple_formatDate;

public class NotificationScheduler {

    public static void setReminder(Context context, Class<?> cls, String bill_type, int alarm_id, long interval, String start_Date, String end_Date) {
        String date = simple_formatDate(start_Date);
        String[] start_DateList = date.split("-");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, Integer.parseInt(start_DateList[0]));
        calendar.set(Calendar.MONTH, (Integer.parseInt(start_DateList[1])) - 1);
        calendar.set(Calendar.YEAR, Integer.parseInt(start_DateList[2]));

        calendar.set(Calendar.HOUR_OF_DAY, 5);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Intent intent1 = new Intent(context, cls);
        intent1.putExtra("alarm_id", alarm_id);
        intent1.putExtra("bill_type", bill_type);
        intent1.putExtra("end_date", simple_formatDate(end_Date));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm_id, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
    }

    public static void cancelReminder(Context context, Class<?> cls, int alarm_id) {
        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm_id, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public static void showNotification(Context context, Class<?> cls, String billType, int alarm_id) {
        Intent notificationIntent = new Intent(context, cls);
        notificationIntent.putExtra(NOTIFICATION_TYPE, CONFIG_REMINDER);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, alarm_id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "NotificationManager_00";
        String channelName = "NotificationManager";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new
                    NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText("Reminder " + billType + " Bill")
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000});

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.setSmallIcon(R.drawable.ic_notification);
        } else {
            notification.setSmallIcon(R.mipmap.ic_launcher);
        }

        manager.notify(alarm_id, notification.build());
    }

}
