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
/**
 * Database table that stores the information of the configuration files
 */
public class ConfigTable {

    public static final String TABLE_NAME = "Config_table";

    public static final String CONFIG_NAME = "config_name";

    public static final String CONFIG_VERSION = "config_version";

    public static final String CONFIG_PATH = "config_path";

    public static final String CONFIG_CREATION_TIME = "config_creation_time";
    public static final String CONFIG_EDIT_TIME = "config_edit_time";
    public static final String CONFIG_DELETE_TIME = "config_delete_time";

    private DataBase db;

    /**
     * Constructor
     * @param context: current context
     */
    public ConfigTable(Context context){
        db = new DataBase(context);
    }

    /**
     * Creates the table
     * @param sqLiteDatabase: SQLite database
     */
    protected static void createTable(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + CONFIG_NAME + " TEXT PRIMARY KEY, " +
                CONFIG_CREATION_TIME + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                CONFIG_EDIT_TIME + " TEXT, " +
                CONFIG_DELETE_TIME + " TEXT, " +
                CONFIG_PATH + " TEXT," +
                CONFIG_VERSION + " INTEGER)";
        sqLiteDatabase.execSQL(createTable);
    }

    /**
     * Drops the table
     * @param sqLiteDatabase: SQLite database
     */
    protected static void dropTable(SQLiteDatabase sqLiteDatabase) {
        String drop = "DROP TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(drop + TABLE_NAME);
    }

    /**
     * Creates a new file
     * @param config_name: file's name
     * @param version: file's version
     * @param path: file's external storage path
     * @return file identifier
     */
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

    /**
     * Creates a new file
     * @param config_name: file's name
     * @param version: file's version
     * @param path: file's external storage path
     * @param db: SQLite database
     * @return file identifier
     */
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

    /**
     * Tags the desired file as deleted via the deletion timestamp
     * @param name: file's name
     */
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

    /**
     * Fetch the version of a given file
     * @param name: file's name
     * @return file's version
     */
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

    /**
     * Fetch the edit timestamp of a given file
     * @param name: file's name
     * @return file's timestamp
     */
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

    /**
     * Fetch the external storage path of a given file
     * @param name: file's name
     * @return file's external storage path
     */
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


    /**
     * Edits the information of a given file
     * @param name: file's name
     * @param version: file's version
     * @param file_path: file's external storage path
     */
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
