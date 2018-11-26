package com.vesrand;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FragmentDays extends Fragment implements View.OnClickListener{
    private GridView gridView;
    private Button buttonOK;
    private Button buttonSetDefault;
    private CalendarView calendarView;
    private ImageButton mondayButton, tuesdayButton, wednesdayButton, thursdayButton, fridayButton, saturdayButton, sundayButton;
    DaysOnClickListener daysOnClickListener;
    private DaysAdapter daysAdapter;
    private ArrayList<String> predefinedDays;
    private AlarmClass alarmItem;

    public interface DaysOnClickListener{
        void OkButtonClicked(AlarmClass alarmClass); //ArrayMap<String, Object> mMap = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_days, container, false);

        predefinedDays = new ArrayList<String>();
        Bundle args = getArguments();
        if (args != null){
            alarmItem = args.getParcelable(FragmentSetAlarm.BUNDLE_KEY_ALARM);
            predefinedDays.addAll(alarmItem.mDays);
        }

        daysOnClickListener = (DaysOnClickListener) getActivity();

        gridView =(GridView) rootView.findViewById(R.id.gridView_days_listing);
        buttonOK = (Button) rootView.findViewById(R.id.button_days_submit);
        buttonSetDefault = (Button) rootView.findViewById(R.id.button_days_set_default);
        calendarView = (CalendarView) rootView.findViewById(R.id.calendarView);
        mondayButton = (ImageButton) rootView.findViewById(R.id.imageButton_monday);
        tuesdayButton = (ImageButton) rootView.findViewById(R.id.imageButton_tuesday);
        wednesdayButton = (ImageButton) rootView.findViewById(R.id.imageButton_wednesday);
        thursdayButton = (ImageButton) rootView.findViewById(R.id.imageButton_thursday);
        fridayButton = (ImageButton) rootView.findViewById(R.id.imageButton_friday);
        saturdayButton = (ImageButton) rootView.findViewById(R.id.imageButton_saturday);
        sundayButton = (ImageButton) rootView.findViewById(R.id.imageButton_sunday);

        gridView.setOnItemClickListener(gridViewOnItemClickListener);
        buttonOK.setOnClickListener(this);
        buttonSetDefault.setOnClickListener(this);
        mondayButton.setOnClickListener(this);
        tuesdayButton.setOnClickListener(this);
        wednesdayButton.setOnClickListener(this);
        thursdayButton.setOnClickListener(this);
        fridayButton.setOnClickListener(this);
        saturdayButton.setOnClickListener(this);
        sundayButton.setOnClickListener(this);

        daysAdapter = new DaysAdapter(getContext(), android.R.layout.simple_list_item_1, predefinedDays);
        gridView.setAdapter(daysAdapter);

        calendarView.setVisibility(View.GONE); //TODO: до лучших времен

        return rootView;
    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("","");
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_days_submit:
                alarmItem.mDays.clear();
                alarmItem.mDays.addAll(daysAdapter.days);
                daysOnClickListener.OkButtonClicked(alarmItem);
                break;
            case R.id.button_days_set_default:
//                predefinedDays.clear();  так тоже можно
                daysAdapter.days.clear();
                daysAdapter.notifyDataSetChanged();
                break;
            case R.id.imageButton_monday:
                dayHandle("Пн");
                break;
            case R.id.imageButton_tuesday:
                dayHandle("Вт");
                break;
            case R.id.imageButton_wednesday:
                dayHandle("Ср");
                break;
            case R.id.imageButton_thursday:
                dayHandle("Чт");
                break;
            case R.id.imageButton_friday:
                dayHandle("Пт");
                break;
            case R.id.imageButton_saturday:
                dayHandle("Сб");
                break;
            case R.id.imageButton_sunday:
                dayHandle("Вс");
                break;
        }
    }

    private void dayHandle(String day){
//                predefinedDays.add("Пн");
        if (daysAdapter.days.contains(day)){
            daysAdapter.days.remove(day);
        }else {
            daysAdapter.days.add(day);
            daysAdapter.sortAndClear();
        }
        daysAdapter.notifyDataSetChanged();

    }

    private GridView.OnItemClickListener gridViewOnItemClickListener = new GridView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                predefinedDays.remove(position);
                daysAdapter.notifyDataSetChanged();
            } catch (NullPointerException e) {
                Toast.makeText(getContext(),"nothing", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
