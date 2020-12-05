package com.example.protocollectorframework.DataModule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.protocollectorframework.Extra.SharedMethods;

import java.util.Date;
import java.util.HashMap;

public class TrajectoryTable {

    public static final String TABLE_NAME = "Trajectory_table";

    public static final String TRAJECTORY_ID = "_id";
    public static final String VISIT_ID = "Visit_id";
    public static final String TRAJECTORY_PATH = "Trajectory_path";
    public static final String TRAJECTORY_OWNER = "Trajectory_owner";
    public static final String TRAJECTORY_CREATION_TIME = "Trajectory_creation_time";
    public static final String TRAJECTORY_EDIT_TIME = "Trajectory_edit_time";
    public static final String TRAJECTORY_DELETE_TIME = "Trajectory_delete_time";

    /// UNICO (VISIT_ID,TRAJECTORY_OWNER)
    private Context context;

    private DataBase db;

    public TrajectoryTable(Context context){
        db = new DataBase(context);
        this.context = context;
    }

    protected static void createTable(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + TRAJECTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                VISIT_ID + " INTEGER, " +
                TRAJECTORY_CREATION_TIME + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                TRAJECTORY_EDIT_TIME + " TEXT, " +
                TRAJECTORY_DELETE_TIME + " TEXT, " +
                TRAJECTORY_PATH + " TEXT, " +
                TRAJECTORY_OWNER + " TEXT, " +
                "FOREIGN KEY(" + VISIT_ID + ") " + "REFERENCES " + VisitTable.TABLE_NAME + "(" + VisitTable.VISIT_ID + "))";


        sqLiteDatabase.execSQL(createTable);
    }

    protected static void dropTable(SQLiteDatabase sqLiteDatabase) {
        String drop = "DROP TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(drop + TABLE_NAME);
    }


    public String addTrajectory(String visit_id, String path, String owner){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return addTrajectory(visit_id, path,owner, db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return null;
        }finally {
            db.close();
        }
    }

    public String initializeTrajectory(String visit_id, String owner){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            try {
                ContentValues cv = new ContentValues();
                cv.put(VISIT_ID, visit_id);
                cv.put(TRAJECTORY_OWNER,owner);

                long id = db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                return Long.toString(id);
            }catch(Exception e){
                Log.e("error", e.toString());
                return null;
            }
        }catch(SQLException e){
            Log.e("error", e.toString());
            return null;
        }finally {
            db.close();
        }
    }

    public int completeTrajectory(String trajectoryId, String path){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            ContentValues cv = new ContentValues();
            cv.put(TRAJECTORY_PATH, path);

            return db.update(TABLE_NAME, cv, TRAJECTORY_ID + " =?", new String[]{trajectoryId});

        }catch(SQLException e){
            Log.e("error", e.toString());
            return -1;
        }finally {
            db.close();
        }

    }

    private String addTrajectory(String visit_id, String path, String owner, SQLiteDatabase db){

        try {
            ContentValues cv = new ContentValues();
            cv.put(VISIT_ID, visit_id);
            cv.put(TRAJECTORY_PATH, path);
            cv.put(TRAJECTORY_OWNER,owner);

            long id = db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            return Long.toString(id);
        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }
    }



    public long editTrajectory(String visit_id, String path, String owner){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return editTrajectory(visit_id, path,owner, db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return -1;
        }finally {
            db.close();
        }
    }



    private long editTrajectory(String visit_id, String path, String owner, SQLiteDatabase db){

        try {
            ContentValues cv = new ContentValues();
            cv.put(TRAJECTORY_PATH, path);
            cv.put(TRAJECTORY_EDIT_TIME, SharedMethods.dateToUTCString(new Date()));

            return db.update(TABLE_NAME, cv, VISIT_ID + " = ? AND " + TRAJECTORY_OWNER + " = ?", new String[]{visit_id,owner});
        }catch(Exception e){
            Log.e("error", e.toString());
            return -1;
        }
    }

    public boolean deleteTrack(String visit_id) {
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return db.delete(TABLE_NAME,  VISIT_ID + " = ?",new String[]{visit_id}) > 0;
        }catch (SQLException e){
            Log.e("error", e.toString());
            return false;
        }
    }

    public String getTrajectoryId(String visit_id, String owner) {
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE "  + VISIT_ID + " =? AND " + TRAJECTORY_OWNER + " =? AND " + TRAJECTORY_DELETE_TIME + " is null", new String[]{visit_id,owner} );
        try {
            res.moveToFirst();

            while (!res.isAfterLast()) {

                if(res.getCount() == 0) {
                    return null;
                }

                try {
                    return res.getString(res.getColumnIndex(TRAJECTORY_ID));
                }catch(Exception e){
                    Log.e("error", e.toString());
                }
            }
        }catch(SQLException e){
            Log.e("error", e.toString());
        }finally {
            res.close();
            db.close();
        }

        return null;
    }

    public HashMap<String, String> getTrajectories(String visit_id) {
        HashMap<String, String> map = new HashMap<String, String>();
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE "  + VISIT_ID + " =? AND " + TRAJECTORY_DELETE_TIME + " is null", new String[]{visit_id} );
        try {
            res.moveToFirst();

            while (!res.isAfterLast()) {

                if(res.getCount() == 0) {
                    return null;
                }

                try {
                    String owner = res.getString(res.getColumnIndex(TRAJECTORY_OWNER));
                    String path = res.getString(res.getColumnIndex(TRAJECTORY_PATH));

                    map.put(owner,path);
                }catch(Exception e){
                    Log.e("error", e.toString());
                    return null;
                }

                res.moveToNext();
            }

            return map;
        }catch(SQLException e){
            Log.e("error", e.toString());
            return null;
        }finally {
            res.close();
            db.close();
        }
    }














}
