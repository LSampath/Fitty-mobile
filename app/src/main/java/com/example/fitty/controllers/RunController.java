package com.example.fitty.controllers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.fitty.adapters.DatabaseHelper;
import com.example.fitty.models.RunningSession;

import java.util.ArrayList;

public class RunController {

    public static long insertSession(DatabaseHelper dbHelper, RunningSession session) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("start_time", session.getStartTime());
        values.put("end_time", session.getEndTime());
        values.put("distance", session.getDistance());

        long result = db.insert(DatabaseHelper.RUN_TABLE, null, values);
        return  result;
    }

    public static int deleteSession(DatabaseHelper dbHelper, int rid) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.RUN_TABLE, "rid=?", new String[]{String.valueOf(rid)});
    }

    public static ArrayList<RunningSession> getSessions(DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select start_time, end_time, distance from " + DatabaseHelper.RUN_TABLE, null);

        ArrayList<RunningSession> sessionList = new ArrayList<>();
        while (res.moveToNext()) {
            sessionList.add(new RunningSession(res.getLong(0), res.getLong(1), res.getDouble(2)));
            Log.i("RUNNING", res.getLong(0)+ ", " + res.getLong(1) + ", " + res.getDouble(2));
        }
        return sessionList;
    }
}
