package com.vesrand;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class AlarmActivity extends AppCompatActivity {
    TextView textView;
    AlarmClass receivedAlarmItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        textView = (TextView) findViewById(R.id.textViewMotivashka);
//        receivedAlarmItem = getIntent().getParcelableExtra(MainActivity.INTENT_EXTRA_ALARM_ITEM);
//        textView.setText(String.format("%d %s %s", receivedAlarmItem.mID, receivedAlarmItem.mTime, receivedAlarmItem.mDays));
        getSupportActionBar().hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private int getRandomItem(int forWhat){
        //TODO: forWhat - флаг (музыка, мотивашка или картинка)
        //взять следующее число из строки и удалить его, если взято последнее число, то сгенерировать новую, но так, чтобы первое != последнее
        //начальную строку я задам вручную в файле
        return forWhat;
    }

    public void onClick(View view) {
        finish();
    }
}
