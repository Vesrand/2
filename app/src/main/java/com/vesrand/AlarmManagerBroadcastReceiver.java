package com.vesrand;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.Calendar;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    AlarmClass receivedAlarmItem;
    Bundle bundle;
    private Handler handler = new Handler();
    private long delay;

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){}
        Log.d("alarmActivity", "receiver launched!"); //TODO: remove me
        try {
            bundle = intent.getBundleExtra(MainActivity.INTENT_EXTRA_ALARM_ITEM);
            Log.d("alarmActivity", "bundle is ok"); //TODO: remove me
        } catch (Exception e) {
            Log.d("alarmActivity", "bundle is null"); //TODO: remove me
        }
        Log.d("alarmActivity", "going further 1"); //TODO: remove me
        if (bundle != null){
            Log.d("alarmActivity", "bundle != null; trying to get parcelable"); //TODO: remove me
            receivedAlarmItem = bundle.getParcelable(MainActivity.INTENT_EXTRA_ALARM_ITEM);
            Log.d("alarmActivity", "bundle != null; is empty: " + bundle.isEmpty()); //TODO: remove me
        }else {
            Log.d("alarmActivity", "bundle is null; trying to read DB"); //TODO: remove me
            //TODO: receivedAlarmItem = getDbInfo();
//            bundle.putParcelable(MainActivity.INTENT_EXTRA_ALARM_ITEM, receivedAlarmItem);
            Log.d("alarmActivity", "bundle = null"); //TODO: remove me
        }
        Log.d("alarmActivity", "going further 2"); //TODO: remove me
        if (receivedAlarmItem != null){
            Log.d("alarmActivity", "receivedAlarmItem != null; id: " + receivedAlarmItem.mID); //TODO: remove me
        }else {
            Log.d("alarmActivity", "receivedAlarmItem = null"); //TODO: remove me
        }
        Log.d("alarmActivity", "going further 3"); //TODO: remove me
        //TODO: проверяем дни недели, если пусто или сегодня совпадает с одним из дней в days, то запускаем
        final Intent intentAlarmActivity = new Intent();
        intentAlarmActivity.setClassName("com.vesrand", "com.vesrand.AlarmActivity");
        intentAlarmActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentAlarmActivity.putExtra(MainActivity.INTENT_EXTRA_ALARM_ITEM, bundle);

        Log.d("alarmActivity", "receiver starts activity!"); //TODO: remove me
        context.startActivity(intentAlarmActivity);

//        Calendar calendar = Calendar.getInstance();
////        calendar.add(Calendar.MINUTE, 1);
//        calendar.roll(Calendar.MINUTE, 1);
//        calendar.set(Calendar.SECOND, 0);
//        delay = calendar.getTimeInMillis() - System.currentTimeMillis();
//        Log.d("alarmActivity", "delay: " + delay); //TODO: remove me
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.d("alarmActivity", "receiver starts activity!"); //TODO: remove me
//                context.startActivity(intentAlarmActivity);
//            }
//        }, delay); //TODO: 1-перезапуск после выключения, 2-дни недели и прочие настройки(см закладки), 3-все действия(удаление, изменение, выключение)
    }


}
