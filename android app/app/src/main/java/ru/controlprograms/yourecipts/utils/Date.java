package ru.controlprograms.yourecipts.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by evgeny on 28.08.16.
 */

public class Date {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static SimpleDateFormat timeStampDataBaseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private Calendar mCalendar;

    public static String getCurrentDate() {
        java.util.Date now = new java.util.Date();
        return  dateFormat.format(now);
    }

    public static String getCurrentTimeStamp() {
        java.util.Date now = new java.util.Date();
        return  dateTimeFormat.format(now);
    }

    public Date(String string) {
        mCalendar = Calendar.getInstance();
        try {
            mCalendar.setTime(dateTimeFormat.parse(string));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date(long time) {
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeZone(TimeZone.getDefault());
        mCalendar.setTimeInMillis(time);
    }

    public String getDate() {
        return dateFormat.format(mCalendar.getTime());
    }

    public String getTime() {
        return timeFormat.format(mCalendar.getTime());
    }

    public int getDayOfMonth() {
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }
    public int getMonth() {
        return mCalendar.get(Calendar.MONTH);
    }
    public int getYear() {
        return mCalendar.get(Calendar.YEAR);
    }

    public int getHour() {
        return mCalendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinutes() {
        return mCalendar.get(Calendar.MINUTE);
    }

    public void setTime(int hours, int minutes) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hours);
        mCalendar.set(Calendar.MINUTE, minutes);
    }

    public void setDate(int dayOfMonth, int monthOfYear, int year) {
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.YEAR, year);
    }

    public String getTimeStampDataBase() {
        return timeStampDataBaseFormat.format(mCalendar.getTime());
    }
    public static String getTimeStampFromDataBase(String dataBaseString) {
        Calendar calendar =  Calendar.getInstance();
        try {
            calendar.setTime(timeStampDataBaseFormat.parse(dataBaseString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTimeFormat.format(calendar.getTime());
    }


    public static String dataBaseToNormal(String timeStamp) {
        Calendar calendar =  Calendar.getInstance();
        try {
            calendar.setTime(timeStampDataBaseFormat.parse(timeStamp));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTimeFormat.format(calendar.getTime());
    }
}
