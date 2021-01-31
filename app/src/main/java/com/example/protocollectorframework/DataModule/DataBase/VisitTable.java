package com.example.protocollectorframework.DataModule.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.protocollectorframework.Complements.SharedMethods;
import com.example.protocollectorframework.DataModule.Data.VisitData;
import com.example.protocollectorframework.DataModule.Data.VisitWithPlotData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Database table that stores the information associated to visits
 */
public class VisitTable {

    public static final String TABLE_NAME = "Visit_table";
    public static final String VISIT_ID = "_id";
    public static final String VISIT_PLOT = "visit_plot_id";
    public static final String VISIT_START_TIME = "visit_start_time";
    public static final String VISIT_END_TIME = "visit_end_time";
    public static final String VISIT_CREATION_TIME = "visit_creation_time";
    public static final String VISIT_EDIT_TIME = "visit_edit_time";
    public static final String VISIT_DELETE_TIME = "visit_delete_time";
    public static final String VISIT_SYNC = "visit_sync";
    public static final String VISIT_INFO_JSON = "visit_info";
    public static final String VISIT_EOI_JSON = "visit_EOI";
    public static final String VISIT_VERSION = "visit_version";

    private Context context;

    private DataBase db;

    /**
     * Constructor
     * @param context: current context
     */
    public VisitTable(Context context){
        db = new DataBase(context);
        this.context = context;
    }

    /**
     * Creates the table
     * @param sqLiteDatabase: SQLite database
     */
    protected static void createTable(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + VISIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                VISIT_PLOT + " INTEGER, " +
                VISIT_START_TIME + " TEXT, " +
                VISIT_END_TIME + " TEXT, " +
                VISIT_CREATION_TIME + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                VISIT_EDIT_TIME + " TEXT, " +
                VISIT_DELETE_TIME + " TEXT, " +
                VISIT_SYNC + " INTEGER DEFAULT 0," +
                VISIT_EOI_JSON + " TEXT, " +
                VISIT_INFO_JSON + " TEXT," +
                VISIT_VERSION + " INTEGER DEFAULT 1," +
                "FOREIGN KEY(" + VISIT_PLOT + ") " + "REFERENCES " + PlotTable.TABLE_NAME + "(" + PlotTable.PLOT_ID + "))";


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
     * Fetch the creation timestamp from a visit
     * @param visit_id: visit's identifier
     * @return visit's creation timestamp
     */
    public String getVisitCreationTime(String visit_id){
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT + " + VISIT_CREATION_TIME + " FROM " + TABLE_NAME + " WHERE " + VISIT_ID + " = ?", new String[]{visit_id});

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
     * Adds a new visit to the table
     * @param plot_id: plot's identifier where the visits takes place
     * @return visit's identifier
     */
    public String addVisit(String plot_id){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return addVisit(plot_id, db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return null;
        }finally {
            db.close();
        }
    }

    /**
     * Adds a new visit to the table
     * @param plot_id: plot's identifier where the visits takes place
     * @param db: SQLite database
     * @return visit's identifier
     */
    private String addVisit(String plot_id, SQLiteDatabase db){
        try {
            ContentValues cv = new ContentValues();
            cv.put(VISIT_PLOT, plot_id);
            cv.put(VISIT_START_TIME, System.currentTimeMillis());
            long id = db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            return Long.toString(id);
        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }
    }

    /**
     * Cancels a visit deleting it from the table
     * @param visit_id: visit's identifier
     * @return true if deleted with success, false otherwise
     */
    public boolean cancelVisit(String visit_id) {
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return db.delete(TABLE_NAME, VISIT_ID +" = ?",new String[]{visit_id}) > 0;
        }catch (SQLException e){
            Log.e("error", e.toString());
            return false;
        }
    }

    /**
     * Changes the visit sync status to "uploaded"
     * @param visit_id: visit's identifier
     * @return true if changed with success, false otherwise
     */
    public long uploadVisit(String visit_id){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return updateVisitFlag(visit_id, 1, db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return -1;
        }finally {
            db.close();
        }
    }

    /**
     * Changes the visit sync status to "error on upload"
     * @param visit_id: visit's identifier
     * @return true if changed with success, false otherwise
     */
    public boolean errorOnUpload(String visit_id){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return updateVisitFlag(visit_id,-1, db) > 0;
        }catch(SQLException e){
            Log.e("error", e.toString());
            return false;
        }finally {
            db.close();
        }
    }

