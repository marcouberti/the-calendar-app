package com.marcouberti.caregivers.util;

import android.content.Context;

import com.marcouberti.caregivers.CaregiversApplication;
import com.marcouberti.caregivers.R;
import com.marcouberti.caregivers.repository.CalendarRepository;
import com.marcouberti.caregivers.ui.calendar.CalendarActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String TAG = "DateUtils";

    public static boolean isYesterday(long date) {
        Calendar cdate = Calendar.getInstance();
        cdate.setTimeInMillis(date);

        cdate.add(Calendar.DATE,1);

        return android.text.format.DateUtils.isToday(cdate.getTimeInMillis());
    }

    public static boolean isTomorrow(long date) {
        Calendar cdate = Calendar.getInstance();
        cdate.setTimeInMillis(date);

        cdate.add(Calendar.DATE,-1);

        return android.text.format.DateUtils.isToday(cdate.getTimeInMillis());
    }

    public static boolean isToday(long date) {
        return android.text.format.DateUtils.isToday(date);
    }

    public static String prettyDate(Context ctx, Date date, int dateFormat) {

        if(isYesterday(date.getTime())) return ctx.getString(R.string.yesterday);
        if(isToday(date.getTime())) return ctx.getString(R.string.today);
        if(isTomorrow(date.getTime())) return ctx.getString(R.string.tomorrow);

        DateFormat f = DateFormat.getDateInstance(dateFormat, Locale.getDefault());
        String formattedDate = f.format(date);
        return formattedDate;
    }

    public static Date getRelativeDate(Date date, int calendarField, int amount) {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.add(calendarField, amount);

        return cal.getTime();
    }

    public static Date getWeekStartDate(Date date) {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return cal.getTime();
    }

    public static Date getWeekEndDate(Date date) {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return cal.getTime();
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
                c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    public static String formatLocalTime(Context ctx, int hour) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        String curTime = String.format("%02d:%02d", hour, 0);

        if(!android.text.format.DateFormat.is24HourFormat(ctx)) {
            DateFormat writeFormat = new SimpleDateFormat("a");
            curTime += " "+writeFormat.format(cal.getTime());
        }

        return curTime;
    }
}
