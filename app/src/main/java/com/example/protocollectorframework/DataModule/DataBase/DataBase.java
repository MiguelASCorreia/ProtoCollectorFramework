package com.example.protocollectorframework.DataModule.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database class
 */

public class DataBase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ProtoCollectorDataBase";
    private static final int VERSION = 1;

    private Context context;

    /**
     * Constructor
     * @param context: current context
     */
    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    /**
     * Creates all the tables
     * @param sqLiteDatabase: SQLite database
     */
    private void createTables(SQLiteDatabase sqLiteDatabase) {
        PlotTable.createTable(sqLiteDatabase);
        VisitTable.createTable(sqLiteDatabase);
        MultimediaTable.createTable(sqLiteDatabase);
        TrajectoryTable.createTable(sqLiteDatabase);
        BluetoothSyncTable.createTable(sqLiteDatabase);
        ComplementaryTable.createTable(sqLiteDatabase);
        ConfigTable.createTable(sqLiteDatabase);
        TrajectorySegmentTable.createTable(sqLiteDatabase);
        TrajectoryPointTable.createTable(sqLiteDatabase);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = ON");
    }

}
