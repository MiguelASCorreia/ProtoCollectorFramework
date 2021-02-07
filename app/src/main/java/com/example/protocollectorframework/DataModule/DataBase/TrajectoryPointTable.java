package com.example.protocollectorframework.DataModule.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.protocollectorframework.DataModule.Data.LocationData;

import java.util.ArrayList;
import java.util.List;

/**
 * Database table that stores the information associated to the points that constitute trajectory's segments
 */
public class TrajectoryPointTable {
    public static final String TABLE_NAME = "Trajectory_point_table";
    public static final String POINT_ID = "_id";
    public static final String SEGMENT_ID = "segment_id";
    public static final String POINT_TIMESTAMP = "point_timestamp";
    public static final String POINT_LAT = "point_lat";
    public static final String POINT_LN = "point_ln";
    public static final String POINT_ELEVATION = "point_ele";
    public static final String POINT_ACCURACY = "point_acc";
    public static final String POINT_SAT = "point_sat";
    public static final String POINT_INFO = "point_info";
    public static final String POINT_CREATION_TIME = "point_creation_time";
    public static final String POINT_EDIT_TIME = "point_edit_time";
    public static final String POINT_DELETE_TIME = "point_delete_time";

    private Context context;

    private DataBase db;

    /**
     * Constructor
     *
     * @param context: current context
     */
    public TrajectoryPointTable(Context context) {
        db = new DataBase(context);
        this.context = context;
    }

    /**
     * Creates the table
     *
     * @param sqLiteDatabase: SQLite database
     */
    protected static void createTable(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + POINT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                SEGMENT_ID + " INTEGER, " +
                POINT_LAT + " REAL, " +
                POINT_LN + " REAL, " +
                POINT_ELEVATION + " REAL, " +
                POINT_ACCURACY + " REAL, " +
                POINT_SAT + " INTEGER, " +
                POINT_TIMESTAMP + " INTEGER, " +
                POINT_INFO + " TEXT, " +
                POINT_CREATION_TIME + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                POINT_EDIT_TIME + " TEXT, " +
                POINT_DELETE_TIME + " TEXT, " +
                "FOREIGN KEY(" + SEGMENT_ID + ") " + "REFERENCES " + TrajectorySegmentTable.TABLE_NAME + "(" + TrajectorySegmentTable.SEGMENT_ID + ") on delete cascade)";


        sqLiteDatabase.execSQL(createTable);
    }

    /**
     * Drops the table
     *
     * @param sqLiteDatabase: SQLite database
     */
    protected static void dropTable(SQLiteDatabase sqLiteDatabase) {
        String drop = "DROP TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(drop + TABLE_NAME);
    }

    /**
     * Adds a new point to the table
     *
     * @param segment_id:   segment's identifier
     * @param locationData: location data object associated to the point
     * @return point's identifier
     */
    public long addPoint(String segment_id, LocationData locationData) {
        SQLiteDatabase db = this.db.getWritableDatabase();
        try {
            return addPoint(segment_id, locationData, db);
        } catch (SQLException e) {
            Log.e("error", e.toString());
            return -1;
        } finally {
            db.close();
        }
    }

    /**
     * Adds a new point to the table
     *
     * @param segment_id:   segment's identifier
     * @param locationData: location data object associated to the point
     * @param db:           SQLite database
     * @return point's identifier
     */
    private long addPoint(String segment_id, LocationData locationData, SQLiteDatabase db) {
        if (segment_id == null || locationData == null || db == null)
            return -1;

        try {
            ContentValues cv = new ContentValues();

            cv.put(SEGMENT_ID, segment_id);
            cv.put(POINT_LAT, locationData.getLat());
            cv.put(POINT_LN, locationData.getLng());
            cv.put(POINT_ELEVATION, locationData.getElevation());
            cv.put(POINT_ACCURACY, locationData.getAccuracy());
            cv.put(POINT_SAT, locationData.getSatNumber());
            cv.put(POINT_TIMESTAMP, locationData.getTimestamp());
            cv.put(POINT_INFO,locationData.getInfo());

            return db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);

        } catch (Exception e) {
            Log.e("error", e.toString());
            return -1;
        }
    }

    /**
     * Fetch the all the points from a given trajectory
     *
     * @param trajectory_id: trajectory's identifier
     * @return list of points associated to the given trajectory
     */
    public List<LocationData> getPointsFromTrajectory(String trajectory_id) {
        java.util.List<LocationData> list = new ArrayList<LocationData>();

        SQLiteDatabase db = this.db.getReadableDatabase();

        String select = TABLE_NAME + "." + POINT_LAT + "," +
                TABLE_NAME + "." + POINT_LN + "," +
                TABLE_NAME + "." + POINT_ELEVATION + "," +
                TABLE_NAME + "." + POINT_ACCURACY + "," +
                TABLE_NAME + "." + POINT_SAT + "," +
                TABLE_NAME + "." + POINT_TIMESTAMP;


        Cursor res = db.rawQuery("SELECT " + select +
                " FROM " + TABLE_NAME + " INNER JOIN " + TrajectorySegmentTable.TABLE_NAME +
                " ON " + TABLE_NAME + "." + SEGMENT_ID + " = " + TrajectorySegmentTable.TABLE_NAME + "." + TrajectorySegmentTable.SEGMENT_ID +
                " WHERE " + TrajectorySegmentTable.TRAJECTORY_ID + " = ?" + " ORDER BY " + POINT_TIMESTAMP + " ASC", new String[]{trajectory_id});

        try {
            res.moveToFirst();

            while (!res.isAfterLast()) {
                list.add(getPoint(res));
                res.moveToNext();
            }

            return list;
        } catch (Exception e) {
            Log.e("error", e.toString());
            return null;
        } finally {
            res.close();
            db.close();
        }
    }

    /**
     * Fetch the information associated to a point given a cursor
     *
     * @param cursor: query's cursor
     * @return location data object associated to the point
     */
    protected LocationData getPoint(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        try {
            double lat = cursor.getDouble(cursor.getColumnIndex(POINT_LAT));
            double ln = cursor.getDouble(cursor.getColumnIndex(POINT_LN));
            double ele = cursor.getDouble(cursor.getColumnIndex(POINT_ELEVATION));
            float acc = cursor.getFloat(cursor.getColumnIndex(POINT_ACCURACY));
            int sat = cursor.getInt(cursor.getColumnIndex(POINT_SAT));
            long timestamp = cursor.getLong(cursor.getColumnIndex(POINT_TIMESTAMP));
            String info = cursor.getString(cursor.getColumnIndex(POINT_INFO));
            return new LocationData(lat, ln, timestamp, ele, acc, sat,info);

        } catch (Exception e) {
            Log.e("error", e.toString());
            return null;
        }
    }


}
