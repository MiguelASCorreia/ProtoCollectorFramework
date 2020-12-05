package com.example.protocollectorframework.DataModule;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TrajectorySegmentTable {

    public static final String TABLE_NAME = "Trajectory_segment_table";

    public static final String SEGMENT_ID = "_id";
    public static final String TRAJECTORY_ID = "Trajectory_id";
    public static final String SEGMENT_CREATION_TIME = "Segment_creation_time";

    private Context context;

    private DataBase db;

    public TrajectorySegmentTable(Context context){
        db = new DataBase(context);
        this.context = context;
    }

    protected static void createTable(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + SEGMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                TRAJECTORY_ID + " INTEGER, " +
                SEGMENT_CREATION_TIME + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + TRAJECTORY_ID + ") " + "REFERENCES " + TrajectoryTable.TABLE_NAME + "(" + TrajectoryTable.TRAJECTORY_ID + ") on delete cascade)";


        sqLiteDatabase.execSQL(createTable);
    }

    protected static void dropTable(SQLiteDatabase sqLiteDatabase) {
        String drop = "DROP TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(drop + TABLE_NAME);
    }

    public String createSegment(String trajectory_id){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return createSegment(trajectory_id, db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return null;
        }finally {
            db.close();
        }
    }

    private String createSegment(String trajectory_id, SQLiteDatabase db){
        if(trajectory_id == null || db == null)
            return null;

        try {
            ContentValues cv = new ContentValues();
            cv.put(TRAJECTORY_ID, trajectory_id);
            return Long.toString(db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE));

        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }
    }

}
