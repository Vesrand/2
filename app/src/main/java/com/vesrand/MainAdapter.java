package com.vesrand;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends ArrayAdapter {
    private Context mContext;
    private List<AlarmClass> mObjects;
    boolean isReadyForClicks;
    private boolean mChecked;
    MainListViewAdapterButtonsListener mainListViewAdapterButtonsListener;
    public interface MainListViewAdapterButtonsListener{
        void removeDbItem(int id);
        void enableDbItem(int id, boolean isEnabled);
        void mainClickLauncher(AlarmClass alarmItem, int position);
    }

    public MainAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        mContext = context;
        mainListViewAdapterButtonsListener = (MainListViewAdapterButtonsListener) context;
        mObjects = new ArrayList<AlarmClass>();
    }

    public MainAdapter(@NonNull Context context, int resource, @NonNull List<AlarmClass> objects) {
        super(context, resource, objects);
        mContext = context;
        mainListViewAdapterButtonsListener = (MainListViewAdapterButtonsListener) context;
        mObjects = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.imageButtonDelete = (ImageButton) convertView.findViewById(R.id.imageButton_delete);
            holder.textViewAlarmTime = (TextView) convertView.findViewById(R.id.textView_alarm_time);
            holder.gridViewDays = (GridView) convertView.findViewById(R.id.gridView_days);
            holder.switchOnOff = (Switch) convertView.findViewById(R.id.switch_on_off);
            convertView.setTag(holder);
        }
        final AlarmClass alarmItem = mObjects.get(position);
        if (alarmItem != null) {
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.textViewAlarmTime.setText(alarmItem.mTime);
            DaysAdapter daysAdapter = new DaysAdapter(mContext, android.R.layout.simple_list_item_1, alarmItem.mDays);
            holder.gridViewDays.setAdapter(daysAdapter);
            if (mObjects.size() == 7) {
                holder.gridViewDays.setNumColumns(7);
            }else{
                holder.gridViewDays.setNumColumns(6);
            }
            holder.imageButtonDelete.setTag(position);
            holder.gridViewDays.setTag(position);
            holder.textViewAlarmTime.setTag(position);
            holder.switchOnOff.setTag(position);
//            holder.switchOnOff.setOnCheckedChangeListener(null);
            isReadyForClicks = false;
            holder.switchOnOff.setChecked(alarmItem.mEnabled);
            isReadyForClicks = true;

            holder.imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos =(Integer)v.getTag();
                    mainListViewAdapterButtonsListener.removeDbItem(mObjects.get(pos).mID);
                    mObjects.remove(pos);
                    notifyDataSetChanged();
                }
            });

            holder.switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isReadyForClicks) {
                        int pos = (Integer) buttonView.getTag();
                        int id = mObjects.get(pos).mID;
                        mainListViewAdapterButtonsListener.enableDbItem(id, isChecked);
                        mObjects.get(pos).mEnabled = isChecked;
//                    notifyDataSetChanged(); //возможно эта херня запускает метод много раз проходя по всему списку??? Вообще для switch оно и не нужно
                    }
                }
            });
//            holder.switchOnOff.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == MotionEvent.ACTION_UP){// || event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
//                        CompoundButton view = (CompoundButton) v;
//                        int pos =(Integer)v.getTag();
//                        mChecked = view.isChecked();
//                        mainListViewAdapterButtonsListener.enableDbItem(pos, mChecked);
//                        mObjects.get(pos).mEnabled = mChecked;
//                    }
//                    return true;
//                }
//            });
            holder.textViewAlarmTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (Integer)v.getTag();
                    mainListViewAdapterButtonsListener.mainClickLauncher(alarmItem, pos);
                }
            });
            holder.gridViewDays.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int pos = (Integer)v.getTag();
                    mainListViewAdapterButtonsListener.mainClickLauncher(alarmItem, pos);
                    return true;
                }
            });
        }

        return convertView;
    }

    static class ViewHolder{
        ImageButton imageButtonDelete;
        TextView textViewAlarmTime;
        GridView gridViewDays;
        Switch switchOnOff;
    }
}
