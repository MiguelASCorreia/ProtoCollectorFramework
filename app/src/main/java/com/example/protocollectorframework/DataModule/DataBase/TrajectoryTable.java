package com.example.protocollectorframework.DataModule.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.protocollectorframework.Complements.SharedMethods;

import java.util.Date;
import java.util.HashMap;

/**
 * Database table that stores the information associated to the GPS trajectories
 */
public class TrajectoryTable {

    public static final String TABLE_NAME = "Trajectory_table";
    public static final String TRAJECTORY_ID = "_id";
    public static final String VISIT_ID = "Visit_id";
    public static final String TRAJECTORY_PATH = "Trajectory_path";
    public static final String TRAJECTORY_OWNER = "Trajectory_owner";
    public static final String TRAJECTORY_CREATION_TIME = "Trajectory_creation_time";
    public static final String TRAJECTORY_EDIT_TIME = "Trajectory_edit_time";
    public static final String TRAJECTORY_DELETE_TIME = "Trajectory_delete_time";

    private Context context;

    private DataBase db;

    /**
     * Constructor
     * @param context: current context
     */
    public TrajectoryTable(Context context){
        db = new DataBase(context);
        this.context = context;
    }

    /**
     * Creates the table
     * @param sqLiteDatabase: SQLite database
     */
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

    /**
     * Drops the table
     * @param sqLiteDatabase: SQLite database
     */
    protected static void dropTable(SQLiteDatabase sqLiteDatabase) {
        String drop = "DROP TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(drop + TABLE_NAME);
    }


    /**
     * Creates a trajectory associated to a visit
     * @param visit_id: visit's identifier
     * @param path: file to the external storage path (GPX file)
     * @param owner: file owner identifier
     * @return trajectory's identifier
     */
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

    /**
     * Initializes a trajectory associated to a visit.
     * @param visit_id: visit's identifier
     * @param owner: file owner identifier
     * @return trajectory's identifier
     */
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

    /**
     * Completes a trajectory with the external path to the GPX file
     * @param trajectoryId: trajectory's identifier
     * @param path: external storage path to the file
     * @return true if completed with success, false otherwise
     */
    public boolean completeTrajectory(String trajectoryId, String path){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            ContentValues cv = new ContentValues();
            cv.put(TRAJECTORY_PATH, path);

            return db.update(TABLE_NAME, cv, TRAJECTORY_ID + " =?", new String[]{trajectoryId}) > 0;

        }catch(SQLException e){
            Log.e("error", e.toString());
            return false;
        }finally {
            db.close();
        }

    }

    /**
     * Creates a trajectory associated to a visit
     * @param visit_id: visit's identifier
     * @param path: file to the external storage path (GPX file)
     * @param owner: file owner identifier
     * @param db: SQLite database
     * @return trajectory's identifier
     */
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


    /**
     * Edits the information of a given trajectory
     * @param visit_id: visit's identifier
     * @param path: file to the external storage path (GPX file)
     * @param owner: file owner identifier
     * @return true if edited with success, false otherwise
     */
    public boolean editTrajectory(String visit_id, String path, String owner){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return editTrajectory(visit_id, path,owner, db) > 0;
        }catch(SQLException e){
            Log.e("error", e.toString());
            return false;
        }finally {
            db.close();
        }
    }

    /**
     * Edits the information of a given trajectory
     * @param visit_id: visit's identifier
     * @param path: file to the external storage path (GPX file)
     * @param owner: file owner identifier
     * @param db: SQLite database
     * @return number of rows affected (bigger than zero if success)
     */
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

    /**
     * Deletes the trajectories from a given visit
     * @param visit_id: visit's identifier
     * @return true if deleted with success, false otherwise
     */
    public boolean deleteTrack(String visit_id) {
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return db.delete(TABLE_NAME,  VISIT_ID + " = ?",new String[]{visit_id}) > 0;
        }catch (SQLException e){
            Log.e("error", e.toString());
            return false;
        }
    }

    /**
     * Returns the trajectory id associated to a visit and owner
     * @param visit_id: visit's identifier
     * @param owner: owner's identifier
     * @return trajectory's identifier
     */
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

    /**
     * Returns a structure that maps all owners to its corresponding trajectory file path from a given visit
     * @param visit_id: visit's identifier
     * @return structure that maps all owners to its corresponding trajectory file
     */
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
