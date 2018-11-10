package com.vesrand;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class AlarmClass implements Parcelable {
    public int mID;
    public String mTime;
    public ArrayList<String> mDays;
    public int mMusicSource;
    public String mMusic;
    public boolean mMotivashka;
    public boolean mEnabled;
    public static final String TIME_DEFAULT = "00:00";

//    public void setTime(String time){this.mTime = time;}
//    public void setDays(ArrayList<String> days){this.mDays = days;}
//    public void setMusicSource(int musicSource){this.mMusicSource = musicSource;}
//    public void setMusic(String music){this.mMusic = music;}
//    public void setMotivashka(boolean motivashka){this.mMotivashka = motivashka;}
//    public void setEnabled (boolean enabled) {this.mEnabled = enabled;}
//    public String getTime(){return mTime;}
//    public ArrayList<String> getDays(){return mDays;}
//    public int getMusicSource(){return mMusicSource;}
//    public String getMusic(){return mMusic;}
//    public boolean getMotivashka(){return mMotivashka;}
//    public boolean getEnabled(){return mEnabled;}

    public AlarmClass (int id, String time, ArrayList<String> days, int musicSource, String music, boolean motivashka, boolean enabled) {
        mID = id;
        mTime = time;
        mDays = days;
        mMusicSource = musicSource;
        mMusic = music;
        mMotivashka = motivashka;
        mEnabled = enabled;
    }

    public AlarmClass (){
        mID = -1;
        mTime = TIME_DEFAULT;
        mDays = new ArrayList<String>();
        mMusicSource = 0;
        mMusic = "";
        mMotivashka = true;
        mEnabled = true;
    }

    private AlarmClass(Parcel in) {
        mID = in.readInt();
        mTime = in.readString();
        mDays = (ArrayList<String>) in.readSerializable();
        mMusicSource = in.readInt();
        mMusic = in.readString();
        mMotivashka = (in.readInt()) != 0;
        mEnabled = (in.readInt()) != 0;

    }

    public static final Creator<AlarmClass> CREATOR = new Creator<AlarmClass>() {
        @Override
        public AlarmClass createFromParcel(Parcel in) {
            return new AlarmClass(in);
        }

        @Override
        public AlarmClass[] newArray(int size) {
            return new AlarmClass[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mID);
        dest.writeString(mTime);
        dest.writeSerializable(mDays);
        dest.writeInt(mMusicSource);
        dest.writeString(mMusic);
        dest.writeInt(mMotivashka ? 1 : 0);
        dest.writeInt(mEnabled ? 1 : 0);
    }
}
