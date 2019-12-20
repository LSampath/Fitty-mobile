package com.example.fitty.controllers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "fitty_db";
//    public static final String USER_TABLE = "user";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        sqLiteDatabase.execSQL(
//                "CREATE TABLE " + USER_TABLE + "(" +
//                        "uid INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                        "name VARCHAR(50) NOT NULL, " +
//                        "gender VARCHAR(100) NOT NULL, " +
//                        "weight NUMERIC(6,3) NOT NULL," +
//                        "height NUMERIC(3,2) NOT NULL," +
//                        "check(gender IN ('Male', 'Female'))" +
//                        ");"
//        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//        sqLiteDatabase.execSQL("drop table if exists " + USER_TABLE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }
}
