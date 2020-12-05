package com.example.protocollectorframework.DataModule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.protocollectorframework.Extra.SharedMethods;
import com.example.protocollectorframework.DataModule.Data.VisitData;
import com.example.protocollectorframework.DataModule.Data.VisitWithPlotData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class VisitTable {

    public static final String TABLE_NAME = "Visit_table";
    public static final String VISIT_ID = "_id";
    public static final String VISIT_PLOT = "Visit_plot_id";
    public static final String VISIT_ROUTE = "Visit_route";
    public static final String VISIT_START_TIME = "Visit_start_time";
    public static final String VISIT_END_TIME = "Visit_end_time";
    public static final String VISIT_CREATION_TIME = "Visit_creation_time";
    public static final String VISIT_EDIT_TIME = "Visit_edit_time";
    public static final String VISIT_DELETE_TIME = "Visit_delete_time";
    public static final String VISIT_SYNC = "Visit_sync";

    public static final String VISIT_TRAPS_JSON = "Visit_traps";
    public static final String VISIT_INFO_JSON = "Visit_info";
    public static final String VISIT_EOI_JSON = "Visit_EOI";

    public static final String VISIT_VERSION = "Visit_version";

    private Context context;

    private DataBase db;

    public VisitTable(Context context){
        db = new DataBase(context);
        this.context = context;
    }

    protected static void createTable(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + VISIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                VISIT_PLOT + " INTEGER, " +
                //VISIT_ROUTE + " TEXT, " +
                VISIT_START_TIME + " TEXT, " +
                VISIT_END_TIME + " TEXT, " +
                VISIT_CREATION_TIME + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                VISIT_EDIT_TIME + " TEXT, " +
                VISIT_DELETE_TIME + " TEXT, " +
                VISIT_SYNC + " INTEGER DEFAULT 0," +
                VISIT_EOI_JSON + " TEXT, " +
                VISIT_TRAPS_JSON + " TEXT," +
                VISIT_INFO_JSON + " TEXT," +
                VISIT_VERSION + " INTEGER DEFAULT 1," +
                "FOREIGN KEY(" + VISIT_PLOT + ") " + "REFERENCES " + PlotTable.TABLE_NAME + "(" + PlotTable.PLOT_ID + "))";


        sqLiteDatabase.execSQL(createTable);
    }

    protected static void dropTable(SQLiteDatabase sqLiteDatabase) {
        String drop = "DROP TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(drop + TABLE_NAME);
    }


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

    public boolean cancelVisit(String visit_id) {
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return db.delete(TABLE_NAME, VISIT_ID +" = ?",new String[]{visit_id}) > 0;
        }catch (SQLException e){
            Log.e("error", e.toString());
            return false;
        }
    }


    public long uploadVisit(String visit_id){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return updateVisitFlag(visit_id, db,1);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return -1;
        }finally {
            db.close();
        }
    }

    public long errorOnUpload(String visit_id){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return updateVisitFlag(visit_id, db,-1);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return -1;
        }finally {
            db.close();
        }
    }

    private long updateVisitFlag(String visit_id, SQLiteDatabase db, int sync_flag){
        try {
            ContentValues cv = new ContentValues();
            cv.put(VISIT_SYNC, sync_flag);
            return db.update(TABLE_NAME, cv, VISIT_ID + " = ?", new String[]{visit_id});

        }catch(Exception e){
            Log.e("error", e.toString());
            return -1;
        }
    }

    public long finishVisit(String visit_id, String eoi_json, String trap_json, String info_json){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return finishVisit(visit_id,eoi_json, trap_json,info_json, db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return -1;
        }finally {
            db.close();
        }
    }


    public long editVisit(String visit_id, String eoi_json, String trap_json, String info_json){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return edit_visit(visit_id, eoi_json, trap_json,info_json, db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return -1;
        }finally {
            db.close();
        }
    }

    public String downloadVisit(List<String> gps_path, String start_time, String end_time, String eoi_json, String trap_json, String info_json, long plot_id, int version){
        SQLiteDatabase db = this.db.getWritableDatabase();
        try{
            return downloadVisit(gps_path, start_time, end_time,eoi_json,trap_json,info_json,plot_id,version,db);
        }catch(SQLException e){
            Log.e("error", e.toString());
            return null;
        }finally {
            db.close();
        }
    }

    private String downloadVisit(List<String> gps_path, String start_time, String end_time, String eoi_json, String trap_json, String info_json, long plot_id, int version, SQLiteDatabase db){
        try {
            ContentValues cv = new ContentValues();
 //           cv.put(VISIT_ROUTE,gps_path);
            cv.put(VISIT_PLOT, plot_id);
            cv.put(VISIT_START_TIME, start_time);
            cv.put(VISIT_END_TIME,end_time);
            cv.put(VISIT_EOI_JSON,eoi_json);
            cv.put(VISIT_TRAPS_JSON,trap_json);
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



    private long edit_visit(String visit_id, String eoi_json, String trap_json, String info_json, SQLiteDatabase db){
        try {
            ContentValues cv = new ContentValues();
            cv.put(VISIT_END_TIME, Long.toString(System.currentTimeMillis()));
            cv.put(VISIT_EOI_JSON,eoi_json);
            cv.put(VISIT_TRAPS_JSON,trap_json);
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


    private long finishVisit(String visit_id, String eoi_json, String trap_json, String info_json, SQLiteDatabase db){
        try {
            ContentValues cv = new ContentValues();
            cv.put(VISIT_END_TIME, Long.toString(System.currentTimeMillis()));
            cv.put(VISIT_EOI_JSON,eoi_json);
            cv.put(VISIT_TRAPS_JSON,trap_json);
            cv.put(VISIT_INFO_JSON,info_json);

            return db.update(TABLE_NAME, cv, VISIT_ID + " = ?", new String[]{visit_id});

        }catch(Exception e){
            Log.e("error", e.toString());
            return -1;
        }
    }

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


    public int getSyncStatus(String id){
        int status = Integer.MIN_VALUE;
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT "+ VISIT_SYNC +" FROM " + TABLE_NAME + " WHERE "  + VISIT_ID + " = ? ", new String[]{id} );
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
    private VisitData getVisit(Cursor cursor){
        if(cursor == null || cursor.getCount() == 0) {
            return null;
        }

        try {
            String ID = cursor.getString(cursor.getColumnIndex(VISIT_ID));
          //  String route = cursor.getString(cursor.getColumnIndex(VISIT_ROUTE));
            //TODO
            String plot = cursor.getString(cursor.getColumnIndex(VISIT_PLOT));
            Long start = cursor.getLong(cursor.getColumnIndex(VISIT_START_TIME));
            Long end = cursor.getLong(cursor.getColumnIndex(VISIT_END_TIME));
            String eoi_json = cursor.getString(cursor.getColumnIndex(VISIT_EOI_JSON));
            String trap_json = cursor.getString(cursor.getColumnIndex(VISIT_TRAPS_JSON));
            String info_json = cursor.getString(cursor.getColumnIndex(VISIT_INFO_JSON));

            TrajectoryTable trajectoryTable = new TrajectoryTable(context);
            HashMap<String, String> routes = trajectoryTable.getTrajectories(ID);

            return new VisitData(ID,plot,routes,start,end,eoi_json,trap_json,info_json);
        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }
    }


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

    public String getPlotIdForVisit(String visit_id){
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT " +  VISIT_PLOT+ " FROM " + TABLE_NAME + " WHERE " + VISIT_DELETE_TIME + " is null AND " + VISIT_ID + " = ?", new String[]{visit_id} );
        res.moveToFirst();
        if(res.getCount() != 0)
            return res.getString(res.getColumnIndex(VISIT_PLOT));
        else return null;

    }


    public VisitData getVisitByID(String id){
        List<VisitData> array_list = new ArrayList<VisitData>();
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE " + VISIT_DELETE_TIME + " is null AND " + VISIT_ID + " = ?", new String[]{id} );
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


    public VisitData getFinishedVisitByID(String id){
        List<VisitData> array_list = new ArrayList<VisitData>();
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE " + VISIT_DELETE_TIME + " is null AND "  + VISIT_END_TIME  +" is not null AND " + VISIT_ID + " = ?", new String[]{id} );
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

//    public VisitData getOnGoingVisitByID(String id){
//        List<VisitData> array_list = new ArrayList<VisitData>();
//        SQLiteDatabase db = this.db.getReadableDatabase();
//        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME + " WHERE "  + VISIT_END_TIME + " is null AND " + VISIT_DELETE_TIME + " is null AND " + VISIT_ID + " = ?", new String[]{id} );
//        try {
//            res.moveToFirst();
//
//            while (res.isAfterLast() == false) {
//                array_list.add(getVisit(res));
//                res.moveToNext();
//            }
//
//            return array_list.get(0);
//        }catch(Exception e){
//            Log.e("error", e.toString());
//            return null;
//        }finally {
//            res.close();
//            db.close();
//        }
//    }


    private VisitWithPlotData getVisitAndPlot(Cursor cursor){
        if(cursor == null || cursor.getCount() == 0) {
            return null;
        }

        try {
            String ID = cursor.getString(cursor.getColumnIndex(VISIT_ID));
            long start = cursor.getLong(cursor.getColumnIndex(VISIT_START_TIME));
            long end = cursor.getLong(cursor.getColumnIndex(VISIT_END_TIME));

            String plot = cursor.getString(cursor.getColumnIndex(VISIT_PLOT));
            String name = cursor.getString(cursor.getColumnIndex(PlotTable.PLOT_NAME));


            return new VisitWithPlotData(ID,start,end,plot,name);
        }catch(Exception e){
            Log.e("error", e.toString());
            return null;
        }
    }

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

    public List<VisitWithPlotData> getVisitsWithPlot() {
        List<VisitWithPlotData> array_list = new ArrayList<>();
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res = getVisitsWithPlotCursor(null,null,null, Integer.MIN_VALUE);
        try {
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                array_list.add(getVisitAndPlot(res));
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

    public boolean isVisitSynced(String id){
        SQLiteDatabase db = this.db.getReadableDatabase();
        Cursor res  = db.rawQuery("SELECT *  FROM " + TABLE_NAME +
                " WHERE " + VISIT_ID + " = ? AND "+ VISIT_SYNC + " = 1", new String[]{id});

        return res.getCount() != 0;
    }

    //date = yyyy.mm.dd.hh.mm
    public boolean hasVisitOnDateAndPlot(String date, String acronym){
        List<VisitWithPlotData> array_list = new ArrayList<>();

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


    public List<VisitWithPlotData> getVisitsWithPlot(String plot_id) {
        List<VisitWithPlotData> array_list = new ArrayList<>();
        SQLiteDatabase db = this.db.getReadableDatabase();
        String select = TABLE_NAME +"."+ VISIT_ID + "," + TABLE_NAME +"."+ VISIT_START_TIME + "," + TABLE_NAME +"."+ VISIT_END_TIME + "," + TABLE_NAME +"."+ VISIT_PLOT + "," + PlotTable.TABLE_NAME + "." + PlotTable.PLOT_NAME;
        Cursor res  = db.rawQuery("SELECT " + select + " FROM " + TABLE_NAME +
                " INNER JOIN " + PlotTable.TABLE_NAME + " ON " + VISIT_PLOT + " = " + PlotTable.TABLE_NAME + "." + PlotTable.PLOT_ID +
                " WHERE " + VISIT_END_TIME + " is not null AND " + VISIT_DELETE_TIME + " is null AND "+ PlotTable.TABLE_NAME + ". " + PlotTable.PLOT_ID + " = ?" + " ORDER BY " + VISIT_CREATION_TIME + " DESC", new String[]{plot_id});

        try {
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                array_list.add(getVisitAndPlot(res));
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
