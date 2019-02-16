package com.inoek.apps.venuser.lastcallin;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.inoek.apps.venuser.lastcallin.R.id.nav_view;


public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ContactsRestored, ContactsObjectObtainer, DetailsFragmentInterface, personUpdaterInterface, FileOptionsInterface, CallPauser, loadMoreCallLogs {
    public static final int RequestPermissionCode = 124;
    // keeps the track of fragments pressed
    public static int fragId = 3;
    private NavigationView navigationView;
    // used to handle back button events
    private Handler mHandler;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    // Saved Call Logs and Contacts
    public static ArrayList<CContacts> savedContactsArrayList;
    public static ArrayList<CCallLogs> savedCallLogsArrayList;
    // List of Logs
    public static ArrayList<CallLogs> callLogsArrayList;
    // List of Contacts
    public static ArrayList<Person> contactArrayList;
    // List of Unsaved Contacts
    public static ArrayList<Person> unSavedContactsArrayList;
    // call Log object
    private CallLogs callLogs;
    // Favourite Contacts Object
    public static FavouriteContacts favouriteContacts;
    // Favourite Contacts
    private ArrayList<FavouriteContactsType> favouriteContactsArrayList;
    // Person object
    public static Person personOb;
    // Progress bar
    public static MyProgressDialog progressDialog;
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor preferencesEditor;
    public static ContactsRestored contactsRestoredObj;
    public static ContactsObjectObtainer contactsObjectObtainer;
    public static DetailsFragmentInterface detailsFragmentInterface;
    public static personUpdaterInterface pUpdaterInterface;
    public static loadMoreCallLogs loadMoreCallLogsInterface;
    public static FileOptionsInterface fOptionsInterface;
    public static CallPauser callPauserInterface;
    public static long mIncomingDuration;
    public static long mOutgoingDuration;
    public static long mCallsOutgoing;
    public static long mCallsIncoming;
    public static long mCallsMissed;
    public static long mCallsRejected;
    public static String mIncomingDurationString;
    public static String mOutgoingDurationString;
    public static AsyncTask<Void, String, Void> loadContactsCallLogsFirstTimeAsyncObj;
    public static AsyncTask<Void, String, Void> loadContactsCallLogsAsyncObj;
    public static AsyncTask<Void, String, Void> loadDataAsyncObj;
    public static String lastDateImported;
    public static boolean firstTimeInstall;
    DatabaseHandler db;

    private boolean compareTwoPhoneNumbers(String phoneNumber1, String phoneNumber2)
    {
        boolean isEqual = false;
        int high,low;
        String highString, lowString;

        if(phoneNumber1.charAt(0) == '0')
        {
            if(phoneNumber1.charAt(1) == '0')
            {
                phoneNumber1 = phoneNumber1.substring(1, phoneNumber1.length());
                phoneNumber1 = phoneNumber1.substring(1, phoneNumber1.length());
            }
            else
            {
                phoneNumber1 = phoneNumber1.substring(1, phoneNumber1.length());
            }
        }

        if(phoneNumber2.charAt(0) == '0')
        {
            if(phoneNumber2.charAt(1) == '0')
            {
                phoneNumber2 = phoneNumber2.substring(1, phoneNumber2.length());
                phoneNumber2 = phoneNumber2.substring(1, phoneNumber2.length());
            }
            else
            {
                phoneNumber2 = phoneNumber2.substring(1, phoneNumber2.length());
            }
        }

        if(phoneNumber1.length() >= phoneNumber2.length())
        {
            high = phoneNumber1.length();
            highString = phoneNumber1;
            low = phoneNumber2.length();
            lowString = phoneNumber2;
        }
        else
        {
            high = phoneNumber2.length();
            highString = phoneNumber2;
            low = phoneNumber1.length();
            lowString = phoneNumber1;
        }

        if(high >= 8 && low >= 8) {
            if (high == low || high - low < 4) {
                if (highString.contains(lowString))
                    isEqual = true;
            }
        }
        else
        {
            if(highString.equals(lowString))
                isEqual = true;
        }
        return isEqual;
    }

    private String getContactImageUrl(String phNumber)
    {
        String contactImageUri = null;
        ArrayList<Person> lcontactArrayList = contactArrayList;
        for( int i = 0;i < lcontactArrayList.size(); i++)
        {
            Person p = lcontactArrayList.get(i);
            if(compareTwoPhoneNumbers(p.getmPhoneNumber(),phNumber)) {
                contactImageUri = p.getmImageUri();
                break;
            }
        }
        return contactImageUri;
    }

    private String getContactName(String phNumber)
    {
        String contactName = null;
        ArrayList<Person> lcontactArrayList = contactArrayList;
        for( int i = 0;i < lcontactArrayList.size(); i++)
        {
            Person p = lcontactArrayList.get(i);
            if(compareTwoPhoneNumbers(p.getmPhoneNumber(),phNumber)) {
                contactName = p.getmName();
                break;
            }
        }
        return contactName;
    }

    private void updateContactStatistics(String phNumber, Integer type, String duration,
                                         String date, String formattedduration, String formattedTime, long dt, long dr)
    {
        ArrayList<Person> lcontactArrayList = contactArrayList;
        for(int i = 0; i < contactArrayList.size(); i++)
        {
            Person p = contactArrayList.get(i);
            if(compareTwoPhoneNumbers(p.getmPhoneNumber(),phNumber)) {
                if(p.addCallHistory(formattedduration,date,type,formattedTime, dt, dr)) {
                    p.updateCallStatistics(type, duration);
                    contactArrayList.remove(i);
                    contactArrayList.add(i, p);
                }
                break;
            }
        }
        contactArrayList = lcontactArrayList;
    }

    private void updateUnsavedContactStatistics(String phNumber, Integer type, String duration,
                                                String date, String formattedduration, String formattedTime, long dt, long dr)
    {
        ArrayList<Person> lcontactArrayList = unSavedContactsArrayList;
        Person person = new Person(callLogs.getmName(),callLogs.getmNumber());
        for(int i = 0; i < lcontactArrayList.size(); i++)
        {
            Person p = lcontactArrayList.get(i);
            if(compareTwoPhoneNumbers(p.getmName(),phNumber)) {
                if(p.addCallHistory(formattedduration,date,type,formattedTime, dt,dr)) {
                    p.updateCallStatistics(type, duration);
                    lcontactArrayList.remove(i);
                    lcontactArrayList.add(i, p);
                }
                return;
            }
        }
        person.addCallHistory(formattedduration,date,type,formattedTime, dt,dr);
        person.updateCallStatistics(type, duration);
        lcontactArrayList.add(person);
        unSavedContactsArrayList = lcontactArrayList;
    }

    void addToCallLogs(CallLogs _cL)
    {
        boolean isPresent = false;
        if(!callLogsArrayList.isEmpty()) {
            if (_cL.getmNumber().contains("Unsaved")) {
                for (CallLogs c : callLogsArrayList) {
                    if (compareTwoPhoneNumbers(_cL.getmName(),c.getmName()) && _cL.getmOrigDate() == c.getmOrigDate()
                            && _cL.getmOrigDuration() == c.getmOrigDuration()) {
                        isPresent = true;
                        break;
                    }
                }
            } else {
                for (CallLogs c : callLogsArrayList) {
                    if (compareTwoPhoneNumbers(_cL.getmNumber(),c.getmNumber()) && _cL.getmOrigDate() == c.getmOrigDate()
                            && _cL.getmOrigDuration() == c.getmOrigDuration()) {
                        isPresent = true;
                        break;
                    }
                }
            }
            if(!isPresent)
            {
                for (CallLogs c : callLogsArrayList) {
                    if (compareTwoPhoneNumbers(_cL.getmNumber(),c.getmName()) && _cL.getmOrigDate() == c.getmOrigDate()
                            && _cL.getmOrigDuration() == c.getmOrigDuration()) {
                        isPresent = true;
                        break;
                    }
                }
            }
            if(!isPresent) {
                callLogsArrayList.add(_cL);
            }
        }
        else
        {
            callLogsArrayList.add(_cL);
        }
        Collections.sort(callLogsArrayList);
    }

    void updateContactNames()
    {
        for(CallLogs c : callLogsArrayList) {
            if (c.getmNumber().contains("Unsaved")) {
                String phName = getContactName(c.getmName());
                if(phName!=null)
                {
                    c.setmNumber(c.getmName());
                    c.setName(phName);
                }
            }
            else
            {
                c.setName(getContactName(c.getmNumber()));
                c.setmPhotoUri(getContactImageUrl(c.getmNumber()));
            }
        }
    }

    private static String getFormattedTime(long inTime)
    {
        String formattedTime = "";
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat sdateFormat = new SimpleDateFormat("HH:mm:ss");
        sdateFormat.setTimeZone(timeZone);
        formattedTime = sdateFormat.format(inTime);
        return formattedTime;
    }


    private void loadCallLogs() {
        // if contactArrayList is filled, please clear it.
        if(!unSavedContactsArrayList.isEmpty())
        {
            unSavedContactsArrayList.clear();
        }
        mCallsMissed = 0;
        mCallsRejected = 0;
        mCallsOutgoing = 0;
        mCallsIncoming = 0;
        mIncomingDuration = 0;
        mOutgoingDuration = 0;
        for(int i = 0; i < savedCallLogsArrayList.size(); i++)
        {
            CCallLogs _callLogs = savedCallLogsArrayList.get(i);
            String phNumber = _callLogs.getNumber();
            String phName = getContactName(phNumber);
            String callType = String.valueOf(_callLogs.getType());
            String callDate = String.valueOf(_callLogs.getDate());
            String callPhoto = getContactImageUrl(phNumber);
            String callDuration = String.valueOf(_callLogs.getDuration());
            callLogs = new CallLogs(phName, callPhoto, phNumber, callType, callDate, callDuration);
            addToCallLogs(callLogs);
            if (callLogs.getmNumber().contains("Unsaved")) {
                updateUnsavedContactStatistics(phNumber, Integer.parseInt(callType), callDuration,
                        callLogs.getmDate(), callLogs.getmDuration(), callLogs.getmTime(),
                        Long.parseLong(callDate), Long.parseLong(callDuration));
            }
            else
            {
                updateContactStatistics(phNumber, Integer.parseInt(callType), callDuration,
                        callLogs.getmDate(), callLogs.getmDuration(), callLogs.getmTime(),
                        Long.parseLong(callDate), Long.parseLong(callDuration));
            }

        }
        updateContactNames();
        mIncomingDurationString = getFormattedTime(mIncomingDuration);
        mOutgoingDurationString = getFormattedTime(mOutgoingDuration);
    }

    private void loadFavourites()
    {
        favouriteContacts = new FavouriteContacts(callLogsArrayList);
    }

    private boolean exists(String number)
    {
        for(int i = 0; i< contactArrayList.size();i++)
        {
            Person p = contactArrayList.get(i);
            if(p.getmPhoneNumber().equals(number))
                return true;
        }
        return false;
    }

    private void loadContacts()
    {
        // if contactArrayList is filled, please clear it.
        if(!contactArrayList.isEmpty())
        {
            contactArrayList.clear();
        }

        for(int i = 0; i < savedContactsArrayList.size(); i++)
        {
            CContacts contactsObj = savedContactsArrayList.get(i);
            int type = Integer.parseInt(contactsObj.getType());
            String Name = contactsObj.getName();
            String Number = contactsObj.getNumber();
            String image_uri = contactsObj.getImageString();
            // remove all spaces
            Number = Number.replaceAll("\\s+","");
            // Find for existence of contact
            if(Number.length()>2 && !exists(Number))
                contactArrayList.add(new Person(Name,Number,image_uri, type));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        db = new DatabaseHandler(this);
        contactsRestoredObj = BaseActivity.this;
        contactsObjectObtainer = BaseActivity.this;
        detailsFragmentInterface = BaseActivity.this;
        pUpdaterInterface = BaseActivity.this;
        fOptionsInterface = BaseActivity.this;
        callPauserInterface = BaseActivity.this;
        loadMoreCallLogsInterface = BaseActivity.this;
        mIncomingDuration = 0;
        mOutgoingDuration = 0;
        mCallsOutgoing = 0;
        mCallsIncoming = 0;
        mCallsMissed = 0;
        mCallsRejected = 0;
        contactArrayList = new ArrayList<>();
        unSavedContactsArrayList = new ArrayList<>();
        callLogsArrayList = new ArrayList<>();
        progressDialog = new MyProgressDialog(BaseActivity.this);
        navigationView = (NavigationView) findViewById(nav_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = getApplicationContext().getSharedPreferences("lastcall",0);
        preferencesEditor = preferences.edit();
        firstTimeInstall = preferences.getBoolean("firstTimeInstall",true);
        mHandler = new Handler();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_CALL_LOG)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_CONTACTS)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_CALL_LOG)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED)
        {
            EnableRuntimePermission();
        }

        if (savedInstanceState == null) {
            if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_CALL_LOG)==PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_CONTACTS)==PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_CALL_LOG)==PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED)
            {
                new UpdateContactsAsyncOnCreate().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if(loadContactsCallLogsAsyncObj!=null) {
            loadContactsCallLogsAsyncObj.cancel(true);
        }
        if(loadContactsCallLogsFirstTimeAsyncObj!=null) {
            loadContactsCallLogsFirstTimeAsyncObj.cancel(true);
        }
        if(loadDataAsyncObj != null)
        {
            loadDataAsyncObj.cancel(true);
        }
        progressDialog.dismiss();
        super.onPause();
    }


    public void loadHomeFragment() {

        navigationView.getMenu().getItem(3).setChecked(true);
        fragId = 3;
        getSupportActionBar().setTitle("Logs");
        showLogsFragment(R.id.contacts_fragment,3,"Logs");

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (getFragmentManager().findFragmentByTag("Logs") != null) {
            drawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = new LogsFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                        android.R.animator.fade_out);
                fragmentTransaction.replace(R.id.contacts_fragment, fragment);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    public void EnableRuntimePermission() {
        // check for build version number, as this requirement starts from Android 5.0
        // Any new permissions, should be first registered in AndroidManifest.xml before getting added here.
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionsNeeded = new ArrayList<String>();
            final List<String> permissionsList = new ArrayList<String>();
            // Add required permissions if not added
            if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
                permissionsNeeded.add("Read Contacts");
            if (!addPermission(permissionsList,
                    Manifest.permission.WRITE_CONTACTS))
                permissionsNeeded.add("Write Contacts");
            if (!addPermission(permissionsList,
                    Manifest.permission.WRITE_CALL_LOG))
                permissionsNeeded.add("Write Call Log");
            if (!addPermission(permissionsList,
                    Manifest.permission.READ_CALL_LOG))
                permissionsNeeded.add("Read Call Log");
            if (!addPermission(permissionsList,
                    Manifest.permission.CALL_PHONE))
                permissionsNeeded.add("Call Phone");
            if (!addPermission(permissionsList,
                    Manifest.permission.READ_EXTERNAL_STORAGE))
                permissionsNeeded.add("Read Storage");
            if (!addPermission(permissionsList,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE))
                permissionsNeeded.add("Write Storage");
            if (permissionsList.size() > 0) {
                // request for permissions
                requestPermissions(
                        permissionsList.toArray(new String[permissionsList
                                .size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                return;
            }
        }
    }

    private boolean addPermission(List<String> permissionsList,
                                  String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!shouldShowRequestPermissionRationale(permission))
                        return false;
                }
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message) {
        // shows alert message if needed, it can be used for anything.
        new AlertDialog.Builder(BaseActivity.this).setMessage(message).create()
                .show();
    }

    private void intiateFragment(Fragment fragment, int resourceId, int itemIndex, String title)
    {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);
        fragId = itemIndex;
        transaction.replace(resourceId,fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
        if(itemIndex < 6) {
            navigationView.getMenu().getItem(itemIndex).setChecked(true);
        }
        else
        {
            if(itemIndex >= 6)
            {
                if(title.equals("BackUp Contacts")) {
                    navigationView.getMenu().getItem(itemIndex).getSubMenu().getItem(0).setChecked(true);
                }
                else if(title.equals("Restore Contacts"))
                {
                    navigationView.getMenu().getItem(itemIndex-1).getSubMenu().getItem(1).setChecked(true);
                }
            }
        }
        getSupportActionBar().setTitle(title);
    }

    public void LoadDataIntoApp()
    {
        savedCallLogsArrayList = db.getAllCallLogs();
        savedContactsArrayList = db.getAllContacts();
        new UpdateContactsAsyncResume().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void showHomeFragment(int resourceId, int itemIndex, String title)
    {
        Fragment fragment = new MainActivity();
        intiateFragment(fragment,resourceId,itemIndex,title);
    }

    public void showLogsFragment(int resourceId, int itemIndex, String title)
    {
        Fragment fragment = new LogsFragment();
        intiateFragment(fragment,resourceId,itemIndex,title);
    }

    public void showDialerFragment(int resourceId, int itemIndex, String title)
    {
        Fragment fragment = new DialerFragment();
        intiateFragment(fragment,resourceId,itemIndex,title);
    }

    public void showFavouritesFragment(int resourceId, int itemIndex, String title)
    {
        Fragment fragment = new favContactActivity();
        intiateFragment(fragment, resourceId, itemIndex, title);
    }

    public void showBackUpFragment(int resourceId, int itemIndex, String title)
    {
        Fragment fragment = new export_contacts();
        intiateFragment(fragment,resourceId,itemIndex,title);
    }

    public void showRestoreFragment(int resourceId, int itemIndex, String title)
    {
        Fragment fragment = new import_contacts();
        intiateFragment(fragment,resourceId,itemIndex,title);
    }

    @Override
    public void showDetailsView(Person p) {
        Fragment fragment = new Call_Details_Fragment();
        fragId = 4;
        personOb = p;
        intiateFragment(fragment, R.id.contacts_fragment,4,"Call Details");
    }


    public void showStatisticsFragment(int resourceId, int itemIndex, String title)
    {
        Fragment fragment = new StatisticsFragment();
        intiateFragment(fragment,resourceId,itemIndex,title);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                // all the permissions to be added in this list "perms"
                perms.put(Manifest.permission.READ_CONTACTS,
                        PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_CONTACTS,
                        PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CALL_LOG,
                        PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_CALL_LOG,
                        PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE,
                        PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE,
                        PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    Toast.makeText(BaseActivity.this, "Thanks! I'll take care of your call logs from now.",
                            Toast.LENGTH_SHORT).show();
                    //    new UpdateContactsAsyncPermissions().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    // Permission Denied
                    showMessageOKCancel("Sorry!, you denied my Permission Requests!");
                    finish();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        if(fragId==3)
        {
            moveTaskToBack(true);
        }
        loadHomeFragment();
    }

    @Override
    protected void onResume() {
        if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_CALL_LOG)==PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_CONTACTS)==PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_CALL_LOG)==PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED) {
            if(firstTimeInstall)
            {
                loadContactsCallLogsFirstTimeAsyncObj = new LoadContactsCallLogsFirstTimeAsync(BaseActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                firstTimeInstall = false;
                preferencesEditor.putBoolean("firstTimeInstall",firstTimeInstall);
                preferencesEditor.apply();
            }
            else {
                loadContactsCallLogsAsyncObj = new LoadContactsCallLogsAsync(BaseActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            loadDataAsyncObj = new LoadDataAsync(BaseActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getAppropriateFragment(int FragmentID)
    {
        switch (FragmentID)
        {
            case 0: showDialerFragment(R.id.contacts_fragment,0,"Dialer");
                break;
            case 1: showHomeFragment(R.id.contacts_fragment,1,"Contacts");
                break;
            case 2: showFavouritesFragment(R.id.contacts_fragment,2,"Favourite Contacts");
                break;
            case 3: showLogsFragment(R.id.contacts_fragment,3,"Logs");
                break;
            case 4:
                break;
            case 5:
                showStatisticsFragment(R.id.contacts_fragment,4,"Statistics");
                fragId = 5;
                break;
            case 6:
                showBackUpFragment(R.id.contacts_fragment,6,"BackUp Contacts");
                break;
            case 7:
                showRestoreFragment(R.id.contacts_fragment,6,"Restore Contacts");
                break;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        item.setChecked(true);
        if (id == R.id.nav_contacts) {
            showHomeFragment(R.id.contacts_fragment,1,"Contacts");
            fragId = 1;
            // Handle the camera action
        } else if (id == R.id.nav_dialer) {
            showDialerFragment(R.id.contacts_fragment,0,"Dialer");
            fragId = 0;

        } else if (id == R.id.nav_logs) {
            showLogsFragment(R.id.contacts_fragment,3,"Logs");
            fragId = 3;
        } else if (id == R.id.nav_favourites) {
            showFavouritesFragment(R.id.contacts_fragment,2,"Favourite Contacts");
            fragId = 2;

        } else if (id == R.id.nav_statistics) {
            showStatisticsFragment(R.id.contacts_fragment,4,"Statistics");
            fragId = 5;

        } else if (id == R.id.nav_backup) {
            showBackUpFragment(R.id.contacts_fragment,6,"BackUp Contacts");
            fragId = 6;

        } else if (id == R.id.nav_restore) {
            showRestoreFragment(R.id.contacts_fragment,6,"Restore Contacts");
            fragId = 7;
        }
        else if (id == R.id.nav_about) {
           // show dialog fragment
            AboutDialog aboutDialog = new AboutDialog();
            aboutDialog.show(getFragmentManager(),"About Dialog");
            fragId = 9;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onContactsRestored() {
        loadContactsCallLogsFirstTimeAsyncObj = new LoadContactsCallLogsFirstTimeAsync(BaseActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public Person getContactsObject(String phNumber) {
        Person person = null;
        if(!contactArrayList.isEmpty())
        {
            for(Person p : contactArrayList)
            {
                if(compareTwoPhoneNumbers(p.getmPhoneNumber(),phNumber))
                {
                    person = p;
                    break;
                }
            }
        }
        if(person==null && !unSavedContactsArrayList.isEmpty())
        {
            for(Person p : unSavedContactsArrayList)
            {
                if(compareTwoPhoneNumbers(p.getmName(),phNumber))
                {
                    person = p;
                    break;
                }
            }
        }
        return person;
    }

    @Override
    public Person getUpdatedPersonDetails(Person p) {
        Person person = null;
        if(p.getmPhoneNumber().contains("Unsaved"))
        {
            for (Person person1 : unSavedContactsArrayList) {
                if (compareTwoPhoneNumbers(person1.getmName(),p.getmName()) && person1.getmPhoneNumber().contains(p.getmPhoneNumber())) {
                    person = person1;
                    break;
                }
            }
        }
        else {
            for (Person person1 : contactArrayList) {
                if (person1.getmName().contains(p.getmName()) && compareTwoPhoneNumbers(person1.getmPhoneNumber(),p.getmPhoneNumber())) {
                    person = person1;
                    break;
                }
            }
        }

        return person;
    }

    @Override
    public void showFolder() {
        SimpleFileDialog FileOpenDialog =  new SimpleFileDialog(BaseActivity.this, "FileShow",
                new SimpleFileDialog.SimpleFileDialogListener()
                {
                    @Override
                    public void onChosenDir(String chosenDir)
                    {
                        // The code in this function will be executed when the dialog OK button is pushed
                    }
                });

        //You can change the default filename using the public variable "Default_File_Name"
        FileOpenDialog.Default_File_Name = "";
        FileOpenDialog.chooseFile_or_Dir();
    }

    @Override
    public void openFile() {
        SimpleFileDialog FileOpenDialog =  new SimpleFileDialog(BaseActivity.this, "FileOpen",
                new SimpleFileDialog.SimpleFileDialogListener()
                {
                    @Override
                    public void onChosenDir(String chosenDir)
                    {
                        // The code in this function will be executed when the dialog OK button is pushed
                        String extension = chosenDir.substring(chosenDir.length()-4);
                        if(extension.contains(".lco") || extension.contains(".lca")) {
                            preferencesEditor.putString("sFilePath", chosenDir);
                            preferencesEditor.apply();
                            import_contacts.tFilePath.setText(chosenDir);
                            import_contacts.import_contacts.setEnabled(true);
                            import_contacts.import_logs.setEnabled(true);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Select .lco/.lca file only.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //You can change the default filename using the public variable "Default_File_Name"
        FileOpenDialog.Default_File_Name = "";
        FileOpenDialog.chooseFile_or_Dir();
    }

    @Override
    public void pauseTheApp() {
        moveTaskToBack(true);
    }

    @Override
    public void loadLogs() {
        loadHomeFragment();
    }

    private String encodedImageString(String url) {
        String encodedImage = null;
        if (url != null) {
            Uri u = Uri.parse(url);
            Bitmap bm = null;
            try {
                bm = BitmapFactory.decodeStream(BaseActivity.this.getContentResolver().openInputStream(u));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] byteArrayImage = baos.toByteArray();
            encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        }
        return encodedImage;
    }

    @Override
    public void load100MoreLogs() {
        String whereClause = android.provider.CallLog.Calls.DATE+" < ?";
        lastDateImported = preferences.getString("lastDateImported",null);
        loadContactsCallLogsAsyncObj = new LoadContactsCallLogsAsync(BaseActivity.this,whereClause,lastDateImported).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public class LoadContactsCallLogsAsync extends AsyncTask<Void, String, Void> {

        DatabaseHandler taskDb;
        MyProgressDialog progressDialog;
        String whereClause;
        String whereClauseArg;

        public LoadContactsCallLogsAsync(Context cxt)
        {
            taskDb = new DatabaseHandler(cxt);
            progressDialog = new MyProgressDialog(cxt);
            whereClause = null;
            whereClauseArg = null;
        }

        public LoadContactsCallLogsAsync(Context cxt, String wClause, String wClauseArg)
        {
            taskDb = new DatabaseHandler(cxt);
            progressDialog = new MyProgressDialog(cxt);
            whereClause = wClause;
            whereClauseArg = wClauseArg;
        }

        @Override
        protected Void doInBackground(Void... params) {

            Cursor managedCursor;
            Uri contacts = CallLog.Calls.CONTENT_URI;
            if (ActivityCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            if(whereClauseArg != null)
            {
                String[] arr = { whereClauseArg };
                managedCursor = getContentResolver().query(contacts, null, whereClause, arr, android.provider.CallLog.Calls.DATE + " DESC limit 100;");
            }
            else
            {
                managedCursor = getContentResolver().query(contacts, null, null, null, android.provider.CallLog.Calls.DATE + " DESC limit 100;");
            }
            publishProgress("Reading call logs.. ");
            if (managedCursor != null) {
                managedCursor.moveToFirst();
                do {
                    if (isCancelled())
                        break;
                    int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                    int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                    int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                    int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                    if (managedCursor.getCount() > 0) {
                        String phNumber = managedCursor.getString(number);
                        String callType = managedCursor.getString(type);
                        String callDate = managedCursor.getString(date);
                        String callDuration = managedCursor.getString(duration);
                        // remove all spaces
                        phNumber = phNumber.replaceAll("\\s+", "");
                        CCallLogs c = new CCallLogs();
                        c.setNumber(phNumber);
                        c.setType(Integer.parseInt(callType));
                        c.setDate(Long.parseLong(callDate));
                        c.setDuration(Integer.parseInt(callDuration));
                        taskDb.addCallLog(c);
                        lastDateImported = callDate;
                        preferencesEditor.putString("lastDateImported", lastDateImported);
                        preferencesEditor.apply();
                        preferencesEditor.commit();
                    }
                    //callLogsArrayList.add(callLogs);
                } while (managedCursor.moveToNext());
                managedCursor.close();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            taskDb.close();

            LoadDataIntoApp();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            final String v = values[0];
            progressDialog.setText(v);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            taskDb.close();
            super.onCancelled();
        }
    }

    public class LoadContactsCallLogsFirstTimeAsync extends AsyncTask<Void, String, Void> {

        DatabaseHandler taskDb;
        MyProgressDialog progressDialog;
        String whereClause;
        String whereClauseArg;

        public LoadContactsCallLogsFirstTimeAsync(Context cxt)
        {
            taskDb = new DatabaseHandler(cxt);
            progressDialog = new MyProgressDialog(cxt);
            whereClause = null;
            whereClauseArg = null;
        }

        @Override
        protected Void doInBackground(Void... params) {

            publishProgress("Importing contacts...");
            String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"
                    + ("1") + "'";
            String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
                    + " COLLATE LOCALIZED ASC";
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, selection
                    + " AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER
                    + "=1", null, sortOrder);
            publishProgress("Importing contacts.. ");
            int i = 1;
            while (phones.moveToNext()) {
                if (isCancelled())
                    break;
                publishProgress("Importing contacts\n("+String.valueOf(i)+"/"+String.valueOf(phones.getCount())+")...");
                i++;
                int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                String Name = phones
                        .getString(phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String Number = phones
                        .getString(phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                String image_uri = phones
                        .getString(phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                // remove all spaces
                Number = Number.replaceAll("\\s+", "");
                CContacts c = new CContacts();
                c.setImageString(encodedImageString(image_uri));
                c.setName(Name);
                c.setType(String.valueOf(type));
                c.setNumber(Number);
                taskDb.addContact(c);
            }

            Cursor managedCursor;
            Uri contacts = CallLog.Calls.CONTENT_URI;
            if (ActivityCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            if(whereClauseArg != null)
            {
                String[] arr = { whereClauseArg };
                managedCursor = getContentResolver().query(contacts, null, whereClause, arr, android.provider.CallLog.Calls.DATE + " DESC limit 100;");
            }
            else
            {
                managedCursor = getContentResolver().query(contacts, null, null, null, android.provider.CallLog.Calls.DATE + " DESC limit 100;");
            }
            publishProgress("Reading call logs.. ");
            if (managedCursor != null) {
                managedCursor.moveToFirst();
                do {
                    if (isCancelled())
                        break;
                    int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                    int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                    int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                    int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                    if (managedCursor.getCount() > 0) {
                        String phNumber = managedCursor.getString(number);
                        String callType = managedCursor.getString(type);
                        String callDate = managedCursor.getString(date);
                        String callDuration = managedCursor.getString(duration);
                        // remove all spaces
                        phNumber = phNumber.replaceAll("\\s+", "");
                        CCallLogs c = new CCallLogs();
                        c.setNumber(phNumber);
                        c.setType(Integer.parseInt(callType));
                        c.setDate(Long.parseLong(callDate));
                        c.setDuration(Integer.parseInt(callDuration));
                        taskDb.addCallLog(c);
                        lastDateImported = callDate;
                        preferencesEditor.putString("lastDateImported", lastDateImported);
                        preferencesEditor.apply();
                        preferencesEditor.commit();
                    }
                    //callLogsArrayList.add(callLogs);
                } while (managedCursor.moveToNext());
                managedCursor.close();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            taskDb.close();

            LoadDataIntoApp();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            final String v = values[0];
            progressDialog.setText(v);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            taskDb.close();
            super.onCancelled();
        }
    }

    public class LoadDataAsync extends AsyncTask<Void, String, Void> {

        DatabaseHandler taskDb;
        MyProgressDialog progressDialog;
        String whereClause;
        String whereClauseArg;

        public LoadDataAsync(Context cxt)
        {
            taskDb = new DatabaseHandler(cxt);
            progressDialog = new MyProgressDialog(cxt);
            whereClause = null;
            whereClauseArg = null;
        }

        @Override
        protected Void doInBackground(Void... params) {

            publishProgress("Importing contacts...");
            String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"
                    + ("1") + "'";
            String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
                    + " COLLATE LOCALIZED ASC";
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, selection
                    + " AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER
                    + "=1", null, sortOrder);
            publishProgress("Importing contacts.. ");
            int i = 1;
            while (phones.moveToNext()) {
                if (isCancelled())
                    break;
                publishProgress("Importing contacts\n("+String.valueOf(i)+"/"+String.valueOf(phones.getCount())+")...");
                i++;
                int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                String Name = phones
                        .getString(phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String Number = phones
                        .getString(phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                String image_uri = phones
                        .getString(phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                // remove all spaces
                Number = Number.replaceAll("\\s+", "");
                CContacts c = new CContacts();
                c.setImageString(encodedImageString(image_uri));
                c.setName(Name);
                c.setType(String.valueOf(type));
                c.setNumber(Number);
                taskDb.addContact(c);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            taskDb.close();

            LoadDataIntoApp();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            final String v = values[0];
            progressDialog.setText(v);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            taskDb.close();
            super.onCancelled();
        }
    }


    public class UpdateContactsAsync extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if(contactArrayList!=null) {
                contactArrayList.clear();
            }
            contactArrayList.clear();
            publishProgress("Updating contacts...");
            loadContacts();
            // load call logs
            publishProgress("Updating call logs...");
            loadCallLogs();
            if(favouriteContactsArrayList!=null) {
                favouriteContactsArrayList.clear();
            }
            // load favourites
            publishProgress("Collecting results...");
            loadFavourites();
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setText("Updating Contacts....");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            showLogsFragment(R.id.contacts_fragment,3,"Logs");
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setText(values[0]);
            super.onProgressUpdate(values);
        }
    }

    public class UpdateContactsAsyncPermissions extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // First time load fragment
            publishProgress("Updating contacts...");
            loadHomeFragment();
            // load call logs
            publishProgress("Updating call logs...");
            loadCallLogs();
            // load favourites
            publishProgress("Collecting results...");
            loadFavourites();
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setText("Updating Contacts....");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setText(values[0]);
            super.onProgressUpdate(values);
        }
    }

    public class UpdateContactsAsyncResume extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // load new contacts
            publishProgress("Updating contacts...");
            loadContacts();
            // load call logs
            publishProgress("Updating call logs...");
            loadCallLogs();
            if(favouriteContactsArrayList!=null) {
                favouriteContactsArrayList.clear();
            }
            // load favourites
            publishProgress("Collecting results...");
            loadFavourites();
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setText("Updating Contacts....");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            // load appropriate fragment
            getAppropriateFragment(fragId);
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setText(values[0]);
            super.onProgressUpdate(values);
        }
    }

    public class UpdateContactsAsyncOnCreate extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // 1 represents Home fragment "Contacts"
            publishProgress("Updating contacts...");
            loadContacts();
            // load call logs
            publishProgress("Updating call logs...");
            loadCallLogs();
            // load favourites
            publishProgress("Collecting results...");
            loadFavourites();
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setText("Updating Contacts....");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            // load home fragment
            loadHomeFragment();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setText(values[0]);
            super.onProgressUpdate(values);
        }
    }
}
