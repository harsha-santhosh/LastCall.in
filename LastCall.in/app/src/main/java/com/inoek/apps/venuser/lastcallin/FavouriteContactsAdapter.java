package com.inoek.apps.venuser.lastcallin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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
 * Created by harshasanthosh on 14/07/17.
 */

public class FavouriteContactsAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private int mResource;
    // Original List
    private List<FavouriteContactsType> originalList;
    //Copy of Original List
    private ArrayList<FavouriteContactsType> filterList;
    // for text view resource
    private boolean mNotifyOnChange = true;

    public FavouriteContactsAdapter(Context context, int resource, List<FavouriteContactsType> originalList)
    {
        mInflater = LayoutInflater.from(context);
        this.originalList = originalList;
        mResource = resource;
        this.filterList = (ArrayList<FavouriteContactsType>) originalList;
    }

    public void add(FavouriteContactsType object) {
        if (filterList != null) {
            filterList.add(object);
        } else {
            originalList.add(object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void addAll(Collection<? extends FavouriteContactsType> collection) {

        if (filterList != null) {
            filterList.addAll(collection);
        } else {
            originalList.addAll(collection);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void addAll(FavouriteContactsType ... items) {

        if (filterList != null) {
            Collections.addAll(filterList, items);
        } else {
            Collections.addAll(originalList, items);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void insert(FavouriteContactsType object, int index) {
        if (filterList != null) {
            filterList.add(index, object);
        } else {
            originalList.add(index, object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void remove(FavouriteContactsType object) {

        if (filterList != null) {
            filterList.remove(object);
        } else {
            originalList.remove(object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void clear() {
        if (filterList != null) {
            filterList.clear();
        } else {
            originalList.clear();
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void sort(Comparator<? super FavouriteContactsType> comparator) {
        if (filterList != null) {
            Collections.sort(filterList, comparator);
        } else {
            Collections.sort(originalList, comparator);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return originalList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return originalList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getPosition(FavouriteContactsType item) {
        return originalList.indexOf(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(mInflater, position, convertView, parent, mResource);
    }

    private View createViewFromResource(LayoutInflater inflater, int position, View convertView,
                                        ViewGroup parent, int resource) {
        View view;

        if (convertView == null) {
            view = inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        final TextView favContactName = (TextView) view.findViewById(R.id.favContactName);
        ImageView favContactImage = (ImageView) view.findViewById(R.id.favContactImage);
        FavouriteContactsType item = (FavouriteContactsType) getItem(position);
        if(item!=null)
        {
            favContactName.setText(item.getCallLogs().getmName());
            favContactName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favContactName.setSelected(true);
                }
            });
            if(!item.getCallLogs().getmPhotoUri().equals("null")) {
                byte[] imageBytes = Base64.decode(item.getCallLogs().getmPhotoUri(), Base64.DEFAULT);
                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                favContactImage.setImageBitmap(decodedImage);
            }
            else
            {
                favContactImage.setImageResource(R.mipmap.ic_launcher_round);
            }
        }
        return view;
    }
}
