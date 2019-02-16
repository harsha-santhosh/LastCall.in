package com.inoek.apps.venuser.lastcallin;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

public class LogsFragment extends Fragment {

    private View main_view;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private ArrayList<CallLogs> callLogsArrayList;
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEditor;
    private ContactsObjectObtainer contactsObjectObtainer;
    private DetailsFragmentInterface detailsFragmentInterface;
    private loadMoreCallLogs loadMoreCallLogsInterfaceObj;
    private FloatingActionButton fab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        preferences = getActivity().getApplicationContext().getSharedPreferences("lastcall",0);
        preferencesEditor = preferences.edit();
        contactsObjectObtainer = BaseActivity.contactsObjectObtainer;
        loadMoreCallLogsInterfaceObj = BaseActivity.loadMoreCallLogsInterface;
        super.onCreate(savedInstanceState);
    }

    protected CallLogs getChild(String title, int position)
    {
        LinkedList<CallLogs> callLogsList = new LinkedList<>();
        for(CallLogs callLog : callLogsArrayList)
        {
            if(callLog.getmDate().equals(title))
            {
                callLogsList.add(callLog);
            }
        }
        return callLogsList.get(position);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_logs_fragment, container, false);
        callLogsArrayList = new ArrayList<>();
        main_view = rootView;
        detailsFragmentInterface = BaseActivity.detailsFragmentInterface;
        context = getActivity().getApplicationContext();
        callLogsArrayList =  BaseActivity.callLogsArrayList;
        expandableListView = (ExpandableListView) main_view.findViewById(R.id.expandableListView);
        fab = (FloatingActionButton) main_view.findViewById(R.id.fab);
        expandableListAdapter = new CustomExpandableListAdapter(context, callLogsArrayList);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
                int expandIndex = i;
                preferencesEditor.putInt("expandIndex",expandIndex);
                preferencesEditor.apply();
            }
        });


        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
                preferencesEditor.putInt("expandIndex",0);
                preferencesEditor.apply();
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                CallLogs callLogs = (CallLogs) getChild(CustomExpandableListAdapter.getTitles().get(groupPosition), childPosition);
                if (callLogs.getmNumber().contains("Unsaved")) {
                    Person p = contactsObjectObtainer.getContactsObject(callLogs.getmName());
                    detailsFragmentInterface.showDetailsView(p);
                } else {
                    Person p = contactsObjectObtainer.getContactsObject(callLogs.getmNumber());
                    detailsFragmentInterface.showDetailsView(p);
                }
                return false;
            }
        });
        int index = preferences.getInt("expandableindex",0);
        int top = preferences.getInt("expandabletop",0);
        int expandIndex = preferences.getInt("expandIndex",0);
        if(!callLogsArrayList.isEmpty()) {
            if(expandableListView.getCount() > 0)
                expandableListView.expandGroup(0);
            expandableListView.expandGroup(expandIndex);
            expandableListView.setSelectionFromTop(index, top);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(),"Load 100 more logs..",Toast.LENGTH_SHORT).show();
                loadMoreCallLogsInterfaceObj.load100MoreLogs();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        int index = preferences.getInt("expandableindex",0);
        int top = preferences.getInt("expandabletop",0);
        int expandIndex = preferences.getInt("expandIndex",0);
        if(!callLogsArrayList.isEmpty()) {
            expandableListView.setSelectionFromTop(index, top);
            expandableListView.expandGroup(expandIndex);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        int index = expandableListView.getFirstVisiblePosition();
        View v = expandableListView.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - expandableListView.getPaddingTop());
        preferencesEditor.putInt("expandableindex",index);
        preferencesEditor.putInt("expandabletop",top);
        preferencesEditor.apply();
        super.onPause();
    }
}
