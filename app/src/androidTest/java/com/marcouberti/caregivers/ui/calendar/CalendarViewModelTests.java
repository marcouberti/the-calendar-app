package com.marcouberti.caregivers.ui.calendar;

import android.app.Application;

import com.marcouberti.caregivers.CaregiversApplication;
import com.marcouberti.caregivers.util.DateUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class CalendarViewModelTests {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void onDateChange() {
        CalendarViewModel viewModel = new CalendarViewModel((Application) CaregiversApplication.getContext());

        AtomicInteger cont = new AtomicInteger();
        AtomicInteger dateCont = new AtomicInteger();

        Observer<List<CalendarViewModel.TimeSlot>> observer = slots -> cont.getAndIncrement();
        Observer<Date> dateObserver = date -> dateCont.getAndIncrement();

        viewModel.getSlots().observeForever(observer);
        viewModel.getDate().observeForever(dateObserver);

        // at startup the live data are triggered with default values
        assertEquals(1, cont.get());
        assertEquals(1, dateCont.get());

        // changing the date should trigger the date live data and also the slot live data
        Date newDate = DateUtils.getRelativeDate(new Date(), Calendar.MONTH, -6);
        viewModel.onDateChange(newDate);

        assertEquals(2, cont.get());
        assertEquals(2, dateCont.get());
        assertTrue(DateUtils.isSameDay(newDate, viewModel.getDate().getValue()));
    }

}
