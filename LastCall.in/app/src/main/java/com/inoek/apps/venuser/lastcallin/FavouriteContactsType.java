package com.inoek.apps.venuser.lastcallin;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by harshasanthosh on 14/07/17.
 */

public class FavouriteContactsType implements Parcelable{
    private CallLogs mCallLogs;
    private Integer mCount;

    public FavouriteContactsType(CallLogs callLogs, Integer count)
    {
        mCallLogs = callLogs;
        mCount = count;
    }

    protected FavouriteContactsType(Parcel in) {
        mCallLogs = in.readParcelable(CallLogs.class.getClassLoader());
        mCount = in.readInt();
    }

    public static final Creator<FavouriteContactsType> CREATOR = new Creator<FavouriteContactsType>() {
        @Override
        public FavouriteContactsType createFromParcel(Parcel in) {
            return new FavouriteContactsType(in);
        }

        @Override
        public FavouriteContactsType[] newArray(int size) {
            return new FavouriteContactsType[size];
        }
    };

    public CallLogs getCallLogs()
    {
        return mCallLogs;
    }

    public Integer getCount()
    {
        return mCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mCallLogs, flags);
        dest.writeInt(mCount);
    }
}
