package com.example.protocollectorframework.DataModule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.protocollectorframework.DataModule.Data.ComplementaryData;
import com.example.protocollectorframework.Extra.SharedMethods;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Database table that stores the information associated to complementary observations associated to visits
 */
public class ComplementaryTable {
    public static final String TABLE_NAME = "Complementary_visit_table";
    public static final String COMPLEMENTARY_ID = "_id";
    public static final String VISIT_ID = "Visit_id";
    public static final String COMPLEMENTARY_START_TIME = "Complementary_start_time";
    public static final String COMPLEMENTARY_END_TIME = "Complementary_end_time";
    public static final String COMPLEMENTARY_CREATION_TIME = "Complementary_creation_time";
    public static final String COMPLEMENTARY_EDIT_TIME = "Complementary_edit_time";
    public static final String COMPLEMENTARY_DELETE_TIME = "Complementary_delete_time";
    public static final String COMPLEMENTARY_EOI_JSON = "Complementary_eoi_json";
    public static final String COMPLEMENTARY_INFO_JSON = "Complementary_info_json";
    public static final String COMPLEMENTARY_SYNC = "Complementary_sync";
    public static final String COMPLEMENTARY_VERSION = "Complementary_version";

    private Context context;

    private DataBase db;

    /**
     * Constructor
     * @param context: current context
     */
    public ComplementaryTable(Context context){
        db = new DataBase(context);
        this.context = context;
    }

    /**
     * Creates the table
     * @param sqLiteDatabase: SQLite database
     */
    protected static void createTable(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + COMPLEMENTARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                COMPLEMENTARY_START_TIME + " TEXT, " +
                COMPLEMENTARY_END_TIME + " TEXT, " +
                COMPLEMENTARY_CREATION_TIME + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                COMPLEMENTARY_EDIT_TIME + " TEXT, " +
                COMPLEMENTARY_DELETE_TIME + " TEXT, " +
                COMPLEMENTARY_SYNC + " INTEGER DEFAULT 0," +
                COMPLEMENTARY_EOI_JSON + " TEXT, " +
                COMPLEMENTARY_INFO_JSON + " TEXT, " +
                COMPLEMENTARY_VERSION + " TEXT DEFAULT 1," +
                VISIT_ID + " TEXT, " +
                "FOREIGN KEY(" + VISIT_ID + ") " + "REFERENCES " + VisitTable.TABLE_NAME + "(" + VisitTable.VISIT_ID + "))";


        sqLiteDatabase.execSQL(createTable);
    }

    /**
     * Fetch the creation timestamp from a complementary observation
     * @param complementary_id: visit's identifier
     * @return complementary observation's creation timestamp
     */
    public String getComplementaryCreationTime(String complementary_id){
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT + " + COMPLEMENTARY_CREATION_TIME + " FROM " + TABLE_NAME + " WHERE " + COMPLEMENTARY_ID + " = ?", new String[]{complementary_id});

        try{
            if (res != null) {
                res.moveToFirst();
                return res.getString(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            res.close();
            db.close();

        }

        return  null;
    }

    /**
     * Creates a complementary observation associated to a visit
     * @param visit_id: visit's identifier
     * @return complementary observation's identifier
     */
    public String initializeComplementary(String visit_id){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return initializeComplementary(visit_id, db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return null;
        }finally {
            db.close();
        }
    }

    /**
     * Creates a complementary observation associated to a visit
     * @param visit_id: visit's identifier
     * @param db: SQLite database
     * @return complementary observation's identifier
     */
    private String initializeComplementary(String visit_id, SQLiteDatabase db){
        try {
            ContentValues cv = new ContentValues();
            cv.put(VISIT_ID, visit_id);
            cv.put(COMPLEMENTARY_START_TIME, Long.toString(System.currentTimeMillis()));
            long id = db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            return Long.toString(id);
        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }
    }

    /**
     * Cancels the complementary observation deleting it from the table
     * @param complementary_id: complementary observation's identifier
     * @return true if deleted with success, false otherwise
     */
    public boolean cancelComplementary(String complementary_id) {
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return db.delete(TABLE_NAME, COMPLEMENTARY_ID +" = ?",new String[]{complementary_id}) > 0;
        }catch (SQLException e){
            Log.e("error", e.toString());
            return false;
        }
    }

    /**
     * Creates a complementary observation with all it's information
     * @param start_time: visit start time in milliseconds
     * @param end_time: visit ending time in milliseconds
     * @param eoi_json: JSON string with the information from the EOIs
     * @param info_json: JSON string with extra information
     * @param visit_id: visit's identifier
     * @param version: complementary observation's version
     * @return complementary observation's identifier
     */
    public String downloadComplementary(String start_time, String end_time, String eoi_json, String info_json, String visit_id, int version){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return downloadComplementary(start_time, end_time,eoi_json,info_json,visit_id,version,db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return null;
        }finally {
            db.close();
        }
    }

    /**
     * Creates a complementary observation with all it's information
     * @param start_time: visit start time in milliseconds
     * @param end_time: visit ending time in milliseconds
     * @param eoi_json: JSON string with the information from the EOIs
     * @param info_json: JSON string with extra information
     * @param visit_id: visit's identifier
     * @param version: complementary observation's version
     * @param db: SQLite database
     * @return complementary observation's identifier
     */
    private String downloadComplementary(String start_time, String end_time, String eoi_json, String info_json, String visit_id, int version, SQLiteDatabase db){
        try {
            ContentValues cv = new ContentValues();
            cv.put(COMPLEMENTARY_START_TIME, start_time);
            cv.put(COMPLEMENTARY_END_TIME,end_time);
            cv.put(COMPLEMENTARY_EOI_JSON,eoi_json);
            cv.put(COMPLEMENTARY_INFO_JSON,info_json);
            cv.put(COMPLEMENTARY_VERSION,version);
            cv.put(VISIT_ID,visit_id);
            cv.put(COMPLEMENTARY_SYNC,1);

            long id = db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);

            return Long.toString(id);
        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }
    }

