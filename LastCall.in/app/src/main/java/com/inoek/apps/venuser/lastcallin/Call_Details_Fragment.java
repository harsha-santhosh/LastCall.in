package com.inoek.apps.venuser.lastcallin;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 */
public class Call_Details_Fragment extends Fragment {
    private TextView mName;
    private TextView mNumber;
    private TextView mMissed;
    private TextView mRejected;
    private TextView mIncoming;
    private TextView mOutgoing;
    private TextView mIncomingDur;
    private TextView mOutgoingDur;
    private ImageView mProfilePic;
    private ListView mHistoryList;
    private Person person;
    private DurationDataListViewAdapter durationDataListViewAdapter;
    private personUpdaterInterface pUpdaterInterface;
    private CallPauser callPauserInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_call_details, container, false);
        mName = (TextView) rootView.findViewById(R.id.call_details_frag_name);
        mNumber = (TextView) rootView.findViewById(R.id.call_details_frag_number);
        mMissed = (TextView) rootView.findViewById(R.id.call_details_frag_missed);
        mRejected = (TextView) rootView.findViewById(R.id.call_details_frag_rejected);
        mIncoming = (TextView) rootView.findViewById(R.id.call_details_frag_incoming);
        mOutgoing = (TextView) rootView.findViewById(R.id.call_details_frag_outgoing);
        mIncomingDur = (TextView) rootView.findViewById(R.id.call_details_frag_incoming_dur);
        mOutgoingDur = (TextView) rootView.findViewById(R.id.call_details_frag_outgoing_dur);
        mProfilePic = (ImageView) rootView.findViewById(R.id.call_details_frag_img);
        Button CallButton = (Button) rootView.findViewById(R.id.call_details_frag_button);
        mHistoryList = (ListView) rootView.findViewById(R.id.callHistory);
        person = BaseActivity.personOb;
        pUpdaterInterface = BaseActivity.pUpdaterInterface;
        callPauserInterface = BaseActivity.callPauserInterface;
        durationDataListViewAdapter = new DurationDataListViewAdapter(getActivity().getApplicationContext(),
                R.layout.history_item,person.getCallHistory());
        mHistoryList.setAdapter(durationDataListViewAdapter);
        mName.setText(person.getmName());
        mNumber.setText(person.getmPhoneNumber());
        mMissed.setText(String.valueOf(person.getmCallsMissed()));
        mRejected.setText(String.valueOf(person.getmCallsRejected()));
        mIncoming.setText(String.valueOf(person.getmCallsIncoming()));
        mOutgoing.setText(String.valueOf(person.getmCallsOutgoing()));
        mIncomingDur.setText(person.getmIncomingDuration());
        mOutgoingDur.setText(person.getmOutgoingDuration());
        if(person.getmImageUri()!=null) {
            byte[] imageBytes = Base64.decode(person.getmImageUri(), Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            mProfilePic.setImageBitmap(decodedImage);
        }
        CallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (mNumber.getText().toString().contains("Unsaved")) {
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mName.getText().toString())));
                        Toast.makeText(getActivity().getApplicationContext(), "Calling...", Toast.LENGTH_SHORT).show();
                        new CountDownTimer(1000,1000){

                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                callPauserInterface.loadLogs();
                            }
                        }.start();
                    } else {
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mNumber.getText().toString())));
                        Toast.makeText(getActivity().getApplicationContext(), "Calling...", Toast.LENGTH_SHORT).show();
                        new CountDownTimer(1000,1000){

                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                callPauserInterface.loadLogs();
                            }
                        }.start();
                    }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        updateDetails();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void updateDetails() {
        if(person!=null) {
            person = pUpdaterInterface.getUpdatedPersonDetails(person);
            if(person!=null) {
                ArrayList<durationData> durationDataArrayList = person.getCallHistory();
                Collections.sort(durationDataArrayList, new Comparator<durationData>() {
                    public int compare(durationData o1, durationData o2) {
                        return String.valueOf(o2.getDt()).compareTo(String.valueOf(o1.getDt()));
                    }
                });
                durationDataListViewAdapter = new DurationDataListViewAdapter(getActivity().getApplicationContext(),
                        R.layout.history_item, durationDataArrayList);
                mHistoryList.setAdapter(durationDataListViewAdapter);
                mName.setText(person.getmName());
                mNumber.setText(person.getmPhoneNumber());
                mMissed.setText(String.valueOf(person.getmCallsMissed()));
                mRejected.setText(String.valueOf(person.getmCallsRejected()));
                mIncoming.setText(String.valueOf(person.getmCallsIncoming()));
                mOutgoing.setText(String.valueOf(person.getmCallsOutgoing()));
                mIncomingDur.setText(person.getmIncomingDuration());
                mOutgoingDur.setText(person.getmOutgoingDuration());
                if (person.getmImageUri() != null) {
                    byte[] imageBytes = Base64.decode(person.getmImageUri(), Base64.DEFAULT);
                    Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    mProfilePic.setImageBitmap(decodedImage);
                }
            }
        }
    }
}
