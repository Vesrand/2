package com.vesrand;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.vesrand.data.DbAlarmContract;
import com.vesrand.data.DbAlarmHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    AlarmClass receivedAlarmItem;
    Bundle bundle;
//    private Handler handler = new Handler();
//    private long delay;
    private ArrayList<AlarmClass> alarmList;
    private DbAlarmHelper mDbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        bundle = intent.getBundleExtra(MainActivity.INTENT_EXTRA_ALARM_ITEM);
        Calendar calendar = Calendar.getInstance();
        if (bundle != null){
            Log.d("alarmActivity", "bundle != null; trying to get parcelable and launch activity"); //TODO: remove me
            receivedAlarmItem = bundle.getParcelable(MainActivity.INTENT_EXTRA_ALARM_ITEM);
            //проверяем дни недели, если пусто или сегодня совпадает с одним из дней в days, то запускаем
            if (receivedAlarmItem != null) {
                if ((receivedAlarmItem.mDays.size() == 0 || receivedAlarmItem.mDays.contains(DaysComparator.intToDay(calendar.get(Calendar.DAY_OF_WEEK)))) && receivedAlarmItem.mEnabled){
                    Intent intentAlarmActivity = new Intent();
                    intentAlarmActivity.setClassName("com.vesrand", "com.vesrand.AlarmActivity");
                    intentAlarmActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentAlarmActivity.putExtra(MainActivity.INTENT_EXTRA_ALARM_ITEM, bundle);
                    Log.d("alarmActivity", "receiver starts activity!"); //TODO: remove me
                    context.startActivity(intentAlarmActivity);
                } else {
                    Log.d("alarmActivity", "wrong day or disabled"); //TODO: remove me
                }
            }else {
                Log.d("alarmActivity", "bundle != null but alarm item is null, trying to identify event"); //TODO: remove me
                //TODO: тут обработка эвента с поздравлением
            }

            //повторить будильник
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingBroadcastIntent;
            String[] time = receivedAlarmItem.mTime.split(":");
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
            calendar.set(Calendar.SECOND, 0);
            if(calendar.getTimeInMillis() < (System.currentTimeMillis())) {
                calendar.roll(Calendar.DAY_OF_YEAR, 1);
            }

            Intent alarmIntent = new Intent(context, AlarmManagerBroadcastReceiver.class);
            alarmIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Bundle tmpBundle = new Bundle();
            tmpBundle.putParcelable(MainActivity.INTENT_EXTRA_ALARM_ITEM, receivedAlarmItem);
            alarmIntent.putExtra(MainActivity.INTENT_EXTRA_ALARM_ITEM, tmpBundle);
            //будем запускать каждый день и проверять в ресивере день
            pendingBroadcastIntent = PendingIntent.getBroadcast(context, receivedAlarmItem.mID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            try {
                alarmManager.cancel(pendingBroadcastIntent); //все отменяем и создаем заново, чтоб не было дубликатов
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("alarmActivity", "pending intent is null, cant cancel it /n" + e.toString());//TODO: remove me
            }
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingBroadcastIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingBroadcastIntent);
            }else if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingBroadcastIntent); //список алармов - гуглить adb shell
            }else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingBroadcastIntent);
            }
        }else {
            Log.d("alarmActivity", "bundle is null; trying to read DB and recreate all alarms"); //TODO: remove me
            alarmList = new ArrayList<AlarmClass>();
            mDbHelper = new DbAlarmHelper(context);
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String[] projectionAlarm = {DbAlarmContract.AlarmEntry._ID,
                    DbAlarmContract.AlarmEntry.COLUMN_CHECKED,
                    DbAlarmContract.AlarmEntry.COLUMN_TIME,
                    DbAlarmContract.AlarmEntry.COLUMN_MUSIC,
                    DbAlarmContract.AlarmEntry.COLUMN_MUSIC_SOURCE,
                    DbAlarmContract.AlarmEntry.COLUMN_MOTIVATION};
            String[] projectionDays = {DbAlarmContract.AlarmDaysEntry._ID,
                    DbAlarmContract.AlarmDaysEntry.COLUMN_ALARM_ID,
                    DbAlarmContract.AlarmDaysEntry.COLUMN_DAYS};
            Cursor cursorAlarm;
            Cursor cursorDays;
            cursorAlarm = db.query(DbAlarmContract.AlarmEntry.TABLE_NAME, projectionAlarm, null, null, null, null, null);
            cursorDays = db.query(DbAlarmContract.AlarmDaysEntry.TABLE_NAME, projectionDays, null, null, null, null, null);

            try{
                int idColumnIndex = cursorAlarm.getColumnIndex(DbAlarmContract.AlarmEntry._ID);
                int checkedColumnIndex = cursorAlarm.getColumnIndex(DbAlarmContract.AlarmEntry.COLUMN_CHECKED);
                int timeColumnIndex = cursorAlarm.getColumnIndex(DbAlarmContract.AlarmEntry.COLUMN_TIME);
                int musicColumnIndex = cursorAlarm.getColumnIndex(DbAlarmContract.AlarmEntry.COLUMN_MUSIC);
                int musicSourceColumnIndex = cursorAlarm.getColumnIndex(DbAlarmContract.AlarmEntry.COLUMN_MUSIC_SOURCE);
                int motivationColumnIndex = cursorAlarm.getColumnIndex(DbAlarmContract.AlarmEntry.COLUMN_MOTIVATION);

                int alarmIdColumnIndex = cursorDays.getColumnIndex(DbAlarmContract.AlarmDaysEntry.COLUMN_ALARM_ID);
                int daysColumnIndex = cursorDays.getColumnIndex(DbAlarmContract.AlarmDaysEntry.COLUMN_DAYS);

                while (cursorAlarm.moveToNext()){
                    AlarmClass alarmItem = new AlarmClass();
                    alarmItem.mID = cursorAlarm.getInt(idColumnIndex);
                    alarmItem.mEnabled = (cursorAlarm.getInt(checkedColumnIndex) != 0 );
                    alarmItem.mTime = cursorAlarm.getString(timeColumnIndex);
                    alarmItem.mMusic = cursorAlarm.getString(musicColumnIndex);
                    alarmItem.mMusicSource = cursorAlarm.getInt(musicSourceColumnIndex);
                    alarmItem.mMotivashka = (cursorAlarm.getInt(motivationColumnIndex) != 0);

                    alarmList.add(alarmItem);
                }

                int alarmId = -1;
                ArrayList<String> currentAlarmDays = new ArrayList<>();
                while (cursorDays.moveToNext()){
                    String currentDay = cursorDays.getString(daysColumnIndex);
                    int currentAlarmId = cursorDays.getInt(alarmIdColumnIndex);
                    if (alarmId != currentAlarmId && alarmId != -1){
                        for (int i = 0; i < alarmList.size(); i++){
                            if (alarmList.get(i).mID == alarmId) {
                                alarmList.get(i).mDays.addAll(currentAlarmDays);
                                break;
                            }
                        }
                        currentAlarmDays = new ArrayList<>();
                    }
                    currentAlarmDays.add(currentDay);
                    alarmId = currentAlarmId;
                }
                for (int i = 0; i < alarmList.size(); i++){
                    if (alarmList.get(i).mID == alarmId) {
                        alarmList.get(i).mDays.addAll(currentAlarmDays);
                        break;
                    }
                }

            } finally {
                cursorAlarm.close();
                cursorDays.close();
            }

            AlarmClass alarmItem;
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingBroadcastIntent;
            for (int i = 0; i < alarmList.size(); i++){
                alarmItem = alarmList.get(i);
                String[] time = alarmItem.mTime.split(":");
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                calendar.set(Calendar.SECOND, 0);
                if(calendar.getTimeInMillis() < (System.currentTimeMillis())) {
                    calendar.roll(Calendar.DAY_OF_YEAR, 1);
                }

                Intent alarmIntent = new Intent(context, AlarmManagerBroadcastReceiver.class);
                alarmIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Bundle tmpBundle = new Bundle();
                tmpBundle.putParcelable(MainActivity.INTENT_EXTRA_ALARM_ITEM, alarmItem);
                alarmIntent.putExtra(MainActivity.INTENT_EXTRA_ALARM_ITEM, tmpBundle);
                //будем запускать каждый день и проверять в ресивере день
                pendingBroadcastIntent = PendingIntent.getBroadcast(context, alarmItem.mID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                try {
                    alarmManager.cancel(pendingBroadcastIntent); //все отменяем и создаем заново, чтоб не было дубликатов
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("alarmActivity", "pending intent is null, cant cancel it /n" + e.toString());
                }
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingBroadcastIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingBroadcastIntent);
                }else if (Build.VERSION.SDK_INT >= 19) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingBroadcastIntent); //список алармов - гуглить adb shell
                }else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingBroadcastIntent);
                }
            }
        }


//        Calendar calendar = Calendar.getInstance();
////        calendar.add(Calendar.MINUTE, 1);
//        calendar.roll(Calendar.MINUTE, 1);
//        calendar.set(Calendar.SECOND, 0);
//        delay = calendar.getTimeInMillis() - System.currentTimeMillis();
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                context.startActivity(intentAlarmActivity);
//            }
//        }, delay);
    }


}
