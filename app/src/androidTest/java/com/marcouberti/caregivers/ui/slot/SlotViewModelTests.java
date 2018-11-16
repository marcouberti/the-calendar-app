package com.marcouberti.caregivers.ui.slot;

import android.app.Application;

import com.marcouberti.caregivers.CaregiversApplication;
import com.marcouberti.caregivers.db.AppDatabase;
import com.marcouberti.caregivers.db.dao.CalendarDao;
import com.marcouberti.caregivers.db.dao.CaregiverDao;
import com.marcouberti.caregivers.db.entity.CaregiverEntity;
import com.marcouberti.caregivers.db.entity.pojo.SlotCaregiver;
import com.marcouberti.caregivers.ui.calendar.CalendarViewModel;
import com.marcouberti.caregivers.util.DateUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class SlotViewModelTests {

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();


    @Test
    public void onRoomChange() {
        SlotViewModel viewModel = new SlotViewModel((Application) CaregiversApplication.getContext());

        AtomicInteger cont = new AtomicInteger();
        Observer<SlotCaregiver> slotObserver = room -> cont.getAndIncrement();
        viewModel.getSlot().observeForever(slotObserver);

        assertEquals(1, cont.get());

        viewModel.onRoomChanged("5");

        assertEquals(2, cont.get());

        assertEquals("5", viewModel.getSlot().getValue().roomId);
    }

    @Test
    public void onPatientNameChange() {
        SlotViewModel viewModel = new SlotViewModel((Application) CaregiversApplication.getContext());

        AtomicInteger cont = new AtomicInteger();
        Observer<SlotCaregiver> slotObserver = room -> cont.getAndIncrement();
        viewModel.getSlot().observeForever(slotObserver);

        assertEquals(1, cont.get());

        viewModel.onPatientNameChanged("Marco Uberti");

        assertEquals(2, cont.get());

        assertEquals("Marco Uberti", viewModel.getSlot().getValue().patientName);
    }
}
