package com.android.ag.blocklock;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 15.03.2016.
 */
public class DBHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "lockbase.db";
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_TABLE = "LockTable";
    public static final String _ID = "_id";
    public static final String PASS_ONE = "pass_one";
    public static final String PASS_TWO = "pass_two";

    private static final String DATABASE_CREATE_SCRIPT = "create table "
            + DATABASE_TABLE + " (" + _ID + " integer primary key autoincrement, "
            + PASS_ONE + " text not null, " + PASS_TWO + " text not null);";

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);

        onCreate(db);
    }
}
