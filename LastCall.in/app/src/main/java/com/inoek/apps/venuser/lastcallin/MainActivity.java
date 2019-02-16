package com.inoek.apps.venuser.lastcallin;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;


public class MainActivity extends Fragment {

    private ArrayList<Person> contactArrayList;
    private ContactsListViewAdapter contactsListViewAdapter;
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEditor;
    private ListView contactsListView;
    private View main_view;
    private LinkedHashMap<String,Integer> mContactsIndexMap;
    private DetailsFragmentInterface detailsFragmentInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        preferences = getActivity().getApplicationContext().getSharedPreferences("lastcall",0);
        preferencesEditor = preferences.edit();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.activity_main, container, false);
        detailsFragmentInterface = BaseActivity.detailsFragmentInterface;
        main_view = rootView;
        contactsListView = (ListView) rootView.findViewById(R.id.contacts_list);
        contactArrayList = new ArrayList<>();
        contactArrayList =  BaseActivity.contactArrayList;
        mContactsIndexMap = new LinkedHashMap<>();
        setUpContacts();
        contactsListView.setFastScrollEnabled(true);
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Person person = (Person) contactsListView.getItemAtPosition(position);
                detailsFragmentInterface.showDetailsView(person);
            }
        });
        registerForContextMenu(contactsListView);
        contactsListView.setAdapter(contactsListViewAdapter);
        displayIndex();
        int index = preferences.getInt("index",0);
        int top = preferences.getInt("top",0);
        if(!contactArrayList.isEmpty()) {
            contactsListView.setSelectionFromTop(index, top);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        contactsListView.smoothScrollBy(0,0);
        View v = contactsListView.getChildAt(0);
        if(v!=null) {
            int index = contactsListView.getFirstVisiblePosition();
            int top = v.getTop() - contactsListView.getPaddingTop();
            preferencesEditor.putInt("index", index);
            preferencesEditor.putInt("top", top);
            preferencesEditor.apply();
        }
        super.onPause();
    }

    private void showPopup( String name)
    {
        String snackText = "Added "+name+" to favourites";
        Snackbar mySnackbar = Snackbar.make(main_view, snackText, Snackbar.LENGTH_SHORT);
        mySnackbar.show();
    }

    private void showAlreadyAdded( String name)
    {
        String snackText = "Already Added "+name+" to favourites";
        Snackbar mySnackbar = Snackbar.make(main_view, snackText, Snackbar.LENGTH_SHORT);
        mySnackbar.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem item1 = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item1);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(true);
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                contactsListViewAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                contactsListViewAdapter.getFilter().filter(newText);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.contacts_list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("Options");
            String[] menuItems = getResources().getStringArray(R.array.positive_options);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.positive_options);
        String menuItemName = menuItems[menuItemIndex];
        if(menuItemName.compareTo(getString(R.string.add_to_favourites)) == 0)
        {
            String contactName = contactArrayList.get(info.position).getmName();
            String phoneNumber = contactArrayList.get(info.position).getmPhoneNumber();
            String photoURI = contactArrayList.get(info.position).getmImageUri();
            String favouritesString = preferences.getString("favouritesString","");
            String newFavouriteContact = contactName+","+phoneNumber+","+photoURI+","+"3"+";";
            if(!favouritesString.contains(contactName+","+phoneNumber)) {
                favouritesString += newFavouriteContact;
                System.out.println(favouritesString);
                preferencesEditor.putString("favouritesString",favouritesString);
                preferencesEditor.apply();
                showPopup(contactName);
            }
            else
            {
                showAlreadyAdded(contactName);
            }
        }
        return true;
    }

    private void setUpContacts()
    {
        contactsListViewAdapter = new ContactsListViewAdapter(getActivity().getApplicationContext(),R.layout.activity_list_item,contactArrayList,mContactsIndexMap);
        contactsListViewAdapter.sort(new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                return o1.getmName().compareTo(o2.getmName());
            }
        });
    }

    private void displayIndex() {
        contactsListViewAdapter.loadIndices();
        LinearLayout indexLayout = (LinearLayout) main_view.findViewById(R.id.side_index);
        TextView textView;
        List<String> indexList = new ArrayList<String>(contactsListViewAdapter.getIndexMap().keySet());
        for (String index : indexList) {
            textView = (TextView) getActivity().getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView selectedIndex = (TextView) v;
                    contactsListView.setSelection(contactsListViewAdapter.getIndexMap().get(selectedIndex.getText()));
                }
            });
            textView.setText(index);
            indexLayout.addView(textView);
        }
    }
}
