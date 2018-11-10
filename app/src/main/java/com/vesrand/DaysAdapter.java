package com.vesrand;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DaysAdapter extends ArrayAdapter {

public ArrayList<String> days;
private Context mContext;

    public DaysAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> predefinedDays) {
        super(context, resource, predefinedDays);
        mContext = context;
        this.days = predefinedDays;
    }

    public DaysAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        mContext = context;
        days = new ArrayList<String>();
    }

    public void sortAndClear(){
        Set<String> daysSet = new HashSet<String>(days);
        days.clear();
        days.addAll(daysSet);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            days.sort(new DaysComparator());
        }else {
            String[] daysArray = days.toArray(new String[days.size()]);
            Arrays.sort(daysArray, new DaysComparator());
            days.clear();
            days.addAll(Arrays.asList(daysArray));
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) convertView;

        if (convertView == null) {
            convertView = new TextView(mContext);
            label = (TextView) convertView;
        }
        label.setText((CharSequence) days.get(position));
        label.setTextSize(20);
        label.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return convertView;
    }

}
