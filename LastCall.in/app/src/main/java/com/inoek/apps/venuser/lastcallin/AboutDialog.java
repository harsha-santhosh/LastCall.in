package com.inoek.apps.venuser.lastcallin;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class AboutDialog extends DialogFragment {


    public AboutDialog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_dialog, container, false);
        getDialog().setTitle("About this App");
        return view;
    }

}
