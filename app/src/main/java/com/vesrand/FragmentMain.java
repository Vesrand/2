package com.vesrand;

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
import android.widget.ListView;

import java.util.ArrayList;

public class FragmentMain extends Fragment implements View.OnClickListener {

    private Button mButtonNewAlarm;
    ListView listViewAlarms;
    public ArrayList<AlarmClass> mAlarmList;
    private MainAdapter mainAdapter;

    MainClickedInterface mainClickedInterface;

    public interface MainClickedInterface {
        void mainClickListener (AlarmClass alarmItem, int position);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mButtonNewAlarm = (Button) rootView.findViewById(R.id.button_add_new_alarm);
        listViewAlarms = (ListView) rootView.findViewById(R.id.listView_alarm_item);

        Bundle args = getArguments();
        try {
            if (args.containsKey(FragmentSetAlarm.BUNDLE_KEY_ALARM)){
                mAlarmList = args.getParcelableArrayList(FragmentSetAlarm.BUNDLE_KEY_ALARM);
            }else{
                mAlarmList = new ArrayList<AlarmClass>();
            }
        } catch (NullPointerException e) {
            Log.d("myLOG", e.toString());
            mAlarmList = new ArrayList<AlarmClass>();
        }
        mainAdapter = new MainAdapter(getContext(), R.layout.list_item, mAlarmList);
        listViewAlarms.setAdapter(mainAdapter);

        //listeners
        mButtonNewAlarm.setOnClickListener(this);
        mainClickedInterface = (MainClickedInterface) getActivity();
        listViewAlarms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mainClickedInterface.mainClickListener(mAlarmList.get(position), position);
            }
        });

        return rootView;
    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("","");
//    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_add_new_alarm){
            mainClickedInterface.mainClickListener(null, -1);
        }
    }

}
