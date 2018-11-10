package com.vesrand;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.vesrand.data.DbAlarmContract;
import com.vesrand.data.DbAlarmHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.crypto.AEADBadTagException;

public class MainActivity extends AppCompatActivity implements FragmentMain.MainClickedInterface, FragmentSetAlarm.SetAlarmClickedInterface, FragmentDays.DaysOnClickListener, MainAdapter.MainListViewAdapterButtonsListener {

    public static final String INTENT_EXTRA_ALARM_ITEM = "alarmItem";
    private boolean mIsPortrait;
    FragmentManager fragmentManager;
    private DbAlarmHelper mDbHelper;
    public ArrayList<AlarmClass> alarmList;
    SQLiteDatabase dataBase;
    int currentId;
    Context context;
    private AlarmManagerBroadcastReceiver broadcastReceiver;
    private AlarmManager alarmManager;
    private Calendar calendar;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this.getApplicationContext();
        fragmentManager = getSupportFragmentManager();
        mDbHelper = new DbAlarmHelper(this);
        alarmList = new ArrayList<AlarmClass>();
        getDbIfo();                                                                 //читаем базу данных (и закрываем)
        dataBase = mDbHelper.getWritableDatabase();                                 //открываем для дальнейшей записи
        broadcastReceiver = new AlarmManagerBroadcastReceiver();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        calendar = Calendar.getInstance();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //узнать ориентацию экрана
        mIsPortrait = isPortrait();

        if (mIsPortrait && savedInstanceState == null) {
            getSupportActionBar().hide();//show();
            //подключаем сходу главный фрагмент
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            FragmentMain fragmentMain = new FragmentMain();
            Bundle args = new Bundle();
            args.putParcelableArrayList(FragmentSetAlarm.BUNDLE_KEY_ALARM, alarmList);
            fragmentMain.setArguments(args);
            fragmentTransaction.add(R.id.container_main, fragmentMain);
            fragmentTransaction.commit();
        } else {
            //TODO:тут будет для альбомной ориентации
            //так же сделать заполнение каждого фрагмента после проверки на нахождении его на экране
        }

//        //test
//        String s = new String();
//        Iterator iterator = alarmList.iterator();
//        AlarmClass currentItem;
//        while (iterator.hasNext()){
//            currentItem = (AlarmClass) iterator.next();
//            s = s + currentItem.mTime + " " + currentItem.mEnabled + "\n";
//        }
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setMessage(s);
//        builder.create().show();
    }

    //узнать ориентацию экрана
    private boolean isPortrait(){
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }else{
            return false;
        }
    }


    @Override
    public void mainClickListener(AlarmClass alarmItem, int pos) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentSetAlarm fragmentSetAlarm = new FragmentSetAlarm();

        Bundle args = new Bundle();
        currentId = pos;
        args.putParcelable(FragmentSetAlarm.BUNDLE_KEY_ALARM, alarmItem);
        fragmentSetAlarm.setArguments(args);

        fragmentTransaction.replace(R.id.container_main, fragmentSetAlarm);
        fragmentTransaction.commit();
        getSupportActionBar().hide();
    }

    @Override
    public void mainClickLauncher(AlarmClass alarmItem, int position) {
        mainClickListener(alarmItem, position);
    }

    @Override
    public void buttonClickedCancelSubmit(AlarmClass alarmItem) {
        if (alarmItem != null){
            if (alarmItem.mID == -1) {  //новый (изменить id на положительный)
                alarmItem.mID = alarmList.size() == 0 ? 0 : alarmList.get(alarmList.size()-1).mID + 1;    //вариант без сдвигания id, если сдвигать id, то можно просто alarmItem.mID = alarmList.size();
                alarmList.add(alarmItem);
                addDbItem(alarmItem);
            }else{ //изменить старую запись
                alarmList.remove(currentId);
                alarmList.add(currentId, alarmItem);
                changeDbItem(alarmItem);
            }
        } //else нажата кнопа cancel и ничего не делаем

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentMain fragmentMain = new FragmentMain();

        Bundle args = new Bundle();
        args.putParcelableArrayList(FragmentSetAlarm.BUNDLE_KEY_ALARM, alarmList);
        fragmentMain.setArguments(args);

        fragmentTransaction.replace(R.id.container_main, fragmentMain);
        fragmentTransaction.commit();
        getSupportActionBar().hide();//show();

    }

    @Override
    public void gridviewDaysClicked(AlarmClass alarmItem) {
        if (mIsPortrait) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            FragmentDays fragmentDays = new FragmentDays();

            Bundle args = new Bundle();
            args.putParcelable(FragmentSetAlarm.BUNDLE_KEY_ALARM, alarmItem);
            fragmentDays.setArguments(args);

            fragmentTransaction.replace(R.id.container_main, fragmentDays);
            fragmentTransaction.commit();

            getSupportActionBar().hide();
        } else {
            //тут будет для альбомной ориентации
        }
    }

    @Override
    public void OkButtonClicked(AlarmClass alarmItem) {
        if (mIsPortrait) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            FragmentSetAlarm fragmentSetAlarm = new FragmentSetAlarm();

            Bundle args = new Bundle();
            args.putParcelable(FragmentSetAlarm.BUNDLE_KEY_ALARM, alarmItem);
            fragmentSetAlarm.setArguments(args);

            fragmentTransaction.replace(R.id.container_main, fragmentSetAlarm);
            fragmentTransaction.commit();

            getSupportActionBar().hide();
        } else {
            //тут будет для альбомной ориентации
        }
    }



