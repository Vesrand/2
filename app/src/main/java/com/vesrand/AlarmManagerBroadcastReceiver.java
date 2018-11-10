package com.vesrand;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    AlarmClass receivedAlarmItem;

    @Override
    public void onReceive(Context context, Intent intent) {
        receivedAlarmItem = intent.getParcelableExtra(MainActivity.INTENT_EXTRA_ALARM_ITEM);
        //TODO: handle extra
        //TODO: Handler
        //https://stackoverflow.com/questions/4111905/how-do-you-have-the-code-pause-for-a-couple-of-seconds-in-android
        //http://developer.alexanderklimov.ru/android/theory/handler.php
        Intent intentAlarmActivity = new Intent();
        intentAlarmActivity.setClassName("com.vesrand", "com.vesrand.AlarmActivity");
        intentAlarmActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Log.d("alarmActivity", "receiver is about to start activity at: " + receivedAlarmItem.mTime);
//        intentAlarmActivity.putExtra(MainActivity.INTENT_EXTRA_ALARM_ITEM, receivedAlarmItem);
        Log.d("alarmActivity", "receiver is about to start activity at: ");
        context.startActivity(intentAlarmActivity);
    }


}
