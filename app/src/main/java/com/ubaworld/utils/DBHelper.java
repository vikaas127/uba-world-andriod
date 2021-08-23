package com.ubaworld.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ubaworld.model.ReminderData;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "reminder_Alarm";
    private static final String TABLE_REMINDER = "reminder";
    private static final String KEY_ID = "id";
    private static final String KEY_BILL = "bill";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_REPEAT_OPTION = "repeat_option";
    private static final String KEY_END_DATE = "end_date";
    private static final String KEY_ALARM_ID = "alarm_id";
    private static final String KEY_INTERVAL = "interval";
    private static final String KEY_TIME = "time";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_REMINDER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_BILL + " TEXT," + KEY_AMOUNT + " TEXT,"
                + KEY_START_DATE + " TEXT," + KEY_REPEAT_OPTION + " TEXT," + KEY_END_DATE + " TEXT,"
                + KEY_ALARM_ID + " TEXT," + KEY_INTERVAL + " TEXT," + KEY_TIME + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDER);
        onCreate(db);
    }

    public void addReminder(ReminderData data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BILL, data.getBillType());
        values.put(KEY_AMOUNT, data.getAmount());
        values.put(KEY_START_DATE, data.getDate());
        values.put(KEY_REPEAT_OPTION, data.getRepeatOption());
        values.put(KEY_END_DATE, data.getEndDate());
        values.put(KEY_ALARM_ID, data.getAlarm_Id());
        values.put(KEY_INTERVAL, data.getInterval());
        values.put(KEY_TIME, data.getTime());

        db.insert(TABLE_REMINDER, null, values);
        db.close();
    }

    public ReminderData getReminder(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_REMINDER, new String[] { KEY_ID,
                        KEY_BILL, KEY_AMOUNT, KEY_START_DATE, KEY_REPEAT_OPTION, KEY_END_DATE, KEY_ALARM_ID, KEY_INTERVAL, KEY_TIME },KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ReminderData data = new ReminderData(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),
                Integer.parseInt(cursor.getString(6)), Long.parseLong(cursor.getString(7)),  Long.parseLong(cursor.getString(8)));

        return data;
    }

    public List<ReminderData> getAllReminder() {
        List<ReminderData> reminderList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_REMINDER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ReminderData data = new ReminderData();
                data.setId(Integer.parseInt(cursor.getString(0)));
                data.setBillType(cursor.getString(1));
                data.setAmount(cursor.getString(2));
                data.setDate(cursor.getString(3));
                data.setRepeatOption(cursor.getString(4));
                data.setEndDate(cursor.getString(5));
                data.setAlarm_Id(Integer.parseInt(cursor.getString(6)));
                data.setInterval(Long.parseLong(cursor.getString(7)));
                data.setTime(Long.parseLong(cursor.getString(8)));

                reminderList.add(data);
            } while (cursor.moveToNext());
        }

        return reminderList;
    }

    public int updateReminder(ReminderData data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BILL, data.getBillType());
        values.put(KEY_AMOUNT, data.getAmount());
        values.put(KEY_START_DATE, data.getDate());
        values.put(KEY_REPEAT_OPTION, data.getRepeatOption());
        values.put(KEY_END_DATE, data.getEndDate());
        values.put(KEY_ALARM_ID, data.getAlarm_Id());
        values.put(KEY_INTERVAL, data.getInterval());
        values.put(KEY_TIME, data.getTime());

        return db.update(TABLE_REMINDER, values, KEY_ID + " = ?",
                new String[] {String.valueOf(data.getId()) });
    }

    public void deleteReminder(ReminderData data) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REMINDER, KEY_ID + " = ?",
                new String[] {String.valueOf(data.getId()) });
        db.close();
    }

    public int getReminderCount() {
        String countQuery = "SELECT * FROM " + TABLE_REMINDER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

}