//методы работы с базой данных
//вынесены отдельно исключительно для удобства чтения кода
    private void getDbIfo(){
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

//            int idDaysColumnIndex = cursorDays.getColumnIndex(DbAlarmContract.AlarmDaysEntry._ID);
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
    }

    private void addDbItem(@NonNull AlarmClass alarmItem){
        ContentValues contentValuesAlarm = new ContentValues();
        contentValuesAlarm.put(DbAlarmContract.AlarmEntry._ID, alarmItem.mID);
        contentValuesAlarm.put(DbAlarmContract.AlarmEntry.COLUMN_CHECKED, alarmItem.mEnabled ? 1 : 0);
        contentValuesAlarm.put(DbAlarmContract.AlarmEntry.COLUMN_TIME, alarmItem.mTime);
        contentValuesAlarm.put(DbAlarmContract.AlarmEntry.COLUMN_MUSIC, alarmItem.mMusic);
        contentValuesAlarm.put(DbAlarmContract.AlarmEntry.COLUMN_MUSIC_SOURCE, alarmItem.mMusicSource);
        contentValuesAlarm.put(DbAlarmContract.AlarmEntry.COLUMN_MOTIVATION, alarmItem.mMotivashka ? 1 : 0);
        dataBase.insert(DbAlarmContract.AlarmEntry.TABLE_NAME, null, contentValuesAlarm);

        for (int i = 0; i<alarmItem.mDays.size(); i++) {
            ContentValues contentValuesDays = new ContentValues();
            contentValuesDays.put(DbAlarmContract.AlarmDaysEntry.COLUMN_ALARM_ID, alarmItem.mID);
            contentValuesDays.put(DbAlarmContract.AlarmDaysEntry.COLUMN_DAYS, alarmItem.mDays.get(i));
            dataBase.insert(DbAlarmContract.AlarmDaysEntry.TABLE_NAME, null, contentValuesDays);
        }

        String[] s = alarmItem.mTime.split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(s[1]));
        calendar.roll(Calendar.MINUTE, -1);

        Intent alarmIntent = new Intent(this.getApplicationContext(), AlarmManagerBroadcastReceiver.class);
