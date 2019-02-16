package com.inoek.apps.venuser.lastcallin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<CallLogs> mCallLogsArrayList;
    private static ArrayList<String> mExpandableListTitle;

    private void loadAllDates()
    {
        LinkedHashMap<Long, String> titleList = new LinkedHashMap<>();
        Iterator<CallLogs> callLogsIterator = mCallLogsArrayList.iterator();
        while(callLogsIterator.hasNext())
        {
           CallLogs callLogsObj = (CallLogs) callLogsIterator.next();
            titleList.put(callLogsObj.getDateValue(),callLogsObj.getmDate());
        }
        mExpandableListTitle = new ArrayList<>();
        for(String title : titleList.values())
        {
            mExpandableListTitle.add(title);
        }
    }

    public static ArrayList<String> getTitles()
    {
        return mExpandableListTitle;
    }

    public CustomExpandableListAdapter(Context context, ArrayList<CallLogs> callLogsLinkedList) {
        this.context = context;
        this.mCallLogsArrayList = callLogsLinkedList;
        loadAllDates();
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        String title = mExpandableListTitle.get(listPosition);
        LinkedList<CallLogs> callLogsList = new LinkedList<>();
        for(CallLogs callLog : mCallLogsArrayList)
        {
            if(callLog.getmDate().equals(title))
            {
                callLogsList.add(callLog);
            }
        }
        return callLogsList.get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    private int getCallTypeDrawable(Integer type)
    {
        int resource_id = 0;
        switch (type)
        {
            case 1:
                resource_id = R.drawable.ic_call_received_black_24dp;
                break;
            case 2:
                resource_id = R.drawable.ic_call_made_black_24dp;
                break;
            case 3:
                resource_id = R.drawable.ic_call_missed_black_24dp;
                break;
            case 4:
                resource_id = R.drawable.ic_voicemail_black_24dp;
                break;
            case 5:
                resource_id = R.drawable.ic_cancel_black_24dp;
                break;
            case 6:
                resource_id = R.drawable.ic_block_black_24dp;
                break;
        }
        return resource_id;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final CallLogs callLog = (CallLogs) getChild(listPosition, expandedListPosition);
        final String profileImageUri = callLog.getmPhotoUri();
        final String callName = callLog.getmName();
        final String callTime = callLog.getmTime();
        final String callNumber = callLog.getmNumber();
        final String callDuration = callLog.getmDuration();
        final Integer callType = Integer.parseInt(callLog.getmType());
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView nameTextView = (TextView) convertView
                .findViewById(R.id.call_name);
        nameTextView.setText(callName);
        CircleImageView profileImage = (CircleImageView) convertView.findViewById(R.id.profile_image);
        if(profileImageUri!=null) {
            byte[] imageBytes = Base64.decode(profileImageUri, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            profileImage.setImageBitmap(decodedImage);
        }
        else
        {
            profileImage.setImageResource(R.mipmap.ic_launcher_round);
        }
        TextView timeTextView = (TextView) convertView
                .findViewById(R.id.call_time);
        timeTextView.setText(callTime);
        TextView numberTextView = (TextView)convertView.findViewById(R.id.call_number);
        numberTextView.setText(callNumber);
        TextView durationTextView = (TextView) convertView.findViewById(R.id.call_duration);
        durationTextView.setText(callDuration);
        ImageView callTypeDrawable = (ImageView) convertView.findViewById(R.id.call_type);
        callTypeDrawable.setImageResource(getCallTypeDrawable(callType));
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        String title = mExpandableListTitle.get(listPosition);
        LinkedList<CallLogs> callLogsList = new LinkedList<>();
        for(CallLogs callLog : mCallLogsArrayList)
        {
            if(callLog.getmDate().equals(title))
            {
                callLogsList.add(callLog);
            }
        }
        return callLogsList.size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return mExpandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return mExpandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        TextView listItemCount = (TextView) convertView.findViewById(R.id.expandedListItemCount);
        int count = getChildrenCount(listPosition);
        listItemCount.setText("("+String.valueOf(count)+")");
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}