    /**
     * Changes the complementary observation sync status to "uploaded"
     * @param complementary_id: complementary observation's identifier
     * @return true if changed with success, false otherwise
     */
    public long uploadComplementary(String complementary_id){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return updateVisitFlag(complementary_id, 1, db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return -1;
        }finally {
            db.close();
        }
    }

    /**
     * Changes the complementary observation sync status to "error on upload"
     * @param complementary_id: complementary observation's identifier
     * @return true if changed with success, false otherwise
     */
    public long errorOnUpload(String complementary_id){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return updateVisitFlag(complementary_id, -1, db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return -1;
        }finally {
            db.close();
        }
    }

    /**
     * Updates the sync flag of a given complementary observation
     * @param complementary_id: complementary observation's identifier
     * @param sync_flag: sync flag
     * @param db: SQLite database
     * @return number of rows affected (bigger than 1 if success)
     */
    private long updateVisitFlag(String complementary_id, int sync_flag, SQLiteDatabase db){
        try {
            ContentValues cv = new ContentValues();
            cv.put(COMPLEMENTARY_SYNC, sync_flag);
            return db.update(TABLE_NAME, cv, COMPLEMENTARY_ID + " = ?", new String[]{complementary_id});

        }catch(Exception e){
            Log.e("error", e.toString());
            return -1;
        }
    }