//       TODO: alarmIntent.setAction();
        alarmIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Bundle bundle = new Bundle();
        bundle.putParcelable(INTENT_EXTRA_ALARM_ITEM, alarmItem);
        alarmIntent.putExtra(INTENT_EXTRA_ALARM_ITEM, bundle);
        PendingIntent pendingBroadcastIntent = PendingIntent.getBroadcast(this.getApplicationContext(), alarmItem.mID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        //TODO: надо добавить дни даже для единовременного будильника, иначе он воспримет меньшее значение времени как пропущенный и запустит сразу
        //TODO: попробовать переставить время на телефоне, попробовать на реальном телефоне
        //TODO: flags here см. урок 119 и форум там же, и developer/Intent

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingBroadcastIntent);
        }else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingBroadcastIntent); //список алармов - гуглить adb shell
        }else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingBroadcastIntent);
        }
        //TODO: далее нотификация
    }

    private void changeDbItem(@NonNull AlarmClass alarmItem){
        ContentValues contentValuesAlarm = new ContentValues();
        String[] whereArgs = new String[]{Integer.toString(alarmItem.mID)};
        contentValuesAlarm.put(DbAlarmContract.AlarmEntry.COLUMN_CHECKED, alarmItem.mEnabled ? 1 : 0);
        contentValuesAlarm.put(DbAlarmContract.AlarmEntry.COLUMN_TIME, alarmItem.mTime);
        contentValuesAlarm.put(DbAlarmContract.AlarmEntry.COLUMN_MUSIC, alarmItem.mMusic);
        contentValuesAlarm.put(DbAlarmContract.AlarmEntry.COLUMN_MUSIC_SOURCE, alarmItem.mMusicSource);
        contentValuesAlarm.put(DbAlarmContract.AlarmEntry.COLUMN_MOTIVATION, alarmItem.mMotivashka ? 1 : 0);
        dataBase.update(DbAlarmContract.AlarmEntry.TABLE_NAME, contentValuesAlarm, DbAlarmContract.AlarmEntry._ID + " = ?", whereArgs);

        dataBase.delete(DbAlarmContract.AlarmDaysEntry.TABLE_NAME, DbAlarmContract.AlarmDaysEntry.COLUMN_ALARM_ID + " = ?",whereArgs);
        for (int i = 0; i<alarmItem.mDays.size(); i++) {
            ContentValues contentValuesDays = new ContentValues();
            contentValuesDays.put(DbAlarmContract.AlarmDaysEntry.COLUMN_ALARM_ID, alarmItem.mID);
            contentValuesDays.put(DbAlarmContract.AlarmDaysEntry.COLUMN_DAYS, alarmItem.mDays.get(i));
            dataBase.insert(DbAlarmContract.AlarmDaysEntry.TABLE_NAME, null, contentValuesDays);
        }

    }

//методы интерфейса для адаптера ListView
    @Override
    public void removeDbItem(int id){
        String[] whereArgs = new String[]{Integer.toString(id)};
        dataBase.delete(DbAlarmContract.AlarmEntry.TABLE_NAME, DbAlarmContract.AlarmEntry._ID + " = ?", whereArgs);
        dataBase.delete(DbAlarmContract.AlarmDaysEntry.TABLE_NAME, DbAlarmContract.AlarmDaysEntry.COLUMN_ALARM_ID + " = ?",whereArgs);

    }

    @Override
    public void enableDbItem(int id, boolean isEnabled){
        String[] whereArgs = new String[]{Integer.toString(id)};
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbAlarmContract.AlarmEntry.COLUMN_CHECKED, isEnabled ? 1 : 0);
        dataBase.update(DbAlarmContract.AlarmEntry.TABLE_NAME, contentValues, DbAlarmContract.AlarmEntry._ID + " = ?", whereArgs);
        Toast.makeText(this, id + " " + isEnabled ,Toast.LENGTH_SHORT).show();

    }

}
