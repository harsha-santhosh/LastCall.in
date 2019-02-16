package com.inoek.apps.venuser.lastcallin;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by harshasanthosh on 26/08/17.
 */

public class CollectFavouritesAsync extends AsyncTask<String, Void, String> {

    private MyProgressDialog progressDialog;
    private ContactUpdaterInterface<ArrayList<FavouriteContactsType>> contactUpdaterObj;
    private ArrayList<FavouriteContactsType> favouriteContactsArrayList;
    private FavouriteContacts favouriteContacts;

    public CollectFavouritesAsync(FavouriteContacts favContacts, ContactUpdaterInterface<ArrayList<FavouriteContactsType>> contactUpdaterRef, MyProgressDialog pD)
    {
        progressDialog = pD;
        contactUpdaterObj = contactUpdaterRef;
        favouriteContacts = favContacts;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setText("Updating Favourite Contacts...");
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        progressDialog.dismiss();
        contactUpdaterObj.onContactsUpdate(favouriteContactsArrayList);
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... params) {
        favouriteContactsArrayList = favouriteContacts.getFavouriteContacts();
        return null;
    }
}
