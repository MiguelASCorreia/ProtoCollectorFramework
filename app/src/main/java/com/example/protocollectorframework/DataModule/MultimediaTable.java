package com.example.protocollectorframework.DataModule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.protocollectorframework.DataModule.Data.LocationData;
import com.example.protocollectorframework.DataModule.Data.MultimediaData;
import com.example.protocollectorframework.Extra.SharedMethods;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Database table that stores the information associated to the multimedia files
 */
public class MultimediaTable {

    protected static final String TABLE_NAME = "Multimedia_table";
    protected static final String MULTIMEDIA_ID = "_id";
    protected static final String VISIT_ID = "visit_id";
    protected static final String MULTIMEDIA_TYPE = "multimedia_type";
    protected static final String MULTIMEDIA_PATH = "multimedia_path";
    protected static final String MULTIMEDIA_LAT = "multimedia_location_lat";
    protected static final String MULTIMEDIA_LN = "multimedia_location_ln";
    protected static final String MULTIMEDIA_ELEVATION = "multimedia_location_ele";
    protected static final String MULTIMEDIA_ACCURACY = "multimedia_location_acc";
    protected static final String MULTIMEDIA_SAT = "multimedia_location_sat";
    protected static final String MULTIMEDIA_POINT_TIMESTAMP = "multimedia_location_timestamp";
    protected static final String MULTIMEDIA_CREATION_TIME = "multimedia_creation_time";
    protected static final String MULTIMEDIA_EDIT_TIME = "multimedia_edit_time";
    protected static final String MULTIMEDIA_DELETE_TIME = "multimedia_delete_time";
    protected static final String MULTIMEDIA_SYNC = "multimedia_sync";
    protected static final String MULTIMEDIA_DESCRIPTION = "multimedia_description";
    protected static final String MULTIMEDIA_OWNER = "multimedia_owner";

    private DataBase db;

    /**
     * Constructor
     * @param context: current context
     */
    public MultimediaTable(Context context){
        db = new DataBase(context);
    }

    /**
     * Creates the table
     * @param sqLiteDatabase: SQLite database
     */
    protected static void createTable(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + MULTIMEDIA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                VISIT_ID + " INTEGER, " +
                MULTIMEDIA_TYPE + " TEXT, " +
                MULTIMEDIA_PATH + " TEXT, " +
                MULTIMEDIA_LAT + " REAL, " +
                MULTIMEDIA_LN + " REAL, " +
                MULTIMEDIA_ELEVATION + " REAL, " +
                MULTIMEDIA_ACCURACY + " REAL, " +
                MULTIMEDIA_SAT + " INTEGER, " +
                MULTIMEDIA_POINT_TIMESTAMP + " INTEGER, " +
                MULTIMEDIA_CREATION_TIME + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                MULTIMEDIA_EDIT_TIME + " TEXT, " +
                MULTIMEDIA_DELETE_TIME + " TEXT, " +
                MULTIMEDIA_OWNER + " TEXT, " +
                MULTIMEDIA_SYNC + " INTEGER DEFAULT 0," +
                MULTIMEDIA_DESCRIPTION + " TEXT DEFAULT NULL, " +
                "FOREIGN KEY(" + VISIT_ID + ") " + "REFERENCES " + VisitTable.TABLE_NAME + "(" + VisitTable.VISIT_ID + "))";
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
     * Fetch a file given a cursor
     * @param cursor: query's cursor
     * @return multimedia file data object
     */
    private MultimediaData getFile(Cursor cursor){
        if(cursor == null || cursor.getCount() == 0) {
            return null;
        }

        try {
            String id = cursor.getString(cursor.getColumnIndex(MULTIMEDIA_ID));
            String type = cursor.getString(cursor.getColumnIndex(MULTIMEDIA_TYPE));
            String path = cursor.getString(cursor.getColumnIndex(MULTIMEDIA_PATH));
            double lat = cursor.getDouble(cursor.getColumnIndex(MULTIMEDIA_LAT));
            double ln = cursor.getDouble(cursor.getColumnIndex(MULTIMEDIA_LN));
            double ele = cursor.getDouble(cursor.getColumnIndex(MULTIMEDIA_ELEVATION));
            float acc = cursor.getFloat(cursor.getColumnIndex(MULTIMEDIA_ACCURACY));
            int sat = cursor.getInt(cursor.getColumnIndex(MULTIMEDIA_SAT));
            long point_timestamp = cursor.getLong(cursor.getColumnIndex(MULTIMEDIA_POINT_TIMESTAMP));
            long timestamp = cursor.getLong(cursor.getColumnIndex(MULTIMEDIA_CREATION_TIME));
            String description = cursor.getString(cursor.getColumnIndex(MULTIMEDIA_DESCRIPTION));
            String owner = cursor.getString(cursor.getColumnIndex(MULTIMEDIA_OWNER));

            LocationData location = new LocationData(lat,ln,point_timestamp,ele,acc,sat);

            return new MultimediaData(id,type,path,timestamp,location,description,owner);
        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }
    }

    /**
     * Adds a description to a given file
     * @param multimedia_id: multimedia file identifier
     * @param description: description
     * @return true if description was added with success, false otherwise
     */
    public boolean addDescription(String multimedia_id, String description){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return addDescription(multimedia_id,description, db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return false;
        }finally {
            db.close();
        }
    }

    /**
     * Adds a description to a given file
     * @param multimedia_id: multimedia file identifier
     * @param description: description
     * @param db: SQLite database
     * @return true if description was added with success, false otherwise
     */
    private boolean addDescription(String multimedia_id, String description, SQLiteDatabase db){
        try {
            ContentValues cv = new ContentValues();
            cv.put(MULTIMEDIA_DESCRIPTION,description);

            return db.update(TABLE_NAME, cv, MULTIMEDIA_ID + " = ?", new String[]{multimedia_id}) > 0;

        }catch(Exception e){
            Log.e("error", e.toString());
            return false;
        }
    }

    /**
     * Tags the desired multimedia file as deleted via the deletion timestamp
     * @param multimedia_id: multimedia file identifier
     * @return true if tagged with success, false otherwise
     */
    public boolean deleteFile(String multimedia_id) {
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(MULTIMEDIA_DELETE_TIME, SharedMethods.dateToUTCString(new Date()));

            return db.update(TABLE_NAME, contentValues, MULTIMEDIA_ID + " = ?", new String[] {multimedia_id}) > 0;
        }catch (SQLException e){
            Log.e("error", e.toString());
            return false;
        }
    }

