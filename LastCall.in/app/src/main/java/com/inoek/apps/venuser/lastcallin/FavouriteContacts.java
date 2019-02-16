package com.inoek.apps.venuser.lastcallin;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import static com.inoek.apps.venuser.lastcallin.BaseActivity.preferences;
import static com.inoek.apps.venuser.lastcallin.BaseActivity.preferencesEditor;

/**
 * Created by harshasanthosh on 14/07/17.
 */

public class FavouriteContacts implements Parcelable {

    private ArrayList<FavouriteContactsType> mFavouriteContactsArrayList;
    private ArrayList<CallLogs> mCallLogsArrayList;
    private String favContacts, notFavContacts;

    public FavouriteContacts(ArrayList<CallLogs> callLogsArrayList)
    {
        mCallLogsArrayList = callLogsArrayList;
        mFavouriteContactsArrayList = new ArrayList<>();
        favContacts = preferences.getString("favouritesString","");
        notFavContacts = preferences.getString("notFavouritesString","");
        loadFavourites();
    }

    protected FavouriteContacts(Parcel in) {
        mFavouriteContactsArrayList = in.createTypedArrayList(FavouriteContactsType.CREATOR);
        mCallLogsArrayList = in.createTypedArrayList(CallLogs.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mFavouriteContactsArrayList);
        dest.writeTypedList(mCallLogsArrayList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FavouriteContacts> CREATOR = new Creator<FavouriteContacts>() {
        @Override
        public FavouriteContacts createFromParcel(Parcel in) {
            return new FavouriteContacts(in);
        }

        @Override
        public FavouriteContacts[] newArray(int size) {
            return new FavouriteContacts[size];
        }
    };

    private boolean isExists(CallLogs callLogs)
    {
        boolean exist = false;
        for(FavouriteContactsType favouriteContactsType : mFavouriteContactsArrayList)
        {
            if(callLogs.getmNumber().equals("Unsaved"))
            {
                if(callLogs.getmName().equals(favouriteContactsType.getCallLogs().getmName()))
                {
                    exist = true;
                    break;
                }
            }
            else
            {
                if(callLogs.getmNumber().equals(favouriteContactsType.getCallLogs().getmNumber()))
                {
                    exist = true;
                    break;
                }
            }
        }
        return exist;
    }

    private ArrayList<FavouriteContactsType> splitFavouriteString(String data)
    {
        ArrayList<FavouriteContactsType> contactDataArrayList = new ArrayList<>();
        String[] literals = data.split(";");
        for(int i = 0; i < literals.length; i++)
        {
            String[] subLiterals = literals[i].split(",");
            if(subLiterals.length==4) {
                FavouriteContactsType fct = new FavouriteContactsType(new CallLogs(subLiterals[0], subLiterals[1], subLiterals[2]), Integer.parseInt(subLiterals[3]));
                contactDataArrayList.add(fct);
            }
        }
        return contactDataArrayList;
    }


    private void retainOnlyFavourites()
    {
        ArrayList<FavouriteContactsType> favouriteContactData = splitFavouriteString(favContacts);
        ArrayList<FavouriteContactsType> notFavouriteContactData = splitFavouriteString(notFavContacts);
        ArrayList<FavouriteContactsType> aggregatedContactData = new ArrayList<>();
        boolean matchFound;
        for( FavouriteContactsType favCd : favouriteContactData)
        {
            matchFound = false;
            for(FavouriteContactsType notFavCd : notFavouriteContactData)
            {
                if(favCd.getCallLogs().getmName().equals(notFavCd.getCallLogs().getmName()) && favCd.getCallLogs().getmNumber().equals(notFavCd.getCallLogs().getmNumber()))
                {
                    matchFound = true;
                    break;
                }
            }
            if(!matchFound) {
                aggregatedContactData.add(favCd);
            }
        }
        mFavouriteContactsArrayList = aggregatedContactData;
    }

    private boolean compareTwoPhoneNumbers(String phoneNumber1, String phoneNumber2)
    {
        boolean isEqual = false;
        int high,low;
        String highString, lowString;
        if(phoneNumber1.length() >= phoneNumber2.length())
        {
            high = phoneNumber1.length();
            highString = phoneNumber1;
            low = phoneNumber2.length();
            lowString = phoneNumber2;
        }
        else
        {
            high = phoneNumber2.length();
            highString = phoneNumber2;
            low = phoneNumber1.length();
            lowString = phoneNumber1;
        }

        if(high == low || high-low < 4)
        {
            if(highString.contains(lowString))
                isEqual = true;
        }

        return isEqual;
    }

    private void loadFavourites()
    {
        if(preferences!=null) {
            favContacts = preferences.getString("favouritesString", "");
            notFavContacts = preferences.getString("notFavouritesString", "");
        }
        for(int i = 0; i < mCallLogsArrayList.size(); i++)
        {
            CallLogs callLogsA = mCallLogsArrayList.get(i);
            if(!isExists(callLogsA)) {
                int count = 0;
                for (int j = 0; j < mCallLogsArrayList.size(); j++) {
                    CallLogs callLogsB = mCallLogsArrayList.get(j);
                    if (callLogsA.getmNumber().equals("Unsaved")) {
                        if (compareTwoPhoneNumbers(callLogsA.getmName(),callLogsB.getmName())) {
                            count++;
                        }
                    } else {
                        if (compareTwoPhoneNumbers(callLogsA.getmNumber(),callLogsB.getmNumber())) {
                            count++;
                        }
                    }
                }
                if (count >= 3) {
                    String fc = callLogsA.getmName()+","+callLogsA.getmNumber()+","+callLogsA.getmPhotoUri()+","+String.valueOf(count)+";";
                    if(!favContacts.contains(callLogsA.getmName()))
                    {
                        favContacts+=fc;
                        preferencesEditor.putString("favouritesString",favContacts);
                        preferencesEditor.apply();
                    }
                }
            }
        }
        retainOnlyFavourites();
    }

    public void setmFavouriteContactsArrayList(ArrayList<FavouriteContactsType> favouriteContactsArrayList)
    {
        mFavouriteContactsArrayList = favouriteContactsArrayList;
    }

    public ArrayList<FavouriteContactsType> getFavouriteContacts()
    {
        mFavouriteContactsArrayList.clear();
        loadFavourites();
        return mFavouriteContactsArrayList;
    }

}
