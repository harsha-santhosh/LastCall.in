package com.inoek.apps.venuser.lastcallin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by harshasanthosh on 21/05/17.
 */

public class ContactsListViewAdapter extends BaseAdapter implements Filterable, SectionIndexer {

    // Original List
    private List<Person> mContactArrayList;
    //Copy of Original List
    private ArrayList<Person> mFilterList;
    private Context mContext;
    private final LayoutInflater mInflater;
    private int mResource;
    private boolean mNotifyOnChange = true;
    private LinkedHashMap<String,Integer> mContactsIndexMap;
    private CustomFilter filter;
    private String[] sections;

    public ContactsListViewAdapter(Context context, int resource, ArrayList<Person> contactArrayList, LinkedHashMap<String,Integer> contactsIndexMap)
    {
        mContactArrayList = contactArrayList;
        mContext = context;
        mResource = resource;
        mInflater = LayoutInflater.from(mContext);
        mFilterList = (ArrayList<Person>) mContactArrayList;
        filter = new CustomFilter();
    }

    private String getAppropriatePhoneType(int type)
    {
        switch (type)
        {
            case 1:
                return "Home";
            case 2:
                return "Mobile";
            case 3:
                return "Work";
            case 4:
                return "Fax Work";
            case 5:
                return "Fax Home";
            case 6:
                return "Pager";
            case 7:
                return "Other";
            case 8:
                return "Callback";
            case 9:
                return "Car";
            case 10:
                return "Company";
            case 11:
                return "ISDN";
            case 12:
                return "Main";
            case 13:
                return "Other Fax";
            case 14:
                return "Radio";
            case 15:
                return "Telex";
            case 16:
                return "TTY TDD";
            case 17:
                return "Work Mobile";
            case 18:
                return "Work Pager";
            case 19:
                return "Assistant";
            case 20:
                return "MMS";
            default:
                return "";
        }
    }

    public void loadIndices()
    {
        mContactsIndexMap = new LinkedHashMap<>();
        for (int i = 0; i < mContactArrayList.size(); i++) {
            Person person = mContactArrayList.get(i);
            String index = person.getmName().substring(0, 1);
            if (mContactsIndexMap.get(index) == null)
                mContactsIndexMap.put(index, i);
        }
        Set<String> keys = mContactsIndexMap.keySet(); // set of letters

        Iterator<String> it = keys.iterator();
        ArrayList<String> keyList = new ArrayList<String>();

        while (it.hasNext()) {
            String key = it.next();
            keyList.add(key);
        }
        Collections.sort(keyList);//sort the keylist
        sections = new String[keyList.size()]; // simple conversion to array
        keyList.toArray(sections);
    }

    public LinkedHashMap<String,Integer> getIndexMap()
    {
        return mContactsIndexMap;
    }

    public void add(Person object) {
        if (mFilterList != null) {
            mFilterList.add(object);
        } else {
            mContactArrayList.add(object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void addAll(Collection<? extends Person> collection) {

        if (mFilterList != null) {
            mFilterList.addAll(collection);
        } else {
            mContactArrayList.addAll(collection);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void addAll(Person ... items) {

        if (mFilterList != null) {
            Collections.addAll(mFilterList, items);
        } else {
            Collections.addAll(mContactArrayList, items);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void insert(Person object, int index) {
        if (mFilterList != null) {
            mFilterList.add(index, object);
        } else {
            mContactArrayList.add(index, object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void remove(Person object) {

        if (mFilterList != null) {
            mFilterList.remove(object);
        } else {
            mContactArrayList.remove(object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void clear() {
        if (mFilterList != null) {
            mFilterList.clear();
        } else {
            mContactArrayList.clear();
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void sort(Comparator<? super Person> comparator) {
        if (mFilterList != null) {
            Collections.sort(mFilterList, comparator);
        } else {
            Collections.sort(mContactArrayList, comparator);
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
        if(mContactArrayList.size()==0)
            return 1;
        else
            return mContactArrayList.size();
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }


    @Override
    public int getCount() {
        return mContactArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mContactArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        if (convertView == null) {
            view = mInflater.inflate(mResource, parent, false);
        } else {
            view = convertView;
        }
        TextView textViewPersonName = (TextView) view.findViewById(R.id.textViewName);
        ImageView imageViewPersonImage = (ImageView) view.findViewById(R.id.imageViewIcon);
        TextView textViewPersonPhone = (TextView) view.findViewById(R.id.textViewPhone);
        TextView textViewPersonPhoneType = (TextView) view.findViewById(R.id.textViewType);
        Person person = (Person) getItem(position);
        if(person!=null)
        {
            textViewPersonName.setText(person.getmName());
            textViewPersonPhone.setText(person.getmPhoneNumber());
            if(person.getmImageUri()!=null) {
                byte[] imageBytes = Base64.decode(person.getmImageUri(), Base64.DEFAULT);
                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                imageViewPersonImage.setImageBitmap(decodedImage);
            }
            else {
                imageViewPersonImage.setImageResource(R.mipmap.ic_launcher_round);
            }
            textViewPersonPhoneType.setText(getAppropriatePhoneType(person.getmType()));
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        if(filter == null)
            filter = new CustomFilter();
        return filter;
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        String letter = sections[sectionIndex];
        return mContactsIndexMap.get(letter);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    public class CustomFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if(mFilterList==null)
            {
                mFilterList = new ArrayList<>(mContactArrayList);
            }

            if(constraint==null || constraint.length()==0)
            {
                ArrayList<Person> list = new ArrayList<>(mFilterList);
                results.values = list;
                results.count = list.size();
            }
            else
            {
                String prefixString = constraint.toString().toLowerCase();
                ArrayList<Person> values = new ArrayList<>(mFilterList);
                final int count = values.size();
                final ArrayList<Person> newValues = new ArrayList<>();

                for(int i=0;i<count;i++)
                {
                    final Person value = values.get(i);
                    final String valueText = value.getmPhoneNumber().toString().toLowerCase();

                    if(valueText.contains(prefixString))
                    {
                        newValues.add(value);
                    }
                    else
                    {
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++)
                        {
                            if (words[k].startsWith(prefixString))
                            {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mContactArrayList = (List<Person>)results.values;
            if(results.count>0)
            {
                notifyDataSetChanged();
            }
            else
            {
                notifyDataSetInvalidated();
            }
        }
    }
}
