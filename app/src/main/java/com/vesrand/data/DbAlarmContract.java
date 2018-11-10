package com.vesrand.data;

import android.provider.BaseColumns;

public final class DbAlarmContract {
    private DbAlarmContract(){};

    public static final class AlarmEntry implements BaseColumns {
        public final static String TABLE_NAME = "alarm_items";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_CHECKED = "checked";
        public final static String COLUMN_TIME = "time";
        public final static String COLUMN_MUSIC = "music";
        public final static String COLUMN_MUSIC_SOURCE = "source";
        public final static String COLUMN_MOTIVATION = "motivation";

        public final static int CHECKED_TRUE = 1;
        public final static int CHECKED_FALSE = 0;
        public final static int MUSIC_RANDOM = 0;
        public final static int MUSIC_SELECT = 1;
        public final static int MUSIC_DEFAULT = 2;
    }

    public static final class AlarmDaysEntry implements BaseColumns {
        public final static String TABLE_NAME = "days";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_ALARM_ID = "alarm_id";
        public final static String COLUMN_DAYS = "days";

        public final static String DAY_FORMAT = "YYYY-MM-DD";
    }
}
