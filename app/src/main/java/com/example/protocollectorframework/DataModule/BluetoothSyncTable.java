package com.example.protocollectorframework.DataModule;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.protocollectorframework.DataModule.Data.BluetoothSyncData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BluetoothSyncTable {
    protected static final String TABLE_NAME = "Bluetooth_sync_table";
    protected static final String SYNC_ID = "_id";
    protected static final String SYNC_TIMESTAMP = "sync_timestamp";
    protected static final String SYNC_PARTNER = "sync_partner";
    protected static final String SYNC_TYPE = "sync_type";
    protected static final String VISIT_ID = "visit_id";


    private DataBase db;

    public BluetoothSyncTable(Context context){
        db = new DataBase(context);
    }

    protected static void createTable(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + SYNC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                VISIT_ID + " INTEGER, " +
                SYNC_TYPE + " INTEGER, " +
                SYNC_TIMESTAMP + " TEXT, " +
                SYNC_PARTNER + " TEXT, " +
                "FOREIGN KEY(" + VISIT_ID + ") " + "REFERENCES " + VisitTable.TABLE_NAME + "(" + VisitTable.VISIT_ID + "))";
        sqLiteDatabase.execSQL(createTable);
    }

    protected static void dropTable(SQLiteDatabase sqLiteDatabase) {
        String drop = "DROP TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(drop + TABLE_NAME);
    }



    public long addSyncLog(String visit_id, long timestamp, String partner, boolean endVisitSync){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return addSyncLog(visit_id, timestamp,partner,endVisitSync, db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return -1;
        }finally {
            db.close();
        }
    }


    private long addSyncLog(String visit_id, long timestamp, String partner, boolean endVisitSync, SQLiteDatabase db){
        if(visit_id == null || db == null)
            return -1;

        try {
            ContentValues cv = new ContentValues();
            cv.put(SYNC_TIMESTAMP, getLogFormatDate(timestamp));
            cv.put(SYNC_PARTNER, partner);
            cv.put(SYNC_TYPE,endVisitSync ? 1 : 0);
            cv.put(VISIT_ID,visit_id);

            return db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);

        }catch(Exception e){
            Log.e("error", e.toString());
            return -1;
        }
    }


    public List<BluetoothSyncData> getSyncsForVisit(String id){
        java.util.List<BluetoothSyncData> list = new ArrayList<BluetoothSyncData>();

        SQLiteDatabase db = this.db.getReadableDatabase();

        Cursor res = db.rawQuery("SELECT *" +
                " FROM " + TABLE_NAME +
                " WHERE " + VISIT_ID +  " = ?" + " ORDER BY " + SYNC_TIMESTAMP + " ASC", new String[]{id});

        try {
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                list.add(getSyncLog(res));
                res.moveToNext();
            }

            return list;
        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }finally {
            res.close();
            db.close();
        }
    }

    private BluetoothSyncData getSyncLog(Cursor cursor){
        if(cursor == null || cursor.getCount() == 0) {
            return null;
        }
        try {
            String partner = cursor.getString(cursor.getColumnIndex(SYNC_PARTNER));
            String timestamp = cursor.getString(cursor.getColumnIndex(SYNC_TIMESTAMP));
            int endVisitSync = cursor.getInt(cursor.getColumnIndex(SYNC_TYPE));

            return new BluetoothSyncData(partner,timestamp,endVisitSync == 1);
        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }
    }


    public static String getLogFormatDate(long milliSeconds){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.SSS");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }



}