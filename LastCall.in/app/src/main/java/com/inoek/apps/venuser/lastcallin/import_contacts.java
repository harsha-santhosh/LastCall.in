package com.inoek.apps.venuser.lastcallin;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;


public class import_contacts extends Fragment {

    private View main_view;
    private SharedPreferences preferences;
    private ContactsRestored contactsRestoredObj;
    private Button open_file, cloud_import;
    public static Button import_contacts, import_logs;
    public static TextView tFilePath;
    private String sFilePath;
    private XmlPullParserFactory pullParserFactory;
    private ArrayList<CContacts> contactsArrayList;
    private ArrayList<CCallLogs> callLogsArrayList;
    private FileOptionsInterface fileOptionsInterface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        preferences = getActivity().getApplicationContext().getSharedPreferences("lastcall", 0);
        contactsRestoredObj = BaseActivity.contactsRestoredObj;
        fileOptionsInterface = BaseActivity.fOptionsInterface;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sFilePath = preferences.getString("sFilePath", "");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_import_contacts, container, false);
        main_view = rootView;
        open_file = (Button) main_view.findViewById(R.id.open_file);
        import_contacts = (Button) main_view.findViewById(R.id.import_con);
        import_logs = (Button) main_view.findViewById(R.id.import_cal);
        cloud_import = (Button) main_view.findViewById(R.id.cloud_import);
        tFilePath = (TextView) main_view.findViewById(R.id.path);
        if (!sFilePath.isEmpty()) {
            tFilePath.setText(sFilePath);
            import_contacts.setEnabled(true);
            import_logs.setEnabled(true);
        } else {
            import_contacts.setEnabled(false);
            import_logs.setEnabled(false);
        }
        // Free version
        cloud_import.setEnabled(false);
        setOnClickListeners();
        return rootView;
    }

    private void parseXML(XmlPullParser parser) {
        try {
            sFilePath = preferences.getString("sFilePath", "");
            File initialFile = new File(sFilePath);
            if (initialFile.exists()) {
                InputStream in_s = new FileInputStream(initialFile);
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in_s, null);
                new importContactsAsync(parser, BaseActivity.progressDialog).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                tFilePath.setText("\"import file doesn't exist\"");
                Toast.makeText(getActivity().getApplicationContext(), "import file doesn't exist", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }
    }

    private void parseLogsXML(XmlPullParser parser) {
        try {
            sFilePath = preferences.getString("sFilePath", "");
            File initialFile = new File(sFilePath);
            if (initialFile.exists()) {
                InputStream in_s = new FileInputStream(initialFile);
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in_s, null);
                new importCallLogsAsync(parser, BaseActivity.progressDialog).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                tFilePath.setText("\"import file doesn't exist\"");
                Toast.makeText(getActivity().getApplicationContext(), "import file doesn't exist", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }
    }


    public void setOnClickListeners() {
        open_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileOptionsInterface.openFile();
            }
        });

        import_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String extension = tFilePath.getText().toString().substring(tFilePath.getText().toString().length()-4);

                if (extension.contains(".lco")) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                    // Setting Dialog Title
                    alertDialog.setTitle("Confirm Contacts Import...");

                    // Setting Dialog Message
                    alertDialog.setMessage("Are you sure you want import contacts ?");

                    // Setting Icon to Dialog
                    alertDialog.setIcon(R.drawable.ic_contacts);

                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            // Write your code here to invoke YES event
                            if (pullParserFactory != null) {
                                try {
                                    XmlPullParser parser = pullParserFactory.newPullParser();
                                    parseXML(parser);
                                } catch (Exception e) {

                                }
                            }
                        }
                    });

                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event
                            dialog.cancel();
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Select an .lco file", Toast.LENGTH_SHORT).show();
                }
            }
        });

        import_logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String extension = tFilePath.getText().toString().substring(tFilePath.getText().toString().length()-4);
                if (extension.contains(".lca")) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                    // Setting Dialog Title
                    alertDialog.setTitle("Confirm Call Logs Import...");

                    // Setting Dialog Message
                    alertDialog.setMessage("Are you sure you want import call logs ?");

                    // Setting Icon to Dialog
                    alertDialog.setIcon(R.drawable.ic_logs);

                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            // Write your code here to invoke YES event
                            if (pullParserFactory != null) {
                                try {
                                    XmlPullParser parser = pullParserFactory.newPullParser();
                                    parseLogsXML(parser);
                                } catch (Exception e) {

                                }
                            }
                        }
                    });

                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event
                            dialog.cancel();
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Select an .lca file", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cloud_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // import from database Server

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public class importContactsAsync extends AsyncTask<Void, String, Void> {

        private XmlPullParser parser;
        private MyProgressDialog progressDialog;
        private ArrayList<ContentProviderOperation> ops;
        private int rawContactID;

        public importContactsAsync(XmlPullParser pullParser, MyProgressDialog pd) {
            parser = pullParser;
            progressDialog = pd;
            ops = new ArrayList<>();
            rawContactID = ops.size();
        }

        private void createContact() {
            // Adding insert operation to operations list
            // to insert a new raw contact in the table ContactsContract.RawContacts
            ops.clear();
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());
        }

        private void createName(String name) {
            // Adding insert operation to operations list
            // to insert display name in the table ContactsContract.Data
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .build());
        }

        private void createNumber(String number, String type) {
            // Adding insert operation to operations list
            // to insert Mobile Number in the table ContactsContract.Data
            int t = Integer.parseInt(type);
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, t)
                    .build());
        }

        private void createImage(String image) {
            if (image != null && !image.isEmpty()) {
                byte[] imageAsBytes = Base64.decode(image.getBytes(), 0);
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, imageAsBytes)
                        .build());
            }
        }

        private void addContact() {
            try {
                // Executing all the insert operations as a single database transaction
                getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                int eventType = parser.getEventType();
                CContacts contacts = null;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String name;
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            contactsArrayList = new ArrayList<>();
                            break;
                        case XmlPullParser.START_TAG: {
                            name = parser.getName();
                            if (name.equals("contact-info")) {
                                contacts = new CContacts();
                            } else if (contacts != null) {
                                if (name.equals("name")) {
                                    contacts.setName(parser.nextText().trim());
                                    publishProgress(contacts.getName());
                                } else if (name.equals("phone")) {
                                    contacts.setNumber(parser.nextText().trim());
                                } else if (name.equals("type")) {
                                    contacts.setType(parser.nextText().trim());
                                } else if (name.equals("image")) {
                                    contacts.setImageString(parser.nextText().trim());
                                }
                            }
                        }
                        break;

                        case XmlPullParser.END_TAG: {
                            name = parser.getName();
                            if (name.equalsIgnoreCase("contact-info") && contacts != null) {
                                contactsArrayList.add(contacts);
                            }
                        }
                        break;
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (CContacts contacts : contactsArrayList) {
                createContact();
                publishProgress("adding", contacts.getName());
                createName(contacts.getName());
                createNumber(contacts.getNumber(), contacts.getType());
                createImage(contacts.getImageString());
                addContact();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setText("Importing Contacts...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            contactsRestoredObj.onContactsRestored();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values[0] == "adding") {
                progressDialog.setText("Adding " + values[1]);
            } else {
                progressDialog.setText("Extracting " + values[0]);
            }
            super.onProgressUpdate(values);
        }
    }

    public class importCallLogsAsync extends AsyncTask<Void, String, Void> {

        private XmlPullParser parser;
        private MyProgressDialog progressDialog;
        private ArrayList<ContentProviderOperation> ops;
        private int rawContactID;

        public importCallLogsAsync(XmlPullParser pullParser, MyProgressDialog pd) {
            parser = pullParser;
            progressDialog = pd;
            ops = new ArrayList<>();
            rawContactID = ops.size();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                int eventType = parser.getEventType();
                CCallLogs callLogs = null;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String name;
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            callLogsArrayList = new ArrayList<>();
                            break;
                        case XmlPullParser.START_TAG: {
                            name = parser.getName();
                            if (name.equals("call_log_info")) {
                                callLogs = new CCallLogs();
                            } else if (callLogs != null) {
                                if (name.equals("number")) {
                                    callLogs.setNumber(parser.nextText().trim());
                                    publishProgress(callLogs.getNumber());
                                } else if (name.equals("type")) {
                                    callLogs.setType(Integer.parseInt(parser.nextText().trim()));
                                } else if (name.equals("date")) {
                                    callLogs.setDate(Long.parseLong(parser.nextText().trim()));
                                } else if (name.equals("duration")) {
                                    callLogs.setDuration(Integer.parseInt(parser.nextText().trim()));
                                }
                            }
                        }
                        break;

                        case XmlPullParser.END_TAG: {
                            name = parser.getName();
                            if (name.equalsIgnoreCase("call_log_info") && callLogs != null) {
                                callLogsArrayList.add(callLogs);
                            }
                        }
                        break;
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (CCallLogs callLogs : callLogsArrayList) {
                ContentValues values = new ContentValues();
                values.put(CallLog.Calls.NUMBER, callLogs.getNumber());
                publishProgress("adding",callLogs.getNumber());
                values.put(CallLog.Calls.DATE, callLogs.getDate());
                values.put(CallLog.Calls.DURATION, callLogs.getDuration());
                values.put(CallLog.Calls.TYPE, callLogs.getType());
                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return null;
                }
                getActivity().getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setText("Importing Call Logs...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            contactsRestoredObj.onContactsRestored();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if(values[0]=="adding")
            {
                progressDialog.setText("Adding "+values[1]);
            }
            else {
                progressDialog.setText("Extracting " + values[0]);
            }
            super.onProgressUpdate(values);
        }
    }
}
