package com.example.protocollectorframework.DataModule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.protocollectorframework.Extra.SharedMethods;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConfigTable {

    public static final String TABLE_NAME = "Config_table";

    public static final String CONFIG_NAME = "config_name";

    public static final String CONFIG_VERSION = "config_version";

    public static final String CONFIG_PATH = "config_path";

    public static final String CONFIG_CREATION_TIME = "config_creation_time";
    public static final String CONFIG_EDIT_TIME = "config_edit_time";
    public static final String CONFIG_DELETE_TIME = "config_delete_time";

    private DataBase db;

    public ConfigTable(Context context){
        db = new DataBase(context);
    }


    protected static void createTable(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + CONFIG_NAME + " TEXT PRIMARY KEY, " +
                CONFIG_CREATION_TIME + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                CONFIG_EDIT_TIME + " TEXT, " +
                CONFIG_DELETE_TIME + " TEXT, " +
                CONFIG_PATH + " TEXT," +
                CONFIG_VERSION + " INTEGER)";
        sqLiteDatabase.execSQL(createTable);
    }


    public String addConfigFile(String config_name, int version, String path){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return addConfigFile(config_name, version,path, db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return null;
        }finally {
            db.close();
        }
    }

    private String addConfigFile(String config_name, int version, String path, SQLiteDatabase db){

        try {
            ContentValues cv = new ContentValues();
            cv.put(CONFIG_NAME, config_name);
            cv.put(CONFIG_VERSION, version);
            cv.put(CONFIG_PATH, path);

            long id = db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            return Long.toString(id);
        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }
    }



    protected static void dropTable(SQLiteDatabase sqLiteDatabase) {
        String drop = "DROP TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(drop + TABLE_NAME);
    }




    public void deleteConfig(String name){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CONFIG_DELETE_TIME, SharedMethods.dateToUTCString(new Date()));

            db.update(TABLE_NAME, contentValues, CONFIG_NAME + " = ?", new String[] {name});
        }catch(SQLException e){
            Log.e("error", e.toString());
        }finally {
            db.close();
        }
    }


    public int getConfigVersion(String name){
        SQLiteDatabase db = this.db.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT + " + CONFIG_VERSION + " FROM " + TABLE_NAME + " WHERE " + CONFIG_NAME + " = ?", new String[]{name});

        try{
            if (res != null && res.getCount() > 0) {
                res.moveToFirst();
                return res.getInt(0);
            }
        }catch(Exception e){
            Log.e("error", e.toString());
        }finally {
            db.close();
            if(res != null)
                res.close();
        }
        return 0;
    }

    public String getConfigEditTime(String name){
        SQLiteDatabase db = this.db.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT + " + CONFIG_EDIT_TIME + " FROM " + TABLE_NAME + " WHERE " + CONFIG_NAME + " = ?", new String[]{name});

        try{
            if (res != null && res.getCount() > 0) {
                res.moveToFirst();
                return res.getString(0);
            }
        }catch(Exception e){
            Log.e("error", e.toString());
        }finally {
            db.close();
            if(res != null)
                res.close();
        }
        return null;
    }

    public String getConfigPath(String name){
        SQLiteDatabase db = this.db.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT + " + CONFIG_PATH + " FROM " + TABLE_NAME + " WHERE " + CONFIG_NAME + " = ?", new String[]{name});

        try{
            if (res != null && res.getCount() > 0) {
                res.moveToFirst();
                return res.getString(0);
            }
        }catch(SQLException e){
            Log.e("error", e.toString());
        }finally {
            db.close();
            if(res != null)
                res.close();
        }
        return null;
    }

    public List<String> getPaths(){
        List<String> list = new ArrayList<>(6);
        SQLiteDatabase db = this.db.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT + " + CONFIG_PATH + " FROM " + TABLE_NAME + " WHERE " + CONFIG_DELETE_TIME + " is not null", null);

        try{
            if (res != null && res.getCount() > 0) {
                res.moveToFirst();
                list.add(res.getString(0));
            }
            return list;
        }catch(SQLException e){
            Log.e("error", e.toString());
        }finally {
            db.close();
            if(res != null)
                res.close();
        }
        return null;
    }



    public void editConfig(String name, int version, String file_path){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CONFIG_VERSION,version);
            contentValues.put(CONFIG_PATH,file_path);
            contentValues.put(CONFIG_EDIT_TIME, SharedMethods.dateToUTCString(new Date()));

            db.update(TABLE_NAME, contentValues, CONFIG_NAME + " = ?", new String[] {name});
        }catch(SQLException e){
            Log.e("error", e.toString());
        }finally {
            db.close();
        }
    }




}
