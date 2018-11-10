package com.vesrand;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MusicSpinnerAdapter extends ArrayAdapter {
    private LayoutInflater layoutInflater;
    private List<String> mObjects;
    public static final String SPINNER_TITTLE_ITEM1 = "Случайная";
    public static final String SPINNER_TITTLE_ITEM1_SUBITEM = "Из папки ringtones";
    public static final String SPINNER_TITTLE_ITEM2 = "Выбрать мелодию";
    public static final String SPINNER_TITTLE_ITEM2_SUBITEM = "<не выбрано>";
    public static final String SPINNER_TITTLE_ITEM3 = "Мелодия по-умолчанию";
    private static final String[] arrayForEachItem = {SPINNER_TITTLE_ITEM1, SPINNER_TITTLE_ITEM2, SPINNER_TITTLE_ITEM3};

    public MusicSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        layoutInflater = LayoutInflater.from(context);
        mObjects = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        convertView = layoutInflater.inflate(R.layout.spinner_item, parent, false);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.textView_spinner_title);
        TextView tvDescription = (TextView) convertView.findViewById(R.id.textView_spinner_desc);
        tvTitle.setText(arrayForEachItem[position]);
        tvDescription.setText(mObjects.get(position));

        return convertView;
    }
}
