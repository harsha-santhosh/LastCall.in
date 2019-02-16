package com.inoek.apps.venuser.lastcallin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by harshasanthosh on 21/05/17.
 */

public class DurationDataListViewAdapter extends BaseAdapter {

    // Original List
    private List<durationData> mDurationDataArrayList;
    //Copy of Original List
    private ArrayList<durationData> mFilterList;
    private Context mContext;
    private final LayoutInflater mInflater;
    private int mResource;
    private boolean mNotifyOnChange = true;

    public DurationDataListViewAdapter(Context context, int resource, ArrayList<durationData> durationDataArrayList)
    {
        mDurationDataArrayList = durationDataArrayList;
        mContext = context;
        mResource = resource;
        mInflater = LayoutInflater.from(mContext);
        mFilterList = durationDataArrayList;
    }

    public void add(durationData object) {
        if (mFilterList != null) {
            mFilterList.add(object);
        } else {
            mDurationDataArrayList.add(object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void addAll(Collection<? extends durationData> collection) {

        if (mFilterList != null) {
            mFilterList.addAll(collection);
        } else {
            mDurationDataArrayList.addAll(collection);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void addAll(durationData ... items) {

        if (mFilterList != null) {
            Collections.addAll(mFilterList, items);
        } else {
            Collections.addAll(mDurationDataArrayList, items);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void insert(durationData object, int index) {
        if (mFilterList != null) {
            mFilterList.add(index, object);
        } else {
            mDurationDataArrayList.add(index, object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void remove(durationData object) {

        if (mFilterList != null) {
            mFilterList.remove(object);
        } else {
            mDurationDataArrayList.remove(object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void clear() {
        if (mFilterList != null) {
            mFilterList.clear();
        } else {
            mDurationDataArrayList.clear();
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void sort(Comparator<? super durationData> comparator) {
        if (mFilterList != null) {
            Collections.sort(mFilterList, comparator);
        } else {
            Collections.sort(mDurationDataArrayList, comparator);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mNotifyOnChange = true;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if(mDurationDataArrayList.size()==0)
            return 1;
        else
            return mDurationDataArrayList.size();
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }


    @Override
    public int getCount() {
        return mDurationDataArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDurationDataArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        if (convertView == null) {
            view = mInflater.inflate(mResource, parent, false);
        } else {
            view = convertView;
        }
        TextView textViewCallDate = (TextView) view.findViewById(R.id.call_date_info);
        ImageView imageViewCallType = (ImageView) view.findViewById(R.id.call_type_info);
        TextView textViewCallTime = (TextView) view.findViewById(R.id.call_time_info);
        TextView textViewCallDuration = (TextView) view.findViewById(R.id.call_duration_info);
        durationData durationData = (durationData) getItem(position);
        if(durationData!=null)
        {
            textViewCallDate.setText(durationData.getDate());
            textViewCallTime.setText(durationData.getTime());
            imageViewCallType.setImageResource(getCallTypeDrawable(durationData.getType()));
            textViewCallDuration.setText(durationData.getDuration());
        }
        return view;
    }
}
