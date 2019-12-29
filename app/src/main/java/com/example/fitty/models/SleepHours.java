package com.example.fitty.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SleepHours {


    private Date date;
    private double hours;

    public SleepHours(String date, double hours) {
        this.date = stringToDate(date);
        this.hours = hours;
    }

    public static Date stringToDate(String strDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date stepDate = new Date(0);
        try {
            stepDate = format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return stepDate;
    }

    public static String dateToString(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String strDate = dateFormat.format(date);
        return strDate;
    }

    public static String getToday() {
        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, -1);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = dateFormat.format(calendar.getTime());
        return date;
    }

    public Date getDate() {
        return date;
    }

    public String getDateString(String format) {
        return dateToString(this.date, format);
    }

    public double getHours() {
        return hours;
    }
}
