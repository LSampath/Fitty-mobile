package com.example.fitty.controllers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fitty.adapters.DatabaseHelper;
import com.example.fitty.models.StepCount;

import java.util.ArrayList;


public class StepController {

    public static long insertCount(DatabaseHelper dbHelper, int steps) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        // generate yesterday
        String today = StepCount.getToday(0);

        values.put("step_date", today);
        values.put("steps", steps);

        long result = db.insert(DatabaseHelper.STEP_TABLE, null, values);
        return  result;
    }

    public static long insertCountCustom(DatabaseHelper dbHelper, int steps, int date) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        String today = StepCount.getToday(date);

        values.put("step_date", today);
        values.put("steps", steps);

        long result = db.insert(DatabaseHelper.STEP_TABLE, null, values);
        return  result;
    }

    public static int deleteCount(DatabaseHelper dbHelper, int sid) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.STEP_TABLE, "sid=?", new String[]{String.valueOf(sid)});
    }

    public static ArrayList<StepCount> getCounts(DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select step_date, steps from " + DatabaseHelper.STEP_TABLE, null);

        ArrayList<StepCount> countList = new ArrayList<>();
        while (res.moveToNext()) {
            countList.add(new StepCount(res.getString(0), res.getInt(1)));
        }
        return countList;
    }

}
