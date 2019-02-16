package com.inoek.apps.venuser.lastcallin;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class export_contacts extends Fragment {

    private View main_view;
    private ArrayList<Person> contactsArrayList;
    private ArrayList<CallLogs> callLogsArrayList;
    private static FileOutputStream fContactWriter;
    private Button bExportContact, bExportCallLogs, bCloudExport, bOpenFolder;
    private TextView tFilePath;
    private FileOptionsInterface fileOptionsInterface;
    private File fFile = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        fileOptionsInterface = BaseActivity.fOptionsInterface;
        super.onCreate(savedInstanceState);
    }

    private void startPrinting()
    {
        String tag;
        try {
            tag = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
            fContactWriter.write(tag.getBytes());
            tag = "<contact>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startLogPrinting()
    {
        String tag;
        try {
            tag = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
            fContactWriter.write(tag.getBytes());
            tag = "<call_log>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void startContactPrint()
    {
        String tag;
        try {
            tag = "<contact-info>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startCallLogPrint()
    {
        String tag;
        try {
            tag = "<call_log_info>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printName(String name)
    {
        String tag;
        try {
            tag = "<name>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
            fContactWriter.write(name.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
            tag = "</name>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printCallNumber(String number)
    {
        String tag;
        try {
            tag = "<number>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
            fContactWriter.write(number.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
            tag = "</number>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void printPhone(String phone)
    {
        String tag;
        try {
            tag = "<phone>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
            fContactWriter.write(phone.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
            tag = "</phone>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printCallType(String type)
    {
        String tag;
        try {
            tag = "<type>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
            fContactWriter.write(type.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
            tag = "</type>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printType(String type)
    {
        String tag;
        try {
            tag = "<type>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
            fContactWriter.write(type.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
            tag = "</type>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void printCallDate(String date)
    {
        String tag;
        try {
            tag = "<date>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
            fContactWriter.write(date.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
            tag = "</date>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void printImage(String url)
    {
        String tag;
        if(url!=null) {
                try {
                    tag = "<image>";
                    fContactWriter.write(tag.getBytes());
                    tag = "\n";
                    fContactWriter.write(tag.getBytes());
                    fContactWriter.write(url.getBytes());
                    tag = "</image>";
                    fContactWriter.write(tag.getBytes());
                    tag = "\n";
                    fContactWriter.write(tag.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private void printCallDuration(String duration)
    {
        String tag;
        try {
            tag = "<duration>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
            fContactWriter.write(duration.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
            tag = "</duration>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void endContactPrint()
    {
        String tag;
        try {
            tag = "</contact-info>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void endCallLogPrint()
    {
        String tag;
        try {
            tag = "</call_log_info>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void endExport()
    {
        String tag;
        try {
            tag = "</contact>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void endCallLogExport()
    {
        String tag;
        try {
            tag = "</call_log>";
            fContactWriter.write(tag.getBytes());
            tag = "\n";
            fContactWriter.write(tag.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private boolean createFile()
    {
        boolean isCreated = false;
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/lastcallin/contacts/");
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir, "contactsfile.lco");
        try {
            if(!file.exists())
            {
                file.createNewFile();
            }
            fContactWriter = new FileOutputStream(file,false);
            fFile = file;
            isCreated = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCreated;
    }

    private boolean createLogFile()
    {
        boolean isCreated = false;
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/lastcallin/contacts/");
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir, "callLogsfile.lca");
        try {
            if(!file.exists())
            {
                file.createNewFile();
            }
            fContactWriter = new FileOutputStream(file,false);
            fFile = file;
            isCreated = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCreated;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_export_contacts, container, false);
        main_view = rootView;
        bOpenFolder = (Button) main_view.findViewById(R.id.open_folder);
        bExportContact = (Button) main_view.findViewById(R.id.export_contacts);
        bExportCallLogs = (Button) main_view.findViewById(R.id.export_callLogs);
        bCloudExport = (Button) main_view.findViewById(R.id.cloud_export);
        // free version
        bCloudExport.setEnabled(false);
        tFilePath = (TextView) main_view.findViewById(R.id.path);
        contactsArrayList =  BaseActivity.contactArrayList;
        callLogsArrayList = BaseActivity.callLogsArrayList;
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            bExportContact.setEnabled(false);
            bExportCallLogs.setEnabled(false);
            bCloudExport.setEnabled(false);
        }
        setUpOnClickListeners();
        return rootView;
    }

    private void setUpOnClickListeners()
    {
        bOpenFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileOptionsInterface.showFolder();
            }
        });

        bExportContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Export to local file
                if(createFile())
                {
                    new exportContactsAsync(BaseActivity.progressDialog).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

        bExportCallLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(createLogFile())
                {
                    new exportCallLogsAsync(BaseActivity.progressDialog).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

        bCloudExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Export to database in Server
            }
        });
    }

    private class exportContactsAsync extends AsyncTask<Void,Void,Void> {

        private MyProgressDialog mProgressDialog;

        public exportContactsAsync(MyProgressDialog pd)
        {
            mProgressDialog = pd;
        }

        @Override
        protected Void doInBackground(Void... params) {
            startPrinting();
            // iterate through every item in contactArrayList and print all of them using
            // xml printing functions created above
            for(Person p : contactsArrayList)
            {
                startContactPrint();
                printName(p.getmName());
                printPhone(p.getmPhoneNumber());
                printType(String.valueOf(p.getmType()));
                printImage(p.getmImageUri());
                endContactPrint();
            }
            endExport();
            return null;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setText("Exporting Contacts...");
            mProgressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgressDialog.dismiss();
            tFilePath.setText(tFilePath.getText()+"\nExported Contacts at : \n "+fFile.getAbsolutePath());
            super.onPostExecute(aVoid);
        }
    }

    private class exportCallLogsAsync extends AsyncTask<Void,Void,Void> {

        private MyProgressDialog mProgressDialog;

        public exportCallLogsAsync(MyProgressDialog pd)
        {
            mProgressDialog = pd;
        }

        @Override
        protected Void doInBackground(Void... params) {
            startLogPrinting();
            ArrayList<CallLogs> callLogsArrayList_temp = callLogsArrayList;
            Collections.reverse(callLogsArrayList_temp);
            for(CallLogs c : callLogsArrayList_temp)
            {
                startCallLogPrint();
                if(c.getmNumber().equals("Unsaved")) {
                    printCallNumber(c.getmName());
                }
                else
                {
                    printCallNumber(c.getmNumber());
                }
                printCallType(c.getmType());
                printCallDate(String.valueOf(c.getmOrigDate()));
                printCallDuration(String.valueOf(c.getmOrigDuration()));
                endCallLogPrint();
            }
            endCallLogExport();
            Collections.reverse(callLogsArrayList_temp);
            return null;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setText("Exporting Call Logs...");
            mProgressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgressDialog.dismiss();
            tFilePath.setText(tFilePath.getText()+"\nExported Call Logs at : \n "+fFile.getAbsolutePath());
            super.onPostExecute(aVoid);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
