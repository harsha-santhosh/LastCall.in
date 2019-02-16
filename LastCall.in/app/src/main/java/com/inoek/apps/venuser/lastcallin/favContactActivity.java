package com.inoek.apps.venuser.lastcallin;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.inoek.apps.venuser.lastcallin.BaseActivity.preferences;
import static com.inoek.apps.venuser.lastcallin.BaseActivity.preferencesEditor;


public class favContactActivity extends Fragment implements ContactUpdaterInterface<ArrayList<FavouriteContactsType>> {

    private View main_view;
    private Context context;
    private GridView gridViewLayout;
    private ArrayList<FavouriteContactsType> favouriteContactsArrayList;
    private FavouriteContacts favouriteContacts;
    private FavouriteContactsAdapter favouriteContactsAdapter;
    private ContactsObjectObtainer contactsObjectObtainer;
    private DetailsFragmentInterface detailsFragmentInterface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactsObjectObtainer = BaseActivity.contactsObjectObtainer;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_fav_contact, container, false);
        favouriteContactsArrayList = new ArrayList<>();
        favouriteContacts = BaseActivity.favouriteContacts;
        main_view = rootView;
        detailsFragmentInterface = BaseActivity.detailsFragmentInterface;
        context = getActivity().getApplicationContext();
        gridViewLayout = (GridView) main_view.findViewById(R.id.favContactsGridView);
        new CollectFavouritesAsync(favouriteContacts,favContactActivity.this, BaseActivity.progressDialog).execute();
        registerForContextMenu(gridViewLayout);
        return rootView;
    }

    private void showPopup( String name)
    {
        String snackText = "Removed "+name+" from favourites";
        Snackbar mySnackbar = Snackbar.make(main_view, snackText, Snackbar.LENGTH_SHORT);
        mySnackbar.show();
    }

    @Override
    public void onResume() {
        int index = preferences.getInt("gridindex",0);
        gridViewLayout.setSelection(index);
        super.onResume();
    }

    @Override
    public void onPause() {
        int index = gridViewLayout.getFirstVisiblePosition();
        preferencesEditor.putInt("gridindex",index);
        preferencesEditor.apply();
        super.onPause();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.favContactsGridView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("Options");
            String[] menuItems = getResources().getStringArray(R.array.negative_options);
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
        String[] menuItems = getResources().getStringArray(R.array.negative_options);
        String menuItemName = menuItems[menuItemIndex];
        if(menuItemName.compareTo(getString(R.string.remove_from_favourites)) == 0)
        {
            String contactName = favouriteContactsArrayList.get(info.position).getCallLogs().getmName();
            String phoneNumber = favouriteContactsArrayList.get(info.position).getCallLogs().getmNumber();
            String photoURI = favouriteContactsArrayList.get(info.position).getCallLogs().getmPhotoUri() ;
            Integer count = favouriteContactsArrayList.get(info.position).getCount();
            String notFavouritesString = preferences.getString("notFavouritesString","");
            String removeFavouriteContact = contactName+","+phoneNumber+","+photoURI+","+String.valueOf(count)+";";
            if(!notFavouritesString.contains(removeFavouriteContact)) {
                notFavouritesString += removeFavouriteContact;
                favouriteContactsArrayList.remove(info.position);
                favouriteContactsAdapter.notifyDataSetChanged();
                System.out.println(notFavouritesString);
                preferencesEditor.putString("notFavouritesString",notFavouritesString);
                preferencesEditor.apply();
                showPopup(contactName);
            }
        }
        return true;
    }

    @Override
    public void onContactsUpdate(ArrayList<FavouriteContactsType> obj) {
        favouriteContactsArrayList = obj;
        Collections.sort(favouriteContactsArrayList, new Comparator<FavouriteContactsType>() {
            @Override
            public int compare(FavouriteContactsType o1, FavouriteContactsType o2) {
                return o2.getCount().compareTo(o1.getCount());
            }
        });
        favouriteContactsAdapter = new FavouriteContactsAdapter(context, R.layout.favourite_contact, favouriteContactsArrayList);
        gridViewLayout.setAdapter(favouriteContactsAdapter);
        int index = preferences.getInt("gridindex",0);
        gridViewLayout.setSelection(index);
        gridViewLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FavouriteContactsType favouriteContactsType = (FavouriteContactsType) gridViewLayout.getItemAtPosition(position);
                if(favouriteContactsType.getCallLogs().getmNumber().contains("Unsaved")) {
                    Person p = contactsObjectObtainer.getContactsObject(favouriteContactsType.getCallLogs().getmName());
                    detailsFragmentInterface.showDetailsView(p);
                }
                else
                {
                    Person p = contactsObjectObtainer.getContactsObject(favouriteContactsType.getCallLogs().getmNumber());
                    detailsFragmentInterface.showDetailsView(p);
                }
                Person p = contactsObjectObtainer.getContactsObject(favouriteContactsType.getCallLogs().getmNumber());
            }
        });
    }
}
