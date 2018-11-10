package com.vesrand;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    AlarmClass receivedAlarmItem;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra(MainActivity.INTENT_EXTRA_ALARM_ITEM);
        receivedAlarmItem = bundle.getParcelable(MainActivity.INTENT_EXTRA_ALARM_ITEM);
        //TODO: Handler
        //https://stackoverflow.com/questions/4111905/how-do-you-have-the-code-pause-for-a-couple-of-seconds-in-android
        //http://developer.alexanderklimov.ru/android/theory/handler.php
        Intent intentAlarmActivity = new Intent();
        intentAlarmActivity.setClassName("com.vesrand", "com.vesrand.AlarmActivity");
        intentAlarmActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentAlarmActivity.putExtra(MainActivity.INTENT_EXTRA_ALARM_ITEM, bundle);
        Log.d("alarmActivity", "receiver is about to start activity at: "); //TODO: remove me
        context.startActivity(intentAlarmActivity);
    }


}
