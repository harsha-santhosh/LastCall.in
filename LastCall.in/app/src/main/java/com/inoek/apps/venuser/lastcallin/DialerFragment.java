package com.inoek.apps.venuser.lastcallin;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;

/**
 * Created by Harsha on 7/2/2017.
 */

public class DialerFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener{
    private SlidingUpPanelLayout mLayout;
    private static final String TAG = "DialerFragment";

    private ArrayList<Person> array_list;
    private ListView listview;
    private ContactsListViewAdapter contactsListViewAdapter;
    private LinkedHashMap<String,Integer> mContactsIndexMap;
    private EditText screen;
    private boolean zeroPressed;
    private SharedPreferences preferences;
    private LinearLayout dragView;
    private SharedPreferences.Editor preferencesEditor;
    boolean isExpanded;
    private String savedtext;
    private CallPauser callPauserObj;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        preferences = getActivity().getApplicationContext().getSharedPreferences("lastcall",0);
        preferencesEditor = preferences.edit();
        super.onCreate(savedInstanceState);
        zeroPressed = false;
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_dialer_fragment, container, false);
        callPauserObj = BaseActivity.callPauserInterface;
        initializeView(rootView);
        init(rootView);   // call init method
        setListview();    // call setListview method
        panelListener();  // Call paneListener method
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    public void init(View view) {
        mLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
        dragView = (LinearLayout) view.findViewById(R.id.dragView);
        dragView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        listview = (ListView) view.findViewById(R.id.list);
        array_list =  BaseActivity.contactArrayList;
        mContactsIndexMap = new LinkedHashMap<>();
        setUpContacts();
    }

    private void setUpContacts()
    {
        contactsListViewAdapter = new ContactsListViewAdapter(getActivity().getApplicationContext(),R.layout.activity_list_item,array_list,mContactsIndexMap);
        contactsListViewAdapter.sort(new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                return o1.getmName().compareTo(o2.getmName());
            }
        });
        listview.setAdapter(contactsListViewAdapter);
    }

    public void setListview() {
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                Person _person = (Person) contactsListViewAdapter.getItem(position);
                screen.setText(_person.getmPhoneNumber());
                savedtext = _person.getmPhoneNumber();
            }
        });
        contactsListViewAdapter = new ContactsListViewAdapter(getActivity().getApplicationContext(),R.layout.activity_list_item,array_list,mContactsIndexMap);
        listview.setAdapter(contactsListViewAdapter);
        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });
        screen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                contactsListViewAdapter.getFilter().filter(screen.getText());
            }
        });
    }

    private void initializeView(View view) {
        screen = (EditText)view.findViewById(R.id.screen);
        screen.requestFocus();
        closeKeyboard(getActivity(), screen.getWindowToken());
        int idList[] = {R.id.screen,R.id.btn1,R.id.btn2,R.id.btn3,
                R.id.btn4,R.id.btn5,R.id.btn6,
                R.id.btn7,R.id.btn8,R.id.btn9,
                R.id.btnDial,R.id.btndel,R.id.btnStar,
                R.id.btnZero,R.id.btnHash};

        for(int d: idList){
            View v = (View)view.findViewById(d);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }
    }

    public void display(String val){
        screen.append(val);
    }

    public void callPhone()
    {
        boolean ussd = false;
        if(screen.getText().toString().isEmpty())
            Toast.makeText(getActivity().getApplicationContext(),"Enter some digits",Toast.LENGTH_SHORT).show();
        else {
            String phoneNumber = screen.getText().toString();
            if(phoneNumber.contains("#"))
            {
                ussd = true;
            }
            phoneNumber = phoneNumber.replaceAll("#",Uri.encode("#"));
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phoneNumber)));
            Toast.makeText(getActivity().getApplicationContext(), "Calling...", Toast.LENGTH_SHORT).show();
            screen.getText().clear();
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            savedtext = screen.getText().toString();
            isExpanded = false;
            if(!ussd)
            {
                BaseActivity.fragId = 3;
            }
            else
            {
                new CountDownTimer(500, 1000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        callPauserObj.pauseTheApp();
                    }
                }.start();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn1:
                display("1");
                break;
            case R.id.btn2:
                display("2");
                break;
            case R.id.btn3:
                display("3");
                break;
            case R.id.btn4:
                display("4");
                break;
            case R.id.btn5:
                display("5");
                break;
            case R.id.btn6:
                display("6");
                break;
            case R.id.btn7:
                display("7");
                break;
            case R.id.btn8:
                display("8");
                break;
            case R.id.btn9:
                display("9");
                break;
            case R.id.btnZero:
                if(zeroPressed)
                    zeroPressed = false;
                else
                display("0");
                break;
            case R.id.btnStar:
                display("*");
                break;
            case R.id.btnHash:
                display("#");
                break;
            case R.id.btnDial:
                callPhone();
                break;
            case R.id.btndel:
                int length = screen.getText().length();
                if (length > 0) {
                    screen.getText().delete(length - 1, length);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch(v.getId()){
            case R.id.btnZero:
                zeroPressed = true;
                display("+");
                break;
            case R.id.btndel:
                screen.getText().clear();
                savedtext = screen.getText().toString();
                break;
        }
        return true;
    }

    public void panelListener() {

        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.SimplePanelSlideListener() {

            // During the transition of expand and collapse onPanelSlide function will be called.
            public void onPanelSlide(View panel, float slideOffset) {
                Log.e(TAG, "onPanelSlide, offset " + slideOffset);
            }
            public void onPanelExpanded(View panel) {
                Log.e(TAG, "onPanelExpanded");
                savedtext = screen.getText().toString();
                isExpanded = true;
            }
            public void onPanelCollapsed(View panel) {
                Log.e(TAG, "onPanelCollapsed");
                savedtext = screen.getText().toString();
                isExpanded = false;
            }
            public void onPanelAnchored(View panel) {
                Log.e(TAG, "onPanelAnchored");
            }
            public void onPanelHidden(View panel) {
                Log.e(TAG, "onPanelHidden");
            }
        });
    }

    @Override
    public void onResume() {
        int index = preferences.getInt("listindex",0);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        isExpanded = preferences.getBoolean("isExpanded",true);
        if(isExpanded){
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
        else{
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        savedtext = preferences.getString("savedtext","");
        screen.setText(savedtext);
        listview.setSelection(index);
        super.onResume();
    }

    @Override
    public void onPause() {
        int index = listview.getFirstVisiblePosition();
        savedtext = screen.getText().toString();
        preferencesEditor.putInt("listindex",index);
        preferencesEditor.putBoolean("isExpanded",isExpanded);
        preferencesEditor.putString("savedtext",savedtext);
        preferencesEditor.apply();
        super.onPause();
    }
}
