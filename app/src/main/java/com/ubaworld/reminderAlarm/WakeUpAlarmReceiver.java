package com.ubaworld.reminderAlarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ubaworld.model.ReminderData;
import com.ubaworld.utils.DBHelper;
import com.ubaworld.utils.NotificationScheduler;

import java.util.List;

public class WakeUpAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            DBHelper dbHelper = new DBHelper(context);
            List<ReminderData> list = dbHelper.getAllReminder();

            for (int i = 0; i < list.size(); i++) {
                int alarm_Id = list.get(i).getAlarm_Id();
                String startDate = list.get(i).getDate();
                String endDate = list.get(i).getEndDate();
                long interval = list.get(i).getInterval();

                NotificationScheduler.setReminder(context, MyReceiver.class, list.get(i).getBillType(), alarm_Id, interval, startDate, endDate);
            }
        }
    }

}