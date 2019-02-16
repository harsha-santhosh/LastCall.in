package com.inoek.apps.venuser.lastcallin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 2;

	// Database Name
	private static final String DATABASE_NAME = "contactsManager";

	// Contacts table name
	private static final String TABLE_CONTACTS = "contacts";
    private static final String TABLE_CALL_LOGS = "call_logs";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name_str";
	private static final String KEY_PH_NO = "phone_str";
	private static final String KEY_IMG_STR = "image_str";
	private static final String KEY_TYPE = "type_str";

	private static final String KEY_CALL_NUM = "call_num";
	private static final String KEY_CALL_TYPE = "call_type";
	private static final String KEY_DURATION = "duration";
	private static final String KEY_DATE_TIME = "date_time";


	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALL_LOGS);
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY, " + KEY_NAME + " TEXT NOT NULL, "
				+ KEY_PH_NO + " TEXT NOT NULL, "+ KEY_IMG_STR + " TEXT, " + KEY_TYPE + " TEXT)";
		db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_CALL_LOGS_TABLE = "CREATE TABLE " + TABLE_CALL_LOGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_CALL_NUM + " TEXT NOT NULL, "
                + KEY_CALL_TYPE + " TEXT NOT NULL, "+ KEY_DURATION + " TEXT NOT NULL, " + KEY_DATE_TIME + " TEXT NOT NULL)";
        db.execSQL(CREATE_CALL_LOGS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALL_LOGS);
		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new contact
	void addContact(CContacts contact) {

	    if(!checkExistenceOfContact(contact.getNumber())) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_NAME, contact.getName()); // Contact Name
            values.put(KEY_PH_NO, contact.getNumber()); // Contact Phone
            values.put(KEY_IMG_STR, contact.getImageString()); // Contact Image
            values.put(KEY_TYPE, contact.getType()); // Contact Type

            // Inserting Row
            db.insert(TABLE_CONTACTS, null, values);
            db.close(); // Closing database connection
        }
        else
        {
            updateContact(contact);
        }
	}

	private boolean checkExistenceOfContact(String phNumber)
    {
        boolean bExists = false;
        int t_count = 0;
        String query = "SELECT COUNT(*) as T_COUNT FROM "+TABLE_CONTACTS+" WHERE "+KEY_PH_NO+" = '"+phNumber+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                t_count = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
		cursor.close();
		db.close();
        if(t_count > 0)
            bExists = true;

        return bExists;
    }

    private boolean checkExistenceOfCallLogs(CCallLogs callLog)
    {
        boolean bExists = false;
        int t_count = 0;
        String query = "SELECT COUNT(*) as T_COUNT FROM "+TABLE_CALL_LOGS+" WHERE "+KEY_CALL_NUM+" = '"+callLog.getNumber()+"'"
                + " AND "+KEY_CALL_TYPE+" = '"+callLog.getType()+"' AND "+KEY_DURATION+" = '"+ String.valueOf(callLog.getDuration())+"'"
                + " AND "+KEY_DATE_TIME+" = '"+ String.valueOf(callLog.getDate())+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                t_count = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        if(t_count > 0)
            bExists = true;

        return bExists;
    }

    // Adding new call log
    void addCallLog(CCallLogs callLogs) {

        if(!checkExistenceOfCallLogs(callLogs)) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_CALL_NUM, callLogs.getNumber()); // Contact Name
            values.put(KEY_CALL_TYPE, String.valueOf(callLogs.getType())); // Contact Phone
            values.put(KEY_DURATION, String.valueOf(callLogs.getDuration())); // Contact Image
            values.put(KEY_DATE_TIME, String.valueOf(callLogs.getDate())); // Contact Type

            // Inserting Row
            db.insert(TABLE_CALL_LOGS, null, values);
            db.close(); // Closing database connection
        }
    }

	/*// Getting single contact
	Contact getContact(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
				KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2));
		// return contact
		return contact;
	}
*/
	CContacts getContact(String phNumber)
	{
		CContacts contacts = null;
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS+" WHERE phone_str = '"+phNumber+"'";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				contacts = new CContacts();
				contacts.setName(cursor.getString(1));
				contacts.setNumber(cursor.getString(2));
				contacts.setImageString(cursor.getString(3));
				contacts.setType(cursor.getString(4));
			} while (cursor.moveToNext());
		}
        cursor.close();
        db.close();
		return contacts;
	}
	
	// Getting All Contacts
	public ArrayList<CContacts> getAllContacts() {
		ArrayList<CContacts> contactList = new ArrayList<CContacts>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				CContacts contact = new CContacts();
				contact.setName(cursor.getString(1));
				contact.setNumber(cursor.getString(2));
				contact.setImageString(cursor.getString(3));
				contact.setType(cursor.getString(4));
				// Adding contact to list
				contactList.add(contact);
			} while (cursor.moveToNext());
		}
        cursor.close();
        db.close();
		// return contact list
		return contactList;
	}

	// Getting All Contacts
	public ArrayList<CCallLogs> getAllCallLogs() {
		ArrayList<CCallLogs> callLogsList = new ArrayList<CCallLogs>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CALL_LOGS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				CCallLogs callLog = new CCallLogs();
				callLog.setNumber(cursor.getString(1));
				callLog.setType(Integer.parseInt(cursor.getString(2)));
				callLog.setDuration(Integer.parseInt(cursor.getString(3)));
				callLog.setDate(Long.parseLong(cursor.getString(4)));
				// Adding contact to list
				callLogsList.add(callLog);
			} while (cursor.moveToNext());
		}
        cursor.close();
        db.close();
		// return contact list
		return callLogsList;
	}

	// Updating single contact
	public void updateContact(CContacts contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, contact.getName());
		values.put(KEY_TYPE,contact.getType());
		values.put(KEY_IMG_STR,contact.getImageString());

		// updating row
        int rowsAffected = db.update(TABLE_CONTACTS, values, KEY_PH_NO + " = ?",
                new String[] { String.valueOf(contact.getNumber()) });
        System.out.println(rowsAffected);
        db.close();
	}

	// Deleting single contact
	public void deleteContact(CContacts contact) {
		SQLiteDatabase db = this.getWritableDatabase();
		int rows = db.delete(TABLE_CONTACTS, KEY_PH_NO + " = ?",
				new String[] { String.valueOf(contact.getNumber()) });
		db.close();
	}

	// Deleting single contact
	public void deleteCallLog(CCallLogs callLogs) {
		SQLiteDatabase db = this.getWritableDatabase();
		int rows = db.delete(TABLE_CALL_LOGS,
				KEY_CALL_NUM + " = ? AND "+KEY_CALL_TYPE+" = ? AND "+KEY_DATE_TIME+" = ? AND "+KEY_DURATION+" = ?",
				new String[] { callLogs.getNumber(), String.valueOf(callLogs.getType()), String.valueOf(callLogs.getDate()), String.valueOf(callLogs.getDuration())});
		db.close();
	}



	// Getting contacts Count
	public int getContactsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
        cursor.close();
        db.close();

		// return count
		return count;
	}

    // Getting contacts Count
    public int getCallLogsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CALL_LOGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        // return count
        return count;
    }

}
