package com.vesrand.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class DbAlarmHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "alarms.db";
    private static final int DATABASE_VERSION = 1;

    public DbAlarmHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ALARMS_TABLE = "CREATE TABLE " + DbAlarmContract.AlarmEntry.TABLE_NAME + " ("
                + DbAlarmContract.AlarmEntry._ID + " INTEGER PRIMARY KEY, "
                + DbAlarmContract.AlarmEntry.COLUMN_CHECKED + " INTEGER NOT NULL, "
                + DbAlarmContract.AlarmEntry.COLUMN_TIME + " TEXT NOT NULL, "
                + DbAlarmContract.AlarmEntry.COLUMN_MUSIC + " TEXT, "
                + DbAlarmContract.AlarmEntry.COLUMN_MUSIC_SOURCE + " INTEGER NOT NULL, "
                + DbAlarmContract.AlarmEntry.COLUMN_MOTIVATION + " INTEGER NOT NULL);";

        String SQL_CREATE_DAYS_TABLE = "CREATE TABLE " + DbAlarmContract.AlarmDaysEntry.TABLE_NAME + " ("
                + DbAlarmContract.AlarmDaysEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DbAlarmContract.AlarmDaysEntry.COLUMN_ALARM_ID + " INTEGER NOT NULL, "
                + DbAlarmContract.AlarmDaysEntry.COLUMN_DAYS + " TEXT);";

        db.execSQL(SQL_CREATE_ALARMS_TABLE);
        db.execSQL(SQL_CREATE_DAYS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("DB Update LOG", "Update from version " + oldVersion + " to version " + newVersion);
        db.execSQL("DROP TABLE " + DbAlarmContract.AlarmDaysEntry.TABLE_NAME);
        db.execSQL("DROP TABLE " + DbAlarmContract.AlarmEntry.TABLE_NAME);
        onCreate(db);
    }
}
