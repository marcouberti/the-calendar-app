package com.marcouberti.caregivers.db.converter;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DateNoTimeConverterUnitTest {

    @Test
    public void toDate() {

        assertNull(DateNoTimeConverter.toDate(null));

        Date now = new Date();
        Date _toDate = DateNoTimeConverter.toDate(now.getTime());

        Calendar c = Calendar.getInstance();
        c.setTime(_toDate);

        assertEquals(0, c.get(Calendar.HOUR));
        assertEquals(0, c.get(Calendar.MINUTE));
        assertEquals(0, c.get(Calendar.SECOND));
        assertEquals(0, c.get(Calendar.MILLISECOND));
    }

    @Test
    public void toTimestamp() {

        assertNull(DateNoTimeConverter.toTimestamp(null));

        long ts = DateNoTimeConverter.toTimestamp(new Date());

        Date date = new Date();
        date.setTime(ts);

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        assertEquals(0, c.get(Calendar.HOUR));
        assertEquals(0, c.get(Calendar.MINUTE));
        assertEquals(0, c.get(Calendar.SECOND));
        assertEquals(0, c.get(Calendar.MILLISECOND));
    }
}