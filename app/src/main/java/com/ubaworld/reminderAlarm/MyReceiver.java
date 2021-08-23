package com.ubaworld.reminderAlarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ubaworld.activity.Dashboard;
import com.ubaworld.activity.SplashActivity;
import com.ubaworld.model.ReminderData;
import com.ubaworld.utils.DBHelper;
import com.ubaworld.utils.LogUtils;
import com.ubaworld.utils.NotificationScheduler;
import com.ubaworld.utils.Utils;

import java.util.List;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DBHelper dbHelper = new DBHelper(context);
        List<ReminderData> list = dbHelper.getAllReminder();
        int alarm_id = intent.getExtras().getInt("alarm_id");
        String bill_type = intent.getExtras().getString("bill_type");
        String end_Date = intent.getExtras().getString("end_date");
        LogUtils.e("alarm_id", "---> " + alarm_id);
        LogUtils.e("end_date", "---> " + end_Date);

        if (!TextUtils.isEmpty(end_Date))
            if (end_Date.equalsIgnoreCase(Utils.getCurrentDate()))
                NotificationScheduler.cancelReminder(context, MyReceiver.class, alarm_id);

        NotificationScheduler.showNotification(context, SplashActivity.class, bill_type, alarm_id);
    }
}