package com.ubaworld.model;

public class ReminderData {

    private int id;
    private String billType;
    private String amount;
    private String date;
    private String repeatOption;
    private String endDate;
    private int alarm_Id;
    private long interval;
    private long time;

    public ReminderData(int id, String billType, String amount, String date, String repeatOption, String endDate, int alarm_Id, long interval,
                        long time) {
        this.id = id;
        this.billType = billType;
        this.amount = amount;
        this.date = date;
        this.repeatOption = repeatOption;
        this.endDate = endDate;
        this.alarm_Id = alarm_Id;
        this.interval = interval;
        this.time = time;
    }

    public ReminderData(String billType, String amount, String date, String repeatOption, String endDate, int alarm_Id, long interval, long time) {
        this.billType = billType;
        this.amount = amount;
        this.date = date;
        this.repeatOption = repeatOption;
        this.endDate = endDate;
        this.alarm_Id = alarm_Id;
        this.interval = interval;
        this.time = time;
    }

    public ReminderData() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRepeatOption() {
        return repeatOption;
    }

    public void setRepeatOption(String repeatOption) {
        this.repeatOption = repeatOption;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getAlarm_Id() {
        return alarm_Id;
    }

    public void setAlarm_Id(int alarm_Id) {
        this.alarm_Id = alarm_Id;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
