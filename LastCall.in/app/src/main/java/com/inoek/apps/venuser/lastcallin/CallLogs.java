package com.inoek.apps.venuser.lastcallin;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static java.lang.Long.parseLong;

/**
 * Created by harshasanthosh on 21/06/17.
 */

public class CallLogs implements Parcelable, Comparable<CallLogs> {
    
    private String mName;
    private String mPhotoUri;
    private String mNumber;
    private String mTime;
    private String mDate;
    private String mDuration;
    private String mType;
    private long mDateValue;
    private int mOrigDuration;
    private long mOrigDate;
    private String mLongDateTime;

    protected CallLogs(Parcel in) {
        mName = in.readString();
        mPhotoUri = in.readString();
        mNumber = in.readString();
        mTime = in.readString();
        mDate = in.readString();
        mDuration = in.readString();
        mType = in.readString();
        mDateValue = in.readLong();
        mOrigDuration = in.readInt();
        mOrigDate = in.readLong();
        mLongDateTime = in.readString();
    }

    public static final Creator<CallLogs> CREATOR = new Creator<CallLogs>() {
        @Override
        public CallLogs createFromParcel(Parcel in) {
            return new CallLogs(in);
        }

        @Override
        public CallLogs[] newArray(int size) {
            return new CallLogs[size];
        }
    };

    private void getFormattedDate(String datetime)
    {
        String formattedDate = "";
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.valueOf(datetime));
        String callDay = String.valueOf(cal.get(Calendar.DATE));
        int month  = cal.get(Calendar.MONTH);
        String callMonth = cal.getDisplayName(Calendar.MONTH,Calendar.SHORT, Locale.ENGLISH);
        String callYear = String.valueOf(cal.get(Calendar.YEAR));
        formattedDate = callDay+"-"+callMonth+"-"+callYear;
        mDate = formattedDate;
        Date today = null;  // today at midnight
        try {
            today = new SimpleDateFormat("dd-MMM-yyyy").parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mDateValue = today.getTime();
    }

    private void getFormattedTime(String datetime)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.valueOf(datetime));
        mTime = cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE);
    }

    private void getFormattedDuration(String duration)
    {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat sdateFormat = new SimpleDateFormat("HH:mm:ss");
        sdateFormat.setTimeZone(timeZone);
        duration = String.valueOf(parseLong(duration)*1000);
        mDuration = sdateFormat.format(new Date(parseLong(duration)));
    }

    public int getmOrigDuration() {
        return mOrigDuration;
    }

    public void setmOrigDuration(int mOrigDuration) {
        this.mOrigDuration = mOrigDuration;
    }

    public CallLogs(String name, String photoUri, String number, String type, String datetime, String duration)
    {
        mName = (name == null) ? number : name;
        mPhotoUri = photoUri;
        mNumber = (mName.equals(number)) ? "Unsaved" : number;
        mType = type;
        mOrigDuration = Integer.parseInt(duration);
        mLongDateTime = datetime;
        mOrigDate = Long.parseLong(datetime);
        getFormattedTime(datetime);
        getFormattedDate(datetime);
        getFormattedDuration(duration);
    }

    public CallLogs(String name, String number, String photoUri)
    {
        mName = (name == null) ? number : name;
        mPhotoUri = photoUri;
        mNumber = (mName.equals(number)) ? "Unsaved" : number;
        mType = "";
        mTime = "";
        mDate = "";
        mDuration = "";
        mDateValue = 0;
        mOrigDuration=0;
        mOrigDate=0;
    }

    public String getmName() {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public void setmNumber(String number)
    {
        mNumber = number;
    }

    public void setmPhotoUri(String photoUri)
    {
        mPhotoUri = photoUri;
    }

    public String getmPhotoUri() {
        return mPhotoUri;
    }

    public String getmNumber() {
        return mNumber;
    }

    public String getmTime() {
        return mTime;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmDuration() {
        return mDuration;
    }

    public long getDateValue()
    {
        return mDateValue;
    }

    public String getmType()
    {
        return mType;
    }

    public long getmOrigDate() {
        return mOrigDate;
    }

    public String getmLongDateTime() {
        return mLongDateTime;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mPhotoUri);
        dest.writeString(mNumber);
        dest.writeString(mTime);
        dest.writeString(mDate);
        dest.writeString(mDuration);
        dest.writeString(mType);
        dest.writeLong(mDateValue);
        dest.writeInt(mOrigDuration);
        dest.writeLong(mOrigDate);
        dest.writeString(mLongDateTime);
    }

    @Override
    public int compareTo(@NonNull CallLogs o) {
        long diff = o.getmOrigDate()-this.getmOrigDate();
        if(diff > 0)
            return 1;
        else
            return -1;
    }
}
