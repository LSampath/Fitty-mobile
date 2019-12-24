package com.example.fitty.Adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fitty.controllers.StepController;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "fitty_db";
    public static final String STEP_TABLE = "step";
    public static final String RUN_TABLE = "run";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + STEP_TABLE + "(" +
                        "sid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "step_date DATE NOT NULL, " +
                        "steps INT NOT NULL" +
                        ");"
        );

        sqLiteDatabase.execSQL(
                "CREATE TABLE " + RUN_TABLE + "(" +
                        "rid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "start_time BIGINT NOT NULL, " +
                        "end_time BIGINT NOT NULL, " +
                        "distance DECIMAL(8,3) NOT NULL" +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + STEP_TABLE);
        sqLiteDatabase.execSQL("drop table if exists " + RUN_TABLE);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }
}
