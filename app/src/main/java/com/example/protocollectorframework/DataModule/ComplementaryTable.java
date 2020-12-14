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

    public ComplementaryTable(Context context){
        db = new DataBase(context);
        this.context = context;
    }

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

    public boolean cancelComplementary(String complementary_id) {
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return db.delete(TABLE_NAME, COMPLEMENTARY_ID +" = ?",new String[]{complementary_id}) > 0;
        }catch (SQLException e){
            Log.e("error", e.toString());
            return false;
        }
    }

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

    public long uploadComplementary(String complementary_id){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return updateVisitFlag(complementary_id, db,1);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return -1;
        }finally {
            db.close();
        }
    }

    public long errorOnUpload(String complementary_id){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return updateVisitFlag(complementary_id, db,-1);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return -1;
        }finally {
            db.close();
        }
    }

    private long updateVisitFlag(String complementary_id, SQLiteDatabase db, int sync_flag){
        try {
            ContentValues cv = new ContentValues();
            cv.put(COMPLEMENTARY_SYNC, sync_flag);
            return db.update(TABLE_NAME, cv, COMPLEMENTARY_ID + " = ?", new String[]{complementary_id});

        }catch(Exception e){
            Log.e("error", e.toString());
            return -1;
        }
    }


    public long finishComplementary(String complementary_id, String eoi_json, String info_json){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return finishComplementary(complementary_id,eoi_json,info_json, db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return -1;
        }finally {
            db.close();
        }
    }

    private long finishComplementary(String visit_id, String eoi_json, String info_json, SQLiteDatabase db){
        try {
            ContentValues cv = new ContentValues();
            cv.put(COMPLEMENTARY_END_TIME, Long.toString(System.currentTimeMillis()));
            cv.put(COMPLEMENTARY_EOI_JSON,eoi_json);
            cv.put(COMPLEMENTARY_INFO_JSON,info_json);

            return db.update(TABLE_NAME, cv, COMPLEMENTARY_ID + " = ?", new String[]{visit_id});

        }catch(Exception e){
            Log.e("error", e.toString());
            return -1;
        }
    }


    public ComplementaryData getComplementaryByID(String id){
        ComplementaryData complementaryData = null;
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE "  + COMPLEMENTARY_DELETE_TIME + " is null AND " + COMPLEMENTARY_ID + " = ?", new String[]{id} );
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

    public ComplementaryData getOnGoingComplementaryByID(String id){
        ComplementaryData complementaryData = null;
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE "  + COMPLEMENTARY_END_TIME + " is null AND " + COMPLEMENTARY_DELETE_TIME + " is null AND " + COMPLEMENTARY_ID + " = ?", new String[]{id} );
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

    public ComplementaryData getComplementaryByVisitIdForMultimediaCheck(String visit_id){
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


    public long editComplementary(String id, String eoi_json, String info_json){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return editComplementary(id,eoi_json,info_json, db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return -1;
        }finally {
            db.close();
        }
    }
    

    private long editComplementary(String id, String eoi_json, String info_json, SQLiteDatabase db){
        try {
            ContentValues cv = new ContentValues();
            cv.put(COMPLEMENTARY_END_TIME, Long.toString(System.currentTimeMillis()));
            cv.put(COMPLEMENTARY_EOI_JSON,eoi_json);
            cv.put(COMPLEMENTARY_INFO_JSON,info_json);
            cv.put(COMPLEMENTARY_EDIT_TIME, SharedMethods.dateToUTCString(new Date()));

            int oldVersion = getVersion(id,db);
            if(oldVersion > 0) {
                int newVersion = oldVersion + 1;
                cv.put(COMPLEMENTARY_VERSION, newVersion);
            }

            cv.put(COMPLEMENTARY_SYNC,2);

            return db.update(TABLE_NAME, cv, COMPLEMENTARY_ID + " = ?", new String[]{id});

        }catch(Exception e){
            Log.e("error", e.toString());
            return -1;
        }
    }


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
