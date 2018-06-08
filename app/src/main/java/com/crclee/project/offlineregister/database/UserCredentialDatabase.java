package com.crclee.project.offlineregister.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.crclee.project.offlineregister.module.UserDataitems;

import java.util.ArrayList;
import java.util.List;

public class UserCredentialDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "user_credential_db";
    private static final String TABLE_NAME = "user_credential_table";


    private static final String KEY_ID = "_id";
    private static final String KEY_USER_CRD_PHONE = "user_phone";
    private static final String KEY_USER_CRD_EMAIL = "user_email";
    private static final String KEY_USER_CRD_AGE = "user_age";
    private static final String KEY_USER_CRD_NAME = "user_name";


    public UserCredentialDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_URL_LOADER_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +       //0
                KEY_USER_CRD_PHONE + " TEXT, " +                        //1
            KEY_USER_CRD_EMAIL + " TEXT, " +                            //2
                KEY_USER_CRD_AGE + " TEXT, " +                          //3
                KEY_USER_CRD_NAME + " TEXT);";                          //4
        db.execSQL(CREATE_URL_LOADER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    public void saveDataInDb(String phone,String email,String name,String age) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USER_CRD_PHONE, phone);
        values.put(KEY_USER_CRD_EMAIL, email);
        values.put(KEY_USER_CRD_AGE, age);
        values.put(KEY_USER_CRD_NAME, name);

        //inserting the row in table
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public int getAllCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public List<String> getUserDetails() {
        List<String> userItem = new ArrayList<String>();
        //select all query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                String phone=cursor.getString(1);
                userItem.add(phone);

                String email=cursor.getString(2);
                userItem.add(email);

                String age=cursor.getString(3);
                userItem.add(age);

                String name=cursor.getString(4);
                userItem.add(name);

                //adding item to list

            } while (cursor.moveToNext());
        }
        return userItem;
    }


    public void removeAllItems(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

}
