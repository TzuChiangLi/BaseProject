package com.ftrend.zgp.utils.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    //数据库名称
    private static final String DATABASE_NAME = "TEST";
    //数据库版本号
    private static final int DATABASE_VERSION = 0;
    //暂时的测试表
    public static final String CREATE_APPPARAMS = "create table AppParams("
            + "ID integer primary key autoincrement not null,"
            + "ParamName varchar(20) not null,"
            + "ParamValue varchar(100) not null)";

    public DBHelper(Context context) {
        this(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_APPPARAMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 1) {
            DataBaseUpdate.update_0_1(db);
        }
        if (oldVersion < 2) {
            DataBaseUpdate.update_1_2(db);
        }
        if (oldVersion < 3) {
            DataBaseUpdate.update_2_3(db);
        }
        if (oldVersion < 4) {
            DataBaseUpdate.update_3_4(db);
        }
        if (oldVersion < 5) {
            DataBaseUpdate.update_4_5(db);
        }
        if (oldVersion < 6) {
            DataBaseUpdate.update_5_6(db);
        }
    }
}