    /**
     * Terminates an ongoing complementary observation
     * @param complementary_id: complementary observation's identifier
     * @param eoi_json: JSON string with the information from the EOIs
     * @param info_json: JSON string with extra information
     * @return true if updated with success, false otherwise
     */
    public boolean finishComplementary(String complementary_id, String eoi_json, String info_json){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return finishComplementary(complementary_id,eoi_json,info_json, db) > 0;
        }catch(SQLException e){
            Log.e("error", e.toString());
            return false;
        }finally {
            db.close();
        }
    }

    /**
     * Terminates an ongoing complementary observation
     * @param complementary_id: complementary observation's identifier
     * @param eoi_json: JSON string with the information from the EOIs
     * @param info_json: JSON string with extra information
     * @return number of rows affected (bigger than 1 if success)
     */
    private long finishComplementary(String complementary_id, String eoi_json, String info_json, SQLiteDatabase db){
        try {
            ContentValues cv = new ContentValues();
            cv.put(COMPLEMENTARY_END_TIME, Long.toString(System.currentTimeMillis()));
            cv.put(COMPLEMENTARY_EOI_JSON,eoi_json);
            cv.put(COMPLEMENTARY_INFO_JSON,info_json);

            return db.update(TABLE_NAME, cv, COMPLEMENTARY_ID + " = ?", new String[]{complementary_id});

        }catch(Exception e){
            Log.e("error", e.toString());
            return -1;
        }
    }

    /**
     * Fetch a complementary observation by it's identifier
     * @param complementary_id: complementary observation's identifier
     * @return complementary observation data object
     */
    public ComplementaryData getComplementaryByID(String complementary_id){
        ComplementaryData complementaryData = null;
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE "  + COMPLEMENTARY_DELETE_TIME + " is null AND " + COMPLEMENTARY_ID + " = ?", new String[]{complementary_id} );
        try {
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                complementaryData = getComplementary(res);
                res.moveToNext();
            }

            return complementaryData;

        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }finally {
            res.close();
            db.close();
        }
    }

    /**
     * Fetch a complementary observation by the corresponding visit identifier
     * @param visit_id: visit's identifier
     * @return complementary observation data object
     */
    public ComplementaryData getComplementaryByVisitId(String visit_id){
        ComplementaryData complementaryData = null;
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE "  + COMPLEMENTARY_DELETE_TIME + " is null AND " + VISIT_ID + " = ?", new String[]{visit_id} );
        try {
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                complementaryData = getComplementary(res);
                res.moveToNext();
            }

            return complementaryData;

        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }finally {
            res.close();
            db.close();
        }
    }

    /**
     * Fetch a complementary observation given a cursor
     * @param cursor: query's cursor
     * @return complementary observation data object
     */
    private ComplementaryData getComplementary(Cursor cursor){
        if(cursor == null || cursor.getCount() == 0) {
            return null;
        }

        try {
            String ID = cursor.getString(cursor.getColumnIndex(COMPLEMENTARY_ID));

            String visit_id = cursor.getString(cursor.getColumnIndex(VISIT_ID));
            long start = cursor.getLong(cursor.getColumnIndex(COMPLEMENTARY_START_TIME));
            long end = cursor.getLong(cursor.getColumnIndex(COMPLEMENTARY_END_TIME));
            String eoi_json = cursor.getString(cursor.getColumnIndex(COMPLEMENTARY_EOI_JSON));
            String info_json = cursor.getString(cursor.getColumnIndex(COMPLEMENTARY_INFO_JSON));

            return new ComplementaryData(ID,visit_id,start,end,eoi_json,info_json);
        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }
    }

    /**
     * Edits a complementary observation
     * @param complementary_id: complementary observation's identifier
     * @param eoi_json: JSON string with the information from the EOIs
     * @param info_json: JSON string with extra information
     * @return true if edited with success, false otherwise
     */
    public long editComplementary(String complementary_id, String eoi_json, String info_json){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return editComplementary(complementary_id,eoi_json,info_json, db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return -1;
        }finally {
            db.close();
        }
    }

    /**
     * Edits a complementary observation
     * @param complementary_id: complementary observation's identifier
     * @param eoi_json: JSON string with the information from the EOIs
     * @param info_json: JSON string with extra information
     * @return number of rows affected (bigger than 1 if success)
     */
    private long editComplementary(String complementary_id, String eoi_json, String info_json, SQLiteDatabase db){
        try {
            ContentValues cv = new ContentValues();
            cv.put(COMPLEMENTARY_END_TIME, Long.toString(System.currentTimeMillis()));
            cv.put(COMPLEMENTARY_EOI_JSON,eoi_json);
            cv.put(COMPLEMENTARY_INFO_JSON,info_json);
            cv.put(COMPLEMENTARY_EDIT_TIME, SharedMethods.dateToUTCString(new Date()));

            int oldVersion = getVersion(complementary_id,db);
            if(oldVersion > 0) {
                int newVersion = oldVersion + 1;
                cv.put(COMPLEMENTARY_VERSION, newVersion);
            }

            cv.put(COMPLEMENTARY_SYNC,2);

            return db.update(TABLE_NAME, cv, COMPLEMENTARY_ID + " = ?", new String[]{complementary_id});

        }catch(Exception e){
            Log.e("error", e.toString());
            return -1;
        }
    }

    /**
     * Fetch the sync status from a given complementary observation
     * @param complementary_id: complementary observation's identifier
     * @return sync status
     */
    public int getSyncStatus(String complementary_id){
        int status = Integer.MIN_VALUE;
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT "+ COMPLEMENTARY_SYNC +" FROM " + TABLE_NAME + " WHERE "  + COMPLEMENTARY_ID + " = ? ", new String[]{complementary_id} );
        try{
            res.moveToFirst();

            status = res.getInt(res.getColumnIndex(COMPLEMENTARY_SYNC));

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            res.close();
            db.close();
        }
        return status;
    }

    /**
     * Fetch the version of a given complementary observation
     * @param complementary_id: complementary observation's identifier
     * @return complementary observation's version
     */
    public int getComplementaryVersion(String complementary_id){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return getVersion(complementary_id,db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return 0;
        }finally {
            db.close();
        }
    }

    /**
     * Fetch the version of a given complementary observation
     * @param complementary_id: complementary observation's identifier
     * @param db: SQLite database
     * @return complementary observation's version
     */
    private int getVersion(String complementary_id, SQLiteDatabase db) {
        try {

            Cursor res = db.rawQuery("SELECT + " + COMPLEMENTARY_VERSION + " FROM " + TABLE_NAME + " WHERE " + COMPLEMENTARY_ID + " = ?", new String[]{complementary_id});

            if (res != null) {
                res.moveToFirst();
                return res.getInt(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return 0;

    }

    /**
     * Fetch the list of not uploaded complementary observation (sync status != 1)
     * @return list of not uploaded complementary observation
     */
    public List<ComplementaryData> getNotUploadedComplementary() {
        List<ComplementaryData> array_list = new ArrayList<ComplementaryData>();
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE "  + COMPLEMENTARY_END_TIME + " is not null AND " + COMPLEMENTARY_DELETE_TIME + " is null AND " + COMPLEMENTARY_SYNC + " != 1", null );
        try {
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                array_list.add(getComplementary(res));
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


}
