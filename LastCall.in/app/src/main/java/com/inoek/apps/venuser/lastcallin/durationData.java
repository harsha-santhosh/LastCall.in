package com.inoek.apps.venuser.lastcallin;

/**
 * Created by harshasanthosh on 16/11/17.
 */

public class durationData {
    private String duration;
    private String date;
    private String time;
    private int type;
    private long dt;
    private long dr;
    public durationData(String _duration, String _date, int _type, String _time, long _dt, long _dr)
    {
        duration = _duration;
        date = _date;
        type = _type;
        time = _time;
        dt = _dt;
        dr = _dr;
    }
    public String getDuration()
    {
        return duration;
    }
    public String getDate()
    {
        return date;
    }
    public int getType()
    {
        return type;
    }
    public String getTime()
    {
        return time;
    }

    public long getDt() {
        return dt;
    }

    public long getDr() {
        return dr;
    }
}
