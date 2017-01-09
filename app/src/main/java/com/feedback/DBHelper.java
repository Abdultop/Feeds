package com.feedback;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by abdul on 29/7/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Feedback.db";

    public static final String ITEM_TABLE_NAME = "item";
    public static final String ITEM_COLUMN_ID = "_id";
    public static final String ITEM_COLUMN_DATE = "date";
    public static final String ITEM_COLUMN_NAME = "itemname";

    public static final String USER_TABLE_NAME = "user";
    public static final String USER_COLUMN_ID = "_id";
    public static final String USER_COLUMN_NAME = "name";
    public static final String USER_COLUMN_MOBILE = "mobile";

    public static final String FEEDBACK_TABLE_NAME = "feedback";
    public static final String FEEDBACK_COLUMN_ID = "_id";
    public static final String FEEDBACK_COLUMN_MOBILE = "mobile";
    public static final String FEEDBACK_COLUMN_ITEMNAME = "items";
    public static final String FEEDBACK_COLUMN_RATING = "ratings";




    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table item " +
                        "(_id integer primary key autoincrement, date text not null, itemname text not null)"
        );

        db.execSQL(
                "create table user " +
                        "(_id integer primary key autoincrement, name text not null, mobile text not null)"
        );

        db.execSQL(
                "create table feedback " +
                        "(_id integer primary key autoincrement, mobile text not null, items text not null, ratings text not null)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean clearTable(String table_name){    //MR 16-6-16 @Rahman For clear table
        SQLiteDatabase db= this.getWritableDatabase();
        db.execSQL("Delete from "+table_name);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name ='"+table_name+"';");
        db.close();
        return true;
    }

    public boolean insertMenuItem(String date, String name)
    {
        SQLiteDatabase db= this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("date", date);
            contentValues.put("itemname", name);

            db.insert("item", null, contentValues);
            // Log.d("Inserting the menu - ", Name +"-"+ type);
        }
        finally {
            //close the db  if you no longer need it
            if (db != null && db.isOpen()){ db.close();}
        }
        return true;
    }

    public boolean insertUsers(String name, String mobile)
    {
        SQLiteDatabase db= this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            contentValues.put("mobile", mobile);

            db.insert("user", null, contentValues);
            // Log.d("Inserting the menu - ", Name +"-"+ type);
        }
        finally {
            //close the db  if you no longer need it
            if (db != null && db.isOpen()){ db.close();}
        }
        return true;
    }

    public boolean insertFeedback(String mobile, String items, String ratings)
    {
        SQLiteDatabase db= this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("mobile", mobile);
            contentValues.put("items", items);
            contentValues.put("ratings", ratings);

            db.insert("feedback", null, contentValues);
            // Log.d("Inserting the menu - ", Name +"-"+ type);
        }
        finally {
            //close the db  if you no longer need it
            if (db != null && db.isOpen()){ db.close();}
        }
        return true;
    }

    public ArrayList<String> getAllFeedback()
    {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery("select * from feedback", null);
        res.moveToFirst();
        try {
            while (res.isAfterLast() == false) {
                //Log.d("inside dbhelper - ", res.getString(res.getColumnIndex(MENU_COLUMN_NAME)));
                array_list.add(res.getString(res.getColumnIndex(FEEDBACK_COLUMN_MOBILE))+"$"+res.getString(res.getColumnIndex(FEEDBACK_COLUMN_ITEMNAME))+"$"+res.getString(res.getColumnIndex(FEEDBACK_COLUMN_RATING)));
                res.moveToNext();
            }
        }
        finally {
            //close the cursor
            if (res != null){ res.close();}
            //close the db  if you no longer need it
            if (db != null && db.isOpen()){ db.close();}

        }
        return array_list;
    }

    public ArrayList<String> getAllUsers()
    {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery("select * from user", null);
        res.moveToFirst();
        try {
            while (res.isAfterLast() == false) {
                //Log.d("inside dbhelper - ", res.getString(res.getColumnIndex(MENU_COLUMN_NAME)));
                array_list.add(res.getString(res.getColumnIndex(USER_COLUMN_NAME))+"$"+res.getString(res.getColumnIndex(USER_COLUMN_MOBILE)));
                res.moveToNext();
            }
        }
        finally {
            //close the cursor
            if (res != null){ res.close();}
            //close the db  if you no longer need it
            if (db != null && db.isOpen()){ db.close();}

        }
        return array_list;
    }

    public ArrayList<String> getAllMenuItemName()
    {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery("select * from item ORDER BY date DESC", null);
        res.moveToFirst();
        try {
            while (res.isAfterLast() == false) {
                //Log.d("inside dbhelper - ", res.getString(res.getColumnIndex(MENU_COLUMN_NAME)));
                array_list.add(res.getString(res.getColumnIndex(ITEM_COLUMN_DATE))+"$"+res.getString(res.getColumnIndex(ITEM_COLUMN_NAME)));
                res.moveToNext();
            }
        }
        finally {
            //close the cursor
            if (res != null){ res.close();}
            //close the db  if you no longer need it
            if (db != null && db.isOpen()){ db.close();}

        }
        return array_list;
    }
}
