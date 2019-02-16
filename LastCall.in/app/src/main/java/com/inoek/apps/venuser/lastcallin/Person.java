package com.inoek.apps.venuser.lastcallin;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import static java.lang.Long.parseLong;

/**
 * Created by harshasanthosh on 21/05/17.
 */

public class Person implements Parcelable{
    private String mName;
    private String mPhoneNumber;
    private String mImageUri;
    private int mType;
    private long mCallsOutgoing;
    private long mCallsIncoming;
    private long mCallsMissed;
    private long mCallsRejected;
    private String mIncomingDuration;
    private String mOutgoingDuration;
    private ArrayList<durationData> durationDataArrayList;

    private void replaceSpaces()
    {
        mPhoneNumber = mPhoneNumber.replace(" ","");
    }

    public Person()
    {
        mName = null;
        mPhoneNumber = null;
        mType = 0;
        mImageUri = null;
        mCallsOutgoing = 0;
        mCallsIncoming = 0;
        mCallsMissed = 0;
        mCallsRejected = 0;
        mIncomingDuration="0";
        mOutgoingDuration="0";
        durationDataArrayList = new ArrayList<>();
    }

    public Person(String name, String phoneNumber, String imageUri, int type)
    {
        mName = name;
        mPhoneNumber = phoneNumber;
        mImageUri = imageUri;
        mType = type;
        mCallsOutgoing = 0;
        mCallsIncoming = 0;
        mCallsMissed = 0;
        mCallsRejected = 0;
        mIncomingDuration="0";
        mOutgoingDuration="0";
        durationDataArrayList = new ArrayList<>();
    }

    public Person(String name, String phoneNumber)
    {
        mName = name;
        mPhoneNumber = phoneNumber;
        mImageUri = null;
        mType = 7;
        mCallsOutgoing = 0;
        mCallsIncoming = 0;
        mCallsMissed = 0;
        mCallsRejected = 0;
        mIncomingDuration="0";
        mOutgoingDuration="0";
        durationDataArrayList = new ArrayList<>();

    }

    public long getmCallsOutgoing() {
        return mCallsOutgoing;
    }

    public void setmCallsOutgoing(long mCallsOutgoing) {
        this.mCallsOutgoing = mCallsOutgoing;
    }

    public long getmCallsIncoming() {
        return mCallsIncoming;
    }

    public void setmCallsIncoming(long mCallsIncoming) {
        this.mCallsIncoming = mCallsIncoming;
    }

    public long getmCallsMissed() {
        return mCallsMissed;
    }

    public void setmCallsMissed(long mCallsMissed) {
        this.mCallsMissed = mCallsMissed;
    }

    public long getmCallsRejected() {
        return mCallsRejected;
    }

    public void setmCallsRejected(long mCallsRejected) {
        this.mCallsRejected = mCallsRejected;
    }

    public String getmIncomingDuration() {
        return mIncomingDuration;
    }

    public void setmIncomingDuration(String mIncomingDuration) {
        this.mIncomingDuration = mIncomingDuration;
    }

    public String getmOutgoingDuration() {
        return mOutgoingDuration;
    }

    public void setmOutgoingDuration(String mOutgoingDuration) {
        this.mOutgoingDuration = mOutgoingDuration;
    }

    public boolean addCallHistory(String duration, String date, int type, String time, long dt, long dr)
    {
        if(!durationDataArrayList.isEmpty()) {
            boolean isPresent = false;
            for (durationData d : durationDataArrayList) {
                if(d.getDt()==dt && d.getDr()==dr)
                {
                    isPresent = true;
                    break;
                }
            }
            if(!isPresent)
            {
                durationDataArrayList.add(new durationData(duration, date, type, time, dt, dr));
                return true;
            }
        }
        else {
            durationDataArrayList.add(new durationData(duration, date, type, time, dt, dr));
            return true;
        }
        return false;
    }

    public ArrayList<durationData> getCallHistory()
    {
        return durationDataArrayList;
    }

    public void updateCallStatistics(Integer type, String duration)
    {
        switch (type)
        {
            case 1:
                mCallsIncoming++;
                BaseActivity.mCallsIncoming++;
                mIncomingDuration = updateDuration(mIncomingDuration,duration,0);
                break;
            case 2:
                mCallsOutgoing++;
                BaseActivity.mCallsOutgoing++;
                mOutgoingDuration = updateDuration(mOutgoingDuration,duration,1);
                break;
            case 3:
                BaseActivity.mCallsMissed++;
                mCallsMissed++;
                break;
            case 5:
                BaseActivity.mCallsRejected++;
                mCallsRejected++;
                break;
        }
    }

    // type = 0 : incoming
    // type = 1 : outgoing
    private String updateDuration(String existingDuration, String duration, int type)
    {
        String overallDuration = existingDuration;
        long existingD = 0;
        if(!duration.equals("0")) {
            if(!existingDuration.equals("0"))
            {
                TimeZone ntimeZone = TimeZone.getTimeZone("UTC");
                SimpleDateFormat stimeFormat = new SimpleDateFormat("HH:mm:ss");
                stimeFormat.setTimeZone(ntimeZone);
                try {
                    Date d = stimeFormat.parse(existingDuration);
                    existingD = d.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else {
                existingD = parseLong(existingDuration) * 1000;
            }
            long currentD = parseLong(duration) * 1000;
            long updatedDuration = existingD + currentD;
            if(type == 0)
            {
                BaseActivity.mIncomingDuration+=currentD;
            }
            if(type == 1)
            {
                BaseActivity.mOutgoingDuration+=currentD;
            }
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            SimpleDateFormat sdateFormat = new SimpleDateFormat("HH:mm:ss");
            sdateFormat.setTimeZone(timeZone);
            overallDuration = sdateFormat.format(new Date(updatedDuration));
        }
        return overallDuration;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    protected Person(Parcel in) {
        mName = in.readString();
        mPhoneNumber = in.readString();
        mImageUri = in.readString();
        mType = in.readInt();
        mCallsOutgoing = in.readLong();
        mCallsIncoming = in.readLong();
        mCallsMissed = in.readLong();
        mCallsRejected = in.readLong();
        mIncomingDuration= in.readString();
        mOutgoingDuration= in.readString();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public String getmImageUri() {
        return mImageUri;
    }

    public void setmImageUri(String mImageUri) {
        this.mImageUri = mImageUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mPhoneNumber);
        dest.writeString(mImageUri);
        dest.writeInt(mType);
        dest.writeLong(mCallsOutgoing);
        dest.writeLong(mCallsIncoming);
        dest.writeLong(mCallsMissed);
        dest.writeLong(mCallsRejected);
        dest.writeString(mIncomingDuration);
        dest.writeString(mOutgoingDuration);
    }
}
