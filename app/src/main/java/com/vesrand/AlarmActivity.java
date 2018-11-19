package com.vesrand;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

public class AlarmActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    TextView textView;
    AlarmClass receivedAlarmItem;
    MediaPlayer mMediaPlayer;
    AudioManager mAudioManager;
    private List<Integer> mMusicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        textView = (TextView) findViewById(R.id.textViewMotivashka);
        Bundle bundle = getIntent().getBundleExtra(MainActivity.INTENT_EXTRA_ALARM_ITEM);
        receivedAlarmItem = bundle.getParcelable(MainActivity.INTENT_EXTRA_ALARM_ITEM);
        textView.setText(String.format("%d %s %s", receivedAlarmItem.mID, receivedAlarmItem.mTime, receivedAlarmItem.mDays));
        getSupportActionBar().hide();
        //TODO: самый задний фон меняется в зависимости от месяца

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        this.setVolumeControlStream(AudioManager.STREAM_ALARM);
        try {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                releaseMP();
            } else {
                releaseMP();
                mMediaPlayer = MediaPlayer.create(this, R.raw.you_are_a_pirate); //getResources().getIdentifier("you are a pirate.mp3","raw", getPackageName()));
                //mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.start();
            }
        }
        catch (Exception e){
            releaseMP();
            mMediaPlayer = MediaPlayer.create(this, R.raw.you_are_a_pirate);
            // mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        mMediaPlayer.stop();
        super.onDestroy();
        releaseMP();
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

    @Override
    public void onCompletion(MediaPlayer mp) {
        releaseMP();
    }

    private void releaseMP(){
        if (mMediaPlayer != null){
            try {
                mMediaPlayer.release();
                mMediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
