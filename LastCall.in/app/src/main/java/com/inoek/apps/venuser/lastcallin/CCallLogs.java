package com.inoek.apps.venuser.lastcallin;

/**
 * Created by harshasanthosh on 08/10/17.
 */

public class CCallLogs {
    private String number;
    private int type;
    private int duration;
    private long date;

    public CCallLogs()
    {
        number = null;
        type = 0;
        duration = 0;
        date = 0;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
