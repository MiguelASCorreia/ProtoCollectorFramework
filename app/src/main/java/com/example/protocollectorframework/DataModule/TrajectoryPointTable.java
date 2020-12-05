package com.example.protocollectorframework.DataModule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.protocollectorframework.DataModule.Data.LocationData;

import java.util.ArrayList;
import java.util.List;

public class TrajectoryPointTable {
    public static final String TABLE_NAME = "Trajectory_point_table";

    public static final String POINT_ID = "_id";
    public static final String SEGMENT_ID = "Segment_id";
    public static final String POINT_TIMESTAMP = "Point_timestamp";
    public static final String POINT_LAT = "Point_lat";
    public static final String POINT_LN = "Point_ln";
    public static final String POINT_ELEVATION = "Point_ele";
    public static final String POINT_ACCURACY = "Point_acc";
    public static final String POINT_SAT = "Point_sat";


    private Context context;

    private DataBase db;

    public TrajectoryPointTable(Context context){
        db = new DataBase(context);
        this.context = context;
    }

    protected static void createTable(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + POINT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                SEGMENT_ID + " INTEGER, " +
                POINT_LAT + " REAL, " +
                POINT_LN + " REAL, " +
                POINT_ELEVATION + " REAL, " +
                POINT_ACCURACY + " REAL, " +
                POINT_SAT + " INTEGER, " +
                POINT_TIMESTAMP + " INTEGER, " +
                "FOREIGN KEY(" + SEGMENT_ID + ") " + "REFERENCES " + TrajectorySegmentTable.TABLE_NAME + "(" + TrajectorySegmentTable.SEGMENT_ID + ") on delete cascade)";


        sqLiteDatabase.execSQL(createTable);
    }

    protected static void dropTable(SQLiteDatabase sqLiteDatabase) {
        String drop = "DROP TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(drop + TABLE_NAME);
    }

    public long addPoint(String segment_id, LocationData locationData){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return addPoint(segment_id, locationData, db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return -1;
        }finally {
            db.close();
        }
    }


    private long addPoint(String segment_id, LocationData locationData, SQLiteDatabase db){
        if(segment_id == null || locationData == null || db == null)
            return -1;

        try {
            ContentValues cv = new ContentValues();

            cv.put(SEGMENT_ID, segment_id);
            cv.put(POINT_LAT, locationData.getLat());
            cv.put(POINT_LN, locationData.getLng());
            cv.put(POINT_ELEVATION, locationData.getElevation());
            cv.put(POINT_ACCURACY, locationData.getAccuracy());
            cv.put(POINT_SAT, locationData.getSat_number());
            cv.put(POINT_TIMESTAMP, locationData.getTimestamp());

            return db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);

        }catch(Exception e){
            Log.e("error", e.toString());
            return -1;
        }
    }


    public List<LocationData> getPointsFromTrajectory(String trajectory_id){
        java.util.List<LocationData> list = new ArrayList<LocationData>();

        SQLiteDatabase db = this.db.getReadableDatabase();

        String select = TABLE_NAME +"."+ POINT_LAT + "," +
                        TABLE_NAME +"."+ POINT_LN + "," +
                        TABLE_NAME +"."+ POINT_ELEVATION + "," +
                        TABLE_NAME +"."+ POINT_ACCURACY + "," +
                        TABLE_NAME +"."+ POINT_SAT + "," +
                        TABLE_NAME +"."+ POINT_TIMESTAMP;


        Cursor res = db.rawQuery("SELECT "+ select +
                " FROM " + TABLE_NAME  + " INNER JOIN " + TrajectorySegmentTable.TABLE_NAME +
                " ON " + TABLE_NAME + "." + SEGMENT_ID + " = " + TrajectorySegmentTable.TABLE_NAME + "." + TrajectorySegmentTable.SEGMENT_ID +
                " WHERE " + TrajectorySegmentTable.TRAJECTORY_ID  + " = ?" + " ORDER BY " + POINT_TIMESTAMP + " ASC", new String[]{trajectory_id});

        try {
            res.moveToFirst();

            while (!res.isAfterLast()) {
                list.add(getPoint(res));
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


    protected LocationData getPoint(Cursor cursor){
        if(cursor == null || cursor.getCount() == 0) {
            return null;
        }

        try {
            double lat = cursor.getDouble(cursor.getColumnIndex(POINT_LAT));
            double ln = cursor.getDouble(cursor.getColumnIndex(POINT_LN));
            double ele = cursor.getDouble(cursor.getColumnIndex(POINT_ELEVATION));
            float acc = cursor.getFloat(cursor.getColumnIndex(POINT_ACCURACY));
            int sat = cursor.getInt(cursor.getColumnIndex(POINT_SAT));
            long timestamp = cursor.getLong(cursor.getColumnIndex(POINT_TIMESTAMP));

            return new LocationData(lat,ln,timestamp,ele,acc,sat);

        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }
    }


}