    /**
     * Updates the sync flag of a given visit
     * @param visit_id: visit's identifier
     * @param sync_flag: sync flag
     * @param db: SQLite database
     * @return number of rows affected (bigger than zero if success)
     */
    private long updateVisitFlag(String visit_id, int sync_flag, SQLiteDatabase db){
        try {
            ContentValues cv = new ContentValues();
            cv.put(VISIT_SYNC, sync_flag);
            return db.update(TABLE_NAME, cv, VISIT_ID + " = ?", new String[]{visit_id});

        }catch(Exception e){
            Log.e("error", e.toString());
            return -1;
        }
    }

    /**
     * Concludes an ongoing visit
     * @param visit_id: visit's identifier
     * @param eoi_json: JSON string with the information from the EOIs
     * @param info_json: JSON string with extra information
     * @return true if updated with success, false otherwise
     */
    public boolean finishVisit(String visit_id, String eoi_json, String info_json){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return finishVisit(visit_id,eoi_json,info_json, db) > 0;
        }catch(SQLException e){
            Log.e("error", e.toString());
            return false;
        }finally {
            db.close();
        }
    }

    /**
     * Edits a visit
     * @param visit_id: visit's identifier
     * @param eoi_json: JSON string with the information from the EOIs
     * @param info_json: JSON string with extra information
     * @return true if edited with success, false otherwise
     */
    public boolean editVisit(String visit_id, String eoi_json, String info_json){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return edit_visit(visit_id, eoi_json,info_json, db) > 0;
        }catch(SQLException e){
            Log.e("error", e.toString());
            return false;
        }finally {
            db.close();
        }
    }

    /**
     * Creates a visit with all it's information
     * @param gps_path: list of paths to the gps files
     * @param start_time: visit start time in milliseconds
     * @param end_time: visit ending time in milliseconds
     * @param eoi_json: JSON string with the information from the EOIs
     * @param info_json: JSON string with extra information
     * @param plot_id: plot's identifier
     * @param version: visit's version
     * @return visit's identifier
     */
    public String downloadVisit(List<String> gps_path, long start_time, long end_time, String eoi_json, String info_json, long plot_id, int version){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return downloadVisit(gps_path, start_time, end_time,eoi_json, info_json,plot_id,version,db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return null;
        }finally {
            db.close();
        }
    }

    /**
     * Creates a visit with all it's information
     * @param gps_path: list of paths to the gps files
     * @param start_time: visit start time in milliseconds
     * @param end_time: visit ending time in milliseconds
     * @param eoi_json: JSON string with the information from the EOIs
     * @param info_json: JSON string with extra information
     * @param plot_id: plot's identifier
     * @param version: visit's version
     * @param db: SQLite database
     * @return visit's identifier
     */
    private String downloadVisit(List<String> gps_path, long start_time, long end_time, String eoi_json, String info_json, long plot_id, int version, SQLiteDatabase db){
        try {
            ContentValues cv = new ContentValues();
            cv.put(VISIT_PLOT, plot_id);
            cv.put(VISIT_START_TIME, start_time);
            cv.put(VISIT_END_TIME,end_time);
            cv.put(VISIT_EOI_JSON,eoi_json);
            cv.put(VISIT_INFO_JSON,info_json);
            cv.put(VISIT_VERSION,version);
            cv.put(VISIT_SYNC,1);

            long id = db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            if(id != -1) {
                TrajectoryTable trajectoryTable = new TrajectoryTable(context);
                for(String path : gps_path){
                    try{
                        String[] aux = path.split("_");
                        String owner = aux[aux.length-3];
                        if(owner.equals("track"))
                            owner = SharedMethods.getMyId(context);
                        trajectoryTable.addTrajectory(Long.toString(id),path,owner);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            return Long.toString(id);
        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }
    }

    /**
     * Fetch the version of a given visit
     * @param visit_id: visit's identifier
     * @return visit's version
     */
    public int getVisitVersion(String visit_id){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return getVersion(visit_id,db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return 0;
        }finally {
            db.close();
        }
    }

    /**
     * Fetch the version of a given visit
     * @param visit_id: visit's identifier
     * @param db: SQLite database
     * @return visit's version
     */
    private int getVersion(String visit_id, SQLiteDatabase db) {
        try {

            Cursor res = db.rawQuery("SELECT + " + VISIT_VERSION + " FROM " + TABLE_NAME + " WHERE " + VISIT_ID + " = ?", new String[]{visit_id});

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
     * Edits a visit
     * @param visit_id: visit's identifier
     * @param eoi_json: JSON string with the information from the EOIs
     * @param info_json: JSON string with extra information
     * @param db: SQLite database
     * @return number of rows affected (bigger than zero if success)
     */
    private long edit_visit(String visit_id, String eoi_json, String info_json, SQLiteDatabase db){
        try {
            ContentValues cv = new ContentValues();
            cv.put(VISIT_END_TIME, Long.toString(System.currentTimeMillis()));
            cv.put(VISIT_EOI_JSON,eoi_json);
            cv.put(VISIT_INFO_JSON,info_json);
            cv.put(VISIT_EDIT_TIME, SharedMethods.dateToUTCString(new Date()));

            int oldVersion = getVersion(visit_id,db);
            if(oldVersion > 0) {
                int newVersion = oldVersion + 1;
                cv.put(VISIT_VERSION, newVersion);
            }

            cv.put(VISIT_SYNC,2);

            return db.update(TABLE_NAME, cv, VISIT_ID + " = ?", new String[]{visit_id});

        }catch(Exception e){
            Log.e("error", e.toString());
            return -1;
        }
    }


    /**
     * Concludes an ongoing visit
     * @param visit_id: visit's identifier
     * @param eoi_json: JSON string with the information from the EOIs
     * @param info_json: JSON string with extra information
     * @param db: SQLite database
     * @return number of rows affected (bigger than zero if success)
     */
    private long finishVisit(String visit_id, String eoi_json, String info_json, SQLiteDatabase db){
        try {
            ContentValues cv = new ContentValues();
            cv.put(VISIT_END_TIME, Long.toString(System.currentTimeMillis()));
            cv.put(VISIT_EOI_JSON,eoi_json);
            cv.put(VISIT_INFO_JSON,info_json);

            return db.update(TABLE_NAME, cv, VISIT_ID + " = ?", new String[]{visit_id});

        }catch(Exception e){
            Log.e("error", e.toString());
            return -1;
        }
    }

    /**
     * Fetch the list of visits that took place in the given plot
     * @param plot_id: plot's identifier
     * @param visit_id: visit's identifier (used to ignore one visit)
     * @return list of visits that took place in the given plot except the one passed as argument
     */
    public List<VisitData> getVisitsOnPlot(String plot_id, String visit_id){
        List<VisitData> array_list = new ArrayList<VisitData>();
        SQLiteDatabase db = this.db.getReadableDatabase();

        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE " + VISIT_PLOT + "= ? AND " + VISIT_END_TIME + " is not null AND " + VISIT_DELETE_TIME + " is null AND " + VISIT_ID + " != ?"   +  " ORDER BY " + VISIT_CREATION_TIME + " DESC", new String[]{plot_id,visit_id} );
        try {
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                array_list.add(getVisit(res));
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
     * Fetch the sync status from a given visit
     * @param visit_id: visit's identifier
     * @return sync status
     */
    public int getSyncStatus(String visit_id){
        int status = Integer.MIN_VALUE;
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT "+ VISIT_SYNC +" FROM " + TABLE_NAME + " WHERE "  + VISIT_ID + " = ? ", new String[]{visit_id} );
        try{
            res.moveToFirst();

            status = res.getInt(res.getColumnIndex(VISIT_SYNC));

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            res.close();
            db.close();
        }
        return status;
    }

    /**
     * Fetch all the visit that were concluded
     * @return list of concluded visit data objects
     */
    public List<VisitData> getVisits() {
        List<VisitData> array_list = new ArrayList<VisitData>();
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE "  + VISIT_END_TIME + " is not null AND " + VISIT_DELETE_TIME + " is null ORDER BY " + VISIT_START_TIME + " DESC", null );
        try {
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                array_list.add(getVisit(res));
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
     * Fetch the information of a visit given a cursor
     * @param cursor: query's cursor
     * @return visit data object
     */
    private VisitData getVisit(Cursor cursor){
        if(cursor == null || cursor.getCount() == 0) {
            return null;
        }

        try {
            String ID = cursor.getString(cursor.getColumnIndex(VISIT_ID));
            String plot = cursor.getString(cursor.getColumnIndex(VISIT_PLOT));
            Long start = cursor.getLong(cursor.getColumnIndex(VISIT_START_TIME));
            Long end = cursor.getLong(cursor.getColumnIndex(VISIT_END_TIME));
            String eoi_json = cursor.getString(cursor.getColumnIndex(VISIT_EOI_JSON));
            String info_json = cursor.getString(cursor.getColumnIndex(VISIT_INFO_JSON));

            TrajectoryTable trajectoryTable = new TrajectoryTable(context);
            HashMap<String, String> routes = trajectoryTable.getTrajectories(ID);

            return new VisitData(ID,plot,routes,start,end,eoi_json,info_json);
        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }
    }

    /**
     * Fetch the list of not uploaded visits (sync status != 1)
     * @return list of not uploaded visits
     */
    public List<VisitData> getNotUploadedVisits() {
        List<VisitData> array_list = new ArrayList<VisitData>();
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE "  + VISIT_END_TIME + " is not null AND " + VISIT_DELETE_TIME + " is null AND " + VISIT_SYNC + " != 1", null );
        try {
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                array_list.add(getVisit(res));
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
     * Fetch the plot identifier where the given visit took place
     * @param visit_id: visit's identifier
     * @return plot's identifier
     */
    public String getPlotIdForVisit(String visit_id){
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT " +  VISIT_PLOT+ " FROM " + TABLE_NAME + " WHERE " + VISIT_DELETE_TIME + " is null AND " + VISIT_ID + " = ?", new String[]{visit_id} );
        res.moveToFirst();
        if(res.getCount() != 0)
            return res.getString(res.getColumnIndex(VISIT_PLOT));
        else return null;

    }

    /**
     * Fetch the information of a given visit that is concluded
     * @param visit_id: visit's identifier
     * @return concluded visit data object
     */
    public VisitData getVisitByID(String visit_id){
        List<VisitData> array_list = new ArrayList<VisitData>();
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE " + VISIT_DELETE_TIME + " is null AND " + VISIT_ID + " = ?", new String[]{visit_id} );
        try {
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                array_list.add(getVisit(res));
                res.moveToNext();
            }

            return array_list.get(0);
        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }finally {
            res.close();
            db.close();
        }
    }

    /**
     * Fetch the information of a given visit that is concluded
     * @param visit_id: visit's identifier
     * @return concluded visit data object
     */
    public VisitData getFinishedVisitByID(String visit_id){
        List<VisitData> array_list = new ArrayList<VisitData>();
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE " + VISIT_DELETE_TIME + " is null AND "  + VISIT_END_TIME  +" is not null AND " + VISIT_ID + " = ?", new String[]{visit_id} );
        try {
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                array_list.add(getVisit(res));
                res.moveToNext();
            }

            return array_list.get(0);
        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }finally {
            res.close();
            db.close();
        }
    }


    /**
     * Returns a cursor resulting from the query that fetch the combined information of visits and plots
     * @param plot_name: plot's name
     * @param start: visit start time as Calendar
     * @param end: visit ending time as Calendar
     * @param sync_flag: desired sync flag
     * @return cursor resulting from the query that fetch the combined information of visits and plots
     */
    public Cursor getVisitsWithPlotCursor(String plot_name, Calendar start, Calendar end, int sync_flag){
        List<VisitWithPlotData> array_list = new ArrayList<>();
        SQLiteDatabase db = this.db.getReadableDatabase();
        String select = TABLE_NAME +"."+ VISIT_ID + "," + TABLE_NAME +"."+ VISIT_START_TIME + "," + TABLE_NAME +"."+ VISIT_END_TIME + "," + TABLE_NAME +"."+ VISIT_PLOT + "," + TABLE_NAME +"."+ VISIT_SYNC + ","+ PlotTable.TABLE_NAME + "." + PlotTable.PLOT_NAME;
        try {
            String where_plot = "";
            String where_date = "";
            String where_state = "";
            String[] args = null;
            if(plot_name != null) {
                args = new String[]{plot_name};
                where_plot = " AND " + PlotTable.TABLE_NAME + "." + PlotTable.PLOT_NAME + " = ?";
            }
            if(start != null){
                if(end != null){
                    end.add(Calendar.DATE,1);
                    where_date = " AND " + VISIT_START_TIME + " BETWEEN " + start.getTime().getTime() + " AND " + end.getTime().getTime();

                }else{
                    Calendar nextDay = Calendar.getInstance();
                    nextDay.setTime(start.getTime());
                    nextDay.add(Calendar.DATE,1);
                    where_date = " AND " + VISIT_START_TIME + " BETWEEN " + start.getTime().getTime() + " AND " + nextDay.getTime().getTime();
                }
            }
            if(sync_flag == 0 || sync_flag == 1 || sync_flag == -1 || sync_flag == 2){
                where_state = " AND " +  VISIT_SYNC + " = " + sync_flag;
            }

            return db.rawQuery("SELECT " + select + " FROM " + TABLE_NAME +
                    " INNER JOIN " + PlotTable.TABLE_NAME + " ON " + VISIT_PLOT + " = " + PlotTable.TABLE_NAME + "." + PlotTable.PLOT_ID +
                    " WHERE " + VISIT_END_TIME + " is not null AND " + VISIT_DELETE_TIME + " is null" + where_plot + where_date + where_state + " ORDER BY " + VISIT_START_TIME + " DESC", args);
        }catch (Exception e){
            return null;
        }



    }

    /**
     * Check if there is any visit on a given date and plot
     * @param date: date string in the format yyyy.mm.dd.hh.mm
     * @param acronym: plot's acronym
     * @return true if there is at least one visit on the given date and plot, false otherwise
     */
    public boolean hasVisitOnDateAndPlot(String date, String acronym){
        long timeInMilliseconds = -1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm");
        try {
            Date mDate = sdf.parse(date);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        SQLiteDatabase db = this.db.getReadableDatabase();
        String select = TABLE_NAME +"."+ VISIT_ID + "," + TABLE_NAME +"."+ VISIT_START_TIME + "," + TABLE_NAME +"."+ VISIT_END_TIME + "," + TABLE_NAME +"."+ VISIT_PLOT + "," + PlotTable.TABLE_NAME + "." + PlotTable.PLOT_ACRONYM;
        Cursor res  = db.rawQuery("SELECT " + select + " FROM " + TABLE_NAME +
                " INNER JOIN " + PlotTable.TABLE_NAME + " ON " + VISIT_PLOT + " = " + PlotTable.TABLE_NAME + "." + PlotTable.PLOT_ID +
                " WHERE " + VISIT_END_TIME + " is not null AND " + VISIT_DELETE_TIME + " is null AND " + PlotTable.TABLE_NAME + "." + PlotTable.PLOT_ACRONYM + " = ? AND " + VISIT_START_TIME + " BETWEEN " + timeInMilliseconds + " AND " + (timeInMilliseconds + 60000), new String[]{acronym});

        return res.getCount() != 0;
    }

}
