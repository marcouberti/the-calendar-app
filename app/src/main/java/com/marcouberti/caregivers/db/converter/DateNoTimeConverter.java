package com.marcouberti.caregivers.db.converter;

import java.util.Calendar;
import java.util.Date;

import androidx.room.TypeConverter;

public class DateNoTimeConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        if(timestamp == null) return null;
        Date d = new Date(timestamp);
        Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        if(date == null) return null;
        Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().getTime();
    }
}
