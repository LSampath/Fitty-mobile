package com.example.fitty.controllers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fitty.adapters.DatabaseHelper;
import com.example.fitty.models.SleepHours;

import java.util.ArrayList;

public class SleepController {

    public static long insertHours(DatabaseHelper dbHelper, double hours) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        // generate today
        String today = SleepHours.getToday(0);

        values.put("sleep_date", today);
        values.put("hours", hours);

        long result = db.insert(DatabaseHelper.SLEEP_TABLE, null, values);
        return  result;
    }

    public static long insertHoursCustom(DatabaseHelper dbHelper, double hours, int date) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        String today = SleepHours.getToday(date);

        values.put("sleep_date", today);
        values.put("hours", hours);

        long result = db.insert(DatabaseHelper.SLEEP_TABLE, null, values);
        return  result;
    }

    public static int deleteHours(DatabaseHelper dbHelper, int sid) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.SLEEP_TABLE, "sid=?", new String[]{String.valueOf(sid)});
    }

    public static ArrayList<SleepHours> getHours(DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select sleep_date, hours from " + DatabaseHelper.SLEEP_TABLE, null);

        ArrayList<SleepHours> hourList = new ArrayList<>();
        while (res.moveToNext()) {
            hourList.add(new SleepHours(res.getString(0), res.getDouble(1)));
        }
        return hourList;
    }
}
