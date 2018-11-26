package com.vesrand;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.vesrand.data.DbAlarmContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentSetAlarm extends Fragment implements View.OnClickListener{

    private Button buttonTime, buttonCancel, buttonSubmit;
    private GridView gridViewDaysSetup;
    private Spinner spinnerMusic;
    private CheckBox checkBoxPicture;
    private DaysAdapter daysAdapter;
    private TextView labelMusic;

    SetAlarmClickedInterface setAlarmClickedInterface;
    public static final String BUNDLE_KEY_ALARM = "alarm";
    Calendar calendar;
    public AlarmClass alarmItem;
    private boolean skipSpinnerSelectedAction;
    private boolean isNewAlarm = false;

    public interface SetAlarmClickedInterface{
        void buttonClickedCancelSubmit(AlarmClass alarmClass);
        void gridviewDaysClicked(AlarmClass alarmClass);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setalarm, container, false);
        calendar = Calendar.getInstance();

        //id блок
        buttonTime = (Button) rootView.findViewById(R.id.button_time);
        buttonCancel = (Button) rootView.findViewById(R.id.button_setalarm_cancel);
        buttonSubmit = (Button) rootView.findViewById(R.id.button_setalarm_submit);
        gridViewDaysSetup= (GridView) rootView.findViewById(R.id.gridView_days_setup);
        spinnerMusic = (Spinner) rootView.findViewById(R.id.spinner_music);
        checkBoxPicture = (CheckBox) rootView.findViewById(R.id.checkBox_picture);
        labelMusic = (TextView) rootView.findViewById(R.id.editText_label_music);
        labelMusic.setVisibility(View.GONE); //TODO: до лучших времен
        spinnerMusic.setVisibility(View.GONE);

        List<String> listForSpinner = new ArrayList<String>();
        listForSpinner.add(MusicSpinnerAdapter.SPINNER_TITTLE_ITEM1_SUBITEM);
        listForSpinner.add(MusicSpinnerAdapter.SPINNER_TITTLE_ITEM2_SUBITEM);

        //bundle блок
        Bundle args = getArguments();
        try {
            if (args.containsKey(BUNDLE_KEY_ALARM)){
                alarmItem = args.getParcelable(BUNDLE_KEY_ALARM);
                if (alarmItem.mMusicSource == DbAlarmContract.AlarmEntry.MUSIC_SELECT){ //1
                    listForSpinner.remove(DbAlarmContract.AlarmEntry.MUSIC_SELECT); //1
                    listForSpinner.add(alarmItem.mMusic);
                }
                buttonTime.setText(alarmItem.mTime);
                checkBoxPicture.setChecked(alarmItem.mMotivashka);
                setGridViewDaysSetup();
            }else{
                alarmItem = new AlarmClass();
                checkBoxPicture.setChecked(alarmItem.mMotivashka);
                isNewAlarm = true;
            }
        } catch (NullPointerException e) {
            Log.d("myLOG", e.toString());
            alarmItem = new AlarmClass();
            isNewAlarm = true;
        }


//        if (alarmItem.mDays.size() == 0){
//            gridViewDaysSetup.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 40));
//        }else {
//            gridViewDaysSetup.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        }
        if (true) { //TODO: сюда пишем последнюю 3 строчку спиннера
            listForSpinner.add(MusicSpinnerAdapter.SPINNER_TITTLE_ITEM2_SUBITEM);
        } else {//мелодия по умолчанию
        }
        MusicSpinnerAdapter musicSpinnerAdapter = new MusicSpinnerAdapter(getActivity(), R.layout.spinner_item, listForSpinner);
        spinnerMusic.setAdapter(musicSpinnerAdapter);
        skipSpinnerSelectedAction = true;
        spinnerMusic.setSelection(alarmItem.mMusicSource);


        //listener блок
        buttonTime.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        buttonSubmit.setOnClickListener(this);
        spinnerMusic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                alarmItem.mMusicSource = position;
                if (!skipSpinnerSelectedAction) {
                    //TODO: тут будет диалог
                }
                skipSpinnerSelectedAction = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        gridViewDaysSetup.setClickable(false);
        gridViewDaysSetup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    onClickDays();
                }
                return true;
            }
        });

        setAlarmClickedInterface = (SetAlarmClickedInterface) getActivity();

        return rootView;
    }

    //метод для вызова из активности. Для передачи установленных дней из других фрагментов и их установки в GridView
    public void setGridViewDaysSetup (){
        daysAdapter = new DaysAdapter(getActivity(), android.R.layout.simple_list_item_1, alarmItem.mDays);
        gridViewDaysSetup.setAdapter(daysAdapter);
        daysAdapter.notifyDataSetChanged();
    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("","");
//    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_time:
                onClickSetTime();
                break;
            case R.id.button_setalarm_cancel:
                onClickCancel();
                break;
            case R.id.button_setalarm_submit:
                onClickSubmit();
                break;
            case R.id.spinner_music:
                onClickSetMusic();
                break;
            case R.id.checkBox_picture:
                onClickCheckMotivation();
                break;
        }
    }
    private void onClickSetTime(){
        String s = (String) buttonTime.getText();
        String[] sArr = s.split(":");
        if (isNewAlarm) {
            isNewAlarm = false;
            new TimePickerDialog(getActivity(), onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }else{
            new TimePickerDialog(getActivity(), onTimeSetListener, Integer.parseInt(sArr[0]), Integer.parseInt(sArr[1]), true).show();
        }
    }
    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            alarmItem.mTime = String.format("%02d:%02d", hourOfDay, minute);
            buttonTime.setText(alarmItem.mTime);
        }
    };
    private void onClickCancel(){
        setAlarmClickedInterface.buttonClickedCancelSubmit(null);
    }
    private void onClickSubmit(){
        setAlarmClickedInterface.buttonClickedCancelSubmit(alarmItem);
    }
    private void onClickDays(){
        setAlarmClickedInterface.gridviewDaysClicked(alarmItem);
    }
    private void onClickSetMusic(){
//TODO: тут выбор, проверить что alarmItem меняется
    }
    private void onClickCheckMotivation(){

    }
}
