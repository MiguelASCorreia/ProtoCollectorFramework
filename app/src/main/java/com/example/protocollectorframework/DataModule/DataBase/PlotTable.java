package com.example.protocollectorframework.DataModule.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.protocollectorframework.Complements.SharedMethods;
import com.example.protocollectorframework.DataModule.Data.LocationData;
import com.example.protocollectorframework.DataModule.Data.PlotData;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Database table that stores the information associated to the plots
 */
public class PlotTable {

    public static final String TABLE_NAME = "Plot_table";
    public static final String PLOT_ID = "_id";
    public static final String PLOT_ACRONYM = "plot_acronym";
    public static final String PLOT_NAME = "plot_name";
    public static final String PLOT_CENTER_LAT = "plot_center_lat";
    public static final String PLOT_CENTER_LN = "plot_center_ln";
    public static final String PLOT_POLYGON = "plot_polygon";
    public static final String PLOT_CREATION_TIME = "plot_creation_time";
    public static final String PLOT_EDIT_TIME = "plot_edit_time";
    public static final String PLOT_DELETE_TIME = "plot_delete_time";
    public static final String PLOT_INFO = "plot_info";

    private DataBase db;

    /**
     * Constructor
     *
     * @param context: current context
     */
    public PlotTable(Context context) {
        db = new DataBase(context);
    }

