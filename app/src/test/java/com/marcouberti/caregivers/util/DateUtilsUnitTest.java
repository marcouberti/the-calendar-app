package com.marcouberti.caregivers.util;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class DateUtilsUnitTest {

    @Test
    public void testGetWeekStartDate() {
        // generic date
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2018);
        c1.set(Calendar.MONTH, Calendar.NOVEMBER);
        c1.set(Calendar.DAY_OF_MONTH, 8);
        Date day = c1.getTime();

        Date firstDayOfTheWeeek = DateUtils.getWeekStartDate(day);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(firstDayOfTheWeeek);

        assertEquals(5, c2.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.NOVEMBER, c2.get(Calendar.MONTH));
        assertEquals(2018, c2.get(Calendar.YEAR));

        // first day of the year
        c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2019);
        c1.set(Calendar.MONTH, Calendar.JANUARY);
        c1.set(Calendar.DAY_OF_MONTH, 1);
        day = c1.getTime();

        firstDayOfTheWeeek = DateUtils.getWeekStartDate(day);

        c2 = Calendar.getInstance();
        c2.setTime(firstDayOfTheWeeek);

        assertEquals(31, c2.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.DECEMBER, c2.get(Calendar.MONTH));
        assertEquals(2018, c2.get(Calendar.YEAR));
    }

    @Test
    public void testGetWeekEndDate() {

        // generic date
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2018);
        c1.set(Calendar.MONTH, Calendar.NOVEMBER);
        c1.set(Calendar.DAY_OF_MONTH, 8);
        Date day = c1.getTime();

        Date firstDayOfTheWeeek = DateUtils.getWeekEndDate(day);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(firstDayOfTheWeeek);

        assertEquals(11, c2.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.NOVEMBER, c2.get(Calendar.MONTH));
        assertEquals(2018, c2.get(Calendar.YEAR));

        // last day of the year
        c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2018);
        c1.set(Calendar.MONTH, Calendar.DECEMBER);
        c1.set(Calendar.DAY_OF_MONTH, 31);
        day = c1.getTime();

        firstDayOfTheWeeek = DateUtils.getWeekEndDate(day);

        c2 = Calendar.getInstance();
        c2.setTime(firstDayOfTheWeeek);

        assertEquals(6, c2.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, c2.get(Calendar.MONTH));
        assertEquals(2019, c2.get(Calendar.YEAR));
    }

    @Test
    public void testIsSameDay() {

        // generic date
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2018);
        c1.set(Calendar.MONTH, Calendar.NOVEMBER);
        c1.set(Calendar.DAY_OF_MONTH, 8);
        c1.set(Calendar.HOUR_OF_DAY, 5); // differs here
        Date day1 = c1.getTime();

        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.YEAR, 2018);
        c2.set(Calendar.MONTH, Calendar.NOVEMBER);
        c2.set(Calendar.DAY_OF_MONTH, 8);
        c2.set(Calendar.HOUR_OF_DAY, 8); // differs here
        Date day2 = c2.getTime();

        assertTrue(DateUtils.isSameDay(day1, day2));

        Calendar c3 = Calendar.getInstance();
        c3.set(Calendar.YEAR, 2018);
        c3.set(Calendar.MONTH, Calendar.OCTOBER);
        c3.set(Calendar.DAY_OF_MONTH, 8);
        c3.set(Calendar.HOUR_OF_DAY, 8); // differs here
        Date day3 = c3.getTime();

        assertFalse(DateUtils.isSameDay(day1, day3));
    }

    @Test
    public void testGetRelativeDate() {
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2018);
        c1.set(Calendar.MONTH, Calendar.NOVEMBER);
        c1.set(Calendar.DAY_OF_MONTH, 8);
        c1.set(Calendar.HOUR_OF_DAY, 5); // differs here
        Date date1 = c1.getTime();

        Date date2 = DateUtils.getRelativeDate(date1, Calendar.MONTH, -1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        assertEquals(Calendar.OCTOBER, c2.get(Calendar.MONTH));
        assertEquals(8, c2.get(Calendar.DAY_OF_MONTH));
        assertEquals(2018, c2.get(Calendar.YEAR));
    }

}