    /**
     * Deletes the file permanently from the database. This method can be used when the current visit is canceled and there is no need to store information from it.
     * @param multimedia_id: multimedia file indentifier
     * @return true if deleted with success, false otherwise
     */
    public boolean deleteFileOnCancel(String multimedia_id) {
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return db.delete(TABLE_NAME, MULTIMEDIA_ID +" = ?",new String[]{multimedia_id}) > 0;
        }catch (SQLException e){
            Log.e("error", e.toString());
            return false;
        }
    }

    /**
     * Fetch all multimedia files that are in this table
     * @return list of multimedia file data objects
     */
    public List<MultimediaData> getFiles() {
        List<MultimediaData> array_list = new ArrayList<MultimediaData>();
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME, null );
        try {
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                array_list.add(getFile(res));
                res.moveToNext();
            }

            return array_list;
        }catch(SQLException e){
            Log.e("error", e.toString());
            return null;
        }finally {
            res.close();
            db.close();
        }
    }

    /**
     * Creates a new file associated to a visit
     * @param multimediaData: multimedia file data object
     * @param visit_id: visit's identifier
     * @param sync_status: sync status identifier, to separate the cases when the file is added to the table by the user input or by other means
     * @return multimedia's identifier
     */
    public long addFile(MultimediaData multimediaData, String visit_id, int sync_status){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return addFile(multimediaData, visit_id, db,sync_status);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return -1;
        }finally {
            db.close();
        }
    }

    /**
     * Creates a new file associated to a visit
     * @param multimediaData: multimedia file data object
     * @param visit_id: visit's identifier
     * @param sync_status: sync status identifier, to separate the cases when the file is added to the table by the user input or by other means
     * @return multimedia's identifier
     */
    private long addFile(MultimediaData multimediaData, String visit_id, SQLiteDatabase db, int sync_status){
        if(multimediaData == null || db == null)
            return -1;

        try {
            ContentValues cv = new ContentValues();
            cv.put(MULTIMEDIA_TYPE, multimediaData.getType());
            cv.put(MULTIMEDIA_PATH, multimediaData.getPath());
            cv.put(VISIT_ID,visit_id);
            cv.put(MULTIMEDIA_SYNC,sync_status);

            if(multimediaData.getDescription() != null)
                cv.put(MULTIMEDIA_DESCRIPTION,multimediaData.getDescription());

            if(multimediaData.getOwner() != null)
                cv.put(MULTIMEDIA_OWNER,multimediaData.getOwner());

            LocationData locationData = multimediaData.getLocation();
            if(locationData != null){
                cv.put(MULTIMEDIA_LAT, locationData.getLat());
                cv.put(MULTIMEDIA_LN, locationData.getLng());
                cv.put(MULTIMEDIA_ELEVATION, locationData.getElevation());
                cv.put(MULTIMEDIA_ACCURACY, locationData.getAccuracy());
                cv.put(MULTIMEDIA_SAT, locationData.getSat_number());
                cv.put(MULTIMEDIA_POINT_TIMESTAMP, locationData.getTimestamp());
            }
            return db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);

        }catch(Exception e){
            Log.e("error", e.toString());
            return -1;
        }
    }

    /**
     * Fetch all the multimedia files associated to a given visit and type
     * @param visit_id: visit's identifier
     * @param type: multimedia file type identifier
     * @return list of multimedia from the visit
     */
    public List<MultimediaData> getMultimediaFromVisit(String visit_id, String type){
        java.util.List<MultimediaData> list = new ArrayList<MultimediaData>();

        SQLiteDatabase db = this.db.getReadableDatabase();

        Cursor res = db.rawQuery("SELECT *" +
                " FROM " + TABLE_NAME +
                " WHERE " + MULTIMEDIA_TYPE + " = ?" + " AND " + MULTIMEDIA_DELETE_TIME + " is null AND " + VISIT_ID +  " = ?" + " ORDER BY " + MULTIMEDIA_CREATION_TIME, new String[]{type, visit_id});

        try {
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                list.add(getFile(res));
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

    /**
     * Fetch all the multimedia files associated to a given visit
     * @param visit_id: visit's identifier
     * @return list of multimedia from the visit
     */
    public List<MultimediaData> getMultimediaFromVisit(String visit_id){
        java.util.List<MultimediaData> list = new ArrayList<MultimediaData>();

        SQLiteDatabase db = this.db.getReadableDatabase();

        Cursor res = db.rawQuery("SELECT *" +
                " FROM " + TABLE_NAME +
                " WHERE " + VISIT_ID +  " = ?"+ " AND " + MULTIMEDIA_DELETE_TIME + " is null" + " ORDER BY " + MULTIMEDIA_CREATION_TIME, new String[]{visit_id});

        try {
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                list.add(getFile(res));
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

    /**
     * Mark the file as synced
     * @param id: multimedia file identifier
     * @param db: SQLite database
     * @return number of columns affected (greater than zero if success)
     */
    private long markFileAsSync(String id, SQLiteDatabase db){
        try {
            ContentValues cv = new ContentValues();
            cv.put(MULTIMEDIA_SYNC,"1");

            return db.update(TABLE_NAME, cv, VISIT_ID + " = ? AND " + MULTIMEDIA_SYNC + " = ?", new String[]{id,"0"});

        }catch(Exception e){
            Log.e("error", e.toString());
            return -1;
        }
    }

    /**
     * Mark the file as synced
     * @param id: multimedia file identifier
     * @return true if marked successfully, false otherwise
     */
    public boolean markFileAsSync(String id){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return markFileAsSync(id, db) > 0;
        }catch(SQLException e){
            Log.e("error", e.toString());
            return false;
        }finally {
            db.close();
        }
    }


    /**
     * Fetch the multimedia files that where not synced, from a visit
     * @param visit_id: visit's identifier
     * @return list of multimedia data object that where not synced
     */
    public List<MultimediaData> getNotSyncedMultimediaFromVisit(String visit_id){
        java.util.List<MultimediaData> list = new ArrayList<MultimediaData>();

        SQLiteDatabase db = this.db.getReadableDatabase();

        Cursor res = db.rawQuery("SELECT *" +
                " FROM " + TABLE_NAME +
                " WHERE " + VISIT_ID +  " = ?" + " AND " + MULTIMEDIA_DELETE_TIME + " is null AND "  + MULTIMEDIA_SYNC + " = ?" + " ORDER BY " + MULTIMEDIA_CREATION_TIME, new String[]{visit_id,"0"});

        try {
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                list.add(getFile(res));
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

    /**
     * Deletes all multimedia files from the table for a given visit
     * @param visit_id: visit's identifier
     * @return true if deleted with success, false otherwise
     */
    public boolean deleteAllMultimediaFromVisit(String visit_id) {
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return db.delete(TABLE_NAME, VISIT_ID +" = ?",new String[]{visit_id}) > 0;
        }catch (SQLException e){
            Log.e("error", e.toString());
            return false;
        }
    }


    /**
     * Fetch a multimedia file given it's identifier
     * @param id: multimedia file identifier
     * @return multimedia file data object
     */
    public MultimediaData getMultimediaForId(String id){
        List<MultimediaData> array_list = new ArrayList<MultimediaData>();
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE "  + MULTIMEDIA_ID + " = ? AND " + MULTIMEDIA_DELETE_TIME + " is null", new String[]{id} );
        try {
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                array_list.add(getFile(res));
                res.moveToNext();
            }

            return array_list.get(0);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return null;
        }finally {
            res.close();
            db.close();
        }
    }
}