    /**
     * Creates the table
     *
     * @param sqLiteDatabase: SQLite database
     */
    protected static void createTable(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + PLOT_ID + " INTEGER PRIMARY KEY, " +
                PLOT_ACRONYM + " TEXT, " +
                PLOT_NAME + " TEXT, " +
                PLOT_POLYGON + " TEXT, " +
                PLOT_CENTER_LAT + " REAL, " +
                PLOT_CENTER_LN + " REAL, " +
                PLOT_CREATION_TIME + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                PLOT_EDIT_TIME + " TEXT, " +
                PLOT_DELETE_TIME + " TEXT, " +
                PLOT_INFO + " TEXT)";
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
     * Tags the desired plot as deleted via the deletion timestamp
     *
     * @param id: plot's identifier
     */
    public void deletePlotViaFlag(String id) {
        SQLiteDatabase db = this.db.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PLOT_DELETE_TIME, SharedMethods.dateToUTCString(new Date()));

            db.update(TABLE_NAME, contentValues, PLOT_ID + " = ?", new String[]{id});
        } catch (SQLException e) {
            Log.e("error", e.toString());
        } finally {
            db.close();
        }
    }

    /**
     * Edits the information of a plot
     *
     * @param id:      plot's identifier
     * @param info:    JSON string containing plot's information
     * @param acronym: plot's acronym
     */
    public void editInfo(String id, String info, String acronym) {
        SQLiteDatabase db = this.db.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PLOT_ACRONYM, acronym);
            contentValues.put(PLOT_INFO, info);
            contentValues.put(PLOT_EDIT_TIME, SharedMethods.dateToUTCString(new Date()));

            db.update(TABLE_NAME, contentValues, PLOT_ID + " = ?", new String[]{id});
        } catch (SQLException e) {
            Log.e("error", e.toString());
        } finally {
            db.close();
        }
    }

    /**
     * Edits the information of a plot
     *
     * @param id:      plot's identifier
     * @param info:    JSON string containing plot's information
     * @param acronym: plot's acronym
     * @param polygon: GeoJSON string containing plot's limits
     * @param lat:     center point latitude
     * @param ln:      center point longitude
     */
    public void editInfo(String id, String info, String acronym, String polygon, double lat, double ln) {
        SQLiteDatabase db = this.db.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PLOT_POLYGON, polygon);
            contentValues.put(PLOT_CENTER_LAT, lat);
            contentValues.put(PLOT_CENTER_LN, ln);
            contentValues.put(PLOT_ACRONYM, acronym);
            contentValues.put(PLOT_INFO, info);
            contentValues.put(PLOT_EDIT_TIME, SharedMethods.dateToUTCString(new Date()));

            db.update(TABLE_NAME, contentValues, PLOT_ID + " = ?", new String[]{id});
        } catch (SQLException e) {
            Log.e("error", e.toString());
        } finally {
            db.close();
        }
    }


    /**
     * Fetch a plot given a cursor
     *
     * @param cursor: query's cursor
     * @return plot data object
     */
    private PlotData getPlot(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        try {
            String ID = cursor.getString(cursor.getColumnIndex(PLOT_ID));
            String name = cursor.getString(cursor.getColumnIndex(PLOT_NAME));
            String acronym = cursor.getString(cursor.getColumnIndex(PLOT_ACRONYM));
            String limits = cursor.getString(cursor.getColumnIndex(PLOT_POLYGON));
            double lat = cursor.getDouble(cursor.getColumnIndex(PLOT_CENTER_LAT));
            double ln = cursor.getDouble(cursor.getColumnIndex(PLOT_CENTER_LN));
            String info = cursor.getString(cursor.getColumnIndex(PLOT_INFO));

            LocationData center = null;
            if (lat != 0.0 || ln != 0.0) {
                center = new LocationData(lat, ln);
            }

            ArrayList<LocationData> limits_list = new ArrayList<LocationData>();
            if (limits != null) {
                JSONArray jArray = new JSONArray(limits);

                for (int i = 0; i < jArray.length(); i++) {
                    JSONArray point = jArray.getJSONArray(i);
                    limits_list.add(new LocationData(point.getDouble(1), point.getDouble(0))); // alterar
                }
            }


            return new PlotData(ID, acronym, name, center, limits_list, info);
        } catch (Exception e) {
            Log.e("error", e.toString());
            return null;
        }
    }

    /**
     * Fetch the information of a plot given it's identifier
     *
     * @param id: plot's identifier
     * @return plot object data
     */
    public PlotData getPlotById(String id) {
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + PLOT_ID + " =?", new String[]{id});
        try {
            res.moveToFirst();

            return getPlot(res);

        } catch (SQLException e) {
            Log.e("error", e.toString());
            return null;
        } finally {
            res.close();
            db.close();
        }
    }

    /**
     * Fetch all plots in the table that are not tagged as deleted, ordered by name
     *
     * @return list of all plots
     */
    public List<PlotData> getPlots() {
        List<PlotData> array_list = new ArrayList<PlotData>();
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + PLOT_DELETE_TIME + " is null" + " ORDER BY " + PLOT_NAME, null);
        try {
            res.moveToFirst();

            while (!res.isAfterLast()) {
                array_list.add(getPlot(res));
                res.moveToNext();
            }

            return array_list;
        } catch (SQLException e) {
            Log.e("error", e.toString());
            return null;
        } finally {
            res.close();
            db.close();
        }
    }

    /**
     * Fetch the identifiers of all existing plots that are not tagged as deleted
     *
     * @return list of identifiers
     */
    public List<String> getPlotsIds() {
        List<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT " + PLOT_ID + " FROM " + TABLE_NAME + " WHERE " + PLOT_DELETE_TIME + " is null" + " ORDER BY " + PLOT_NAME, null);
        try {
            res.moveToFirst();

            while (!res.isAfterLast()) {
                array_list.add(res.getString(0));
                res.moveToNext();
            }

            return array_list;
        } catch (SQLException e) {
            Log.e("error", e.toString());
            return null;
        } finally {
            res.close();
            db.close();
        }
    }

    /**
     * Fetch all plots in the table ordered by name
     *
     * @return list of all plots
     */
    public List<PlotData> getPlotsForSelection() {
        List<PlotData> array_list = new ArrayList<PlotData>();
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + PLOT_NAME, null);
        try {
            res.moveToFirst();

            while (!res.isAfterLast()) {
                array_list.add(getPlot(res));
                res.moveToNext();
            }

            return array_list;
        } catch (SQLException e) {
            Log.e("error", e.toString());
            return null;
        } finally {
            res.close();
            db.close();
        }
    }


    /**
     * Creates a new plot
     *
     * @param plotData: plot object data
     * @param polygon:  GeoJSON string containing plot's limits
     * @return true if created with success, false otherwise
     */
    public boolean addPlot(PlotData plotData, String polygon) {
        SQLiteDatabase db = this.db.getWritableDatabase();
        try {
            return addPlot(plotData, polygon, db);
        } catch (Exception e) {
            Log.e("error", e.toString());
            return false;
        } finally {
            db.close();
        }
    }


    /**
     * Creates a new plot
     *
     * @param plotData: plot object data
     * @param polygon:  GeoJSON string containing plot's limits
     * @param db:       SQLite database
     * @return true if created with success, false otherwise
     */
    private boolean addPlot(PlotData plotData, String polygon, SQLiteDatabase db) {
        if (plotData == null || db == null)
            return false;

        try {
            ContentValues cv = new ContentValues();
            cv.put(PLOT_ID, Long.parseLong(plotData.getID()));
            cv.put(PLOT_NAME, plotData.getName());
            cv.put(PLOT_ACRONYM, plotData.getAcronym());
            cv.put(PLOT_POLYGON, polygon);

            cv.put(PLOT_CENTER_LAT, plotData.getCenter().getLat());
            cv.put(PLOT_CENTER_LN, plotData.getCenter().getLng());

            cv.put(PLOT_INFO, plotData.getInfo());


            long result = db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);

            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            Log.e("error", e.toString());
            return false;
        }
    }


    /**
     * Fetch the edition timestamp of a given plot
     *
     * @param plot_id: plot's identifier
     * @return edition timestamp
     */
    public String getPlotEditTime(String plot_id) {
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT + " + PLOT_EDIT_TIME + " FROM " + TABLE_NAME + " WHERE " + PLOT_ID + " = ?", new String[]{plot_id});

        try {
            if (res != null) {
                res.moveToFirst();
                return res.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (res != null) {
                res.close();
            }
            db.close();

        }
        return null;
    }
}
