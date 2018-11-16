package com.marcouberti.caregivers.db;

import android.database.sqlite.SQLiteConstraintException;

import com.marcouberti.caregivers.db.dao.CalendarDao;
import com.marcouberti.caregivers.db.dao.CaregiverDao;
import com.marcouberti.caregivers.db.entity.CaregiverEntity;
import com.marcouberti.caregivers.db.entity.SlotEntity;
import com.marcouberti.caregivers.db.entity.pojo.SlotCaregiver;
import com.marcouberti.caregivers.util.DateUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class CalendarDaoTests {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase mDb;
    private CalendarDao calendarDao;
    private CaregiverDao caregiverDao;

    @Before
    public void createDb() {
        mDb = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().getContext(),
                AppDatabase.class).build();
        calendarDao = mDb.calendarDao();
        caregiverDao = mDb.caregiverDao();
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }

    @Test
    public void foreignKeyCheck() throws Exception {

        // try to add a slot referencing a non-existing caregiver
        boolean exception = false;

        try {
            calendarDao.insertAll(generateSlot(new Date(), 1, "2", "1"));
        }catch (SQLiteConstraintException e) {
            exception = true;
        }

        assertTrue(exception);

    }

    @Test
    public void insertSlot() throws Exception {

        // need to add first a caregiver because of the foreign key
        caregiverDao.insertAll(generateCaregivers(1));

        Date today = new Date();
        calendarDao.insertAll(generateSlot(today, 1, "2", "1"));

        List<SlotEntity> slots = calendarDao.loadDailySlotsSync(today);

        assertEquals(1, slots.size());
        assertEquals("1", slots.get(0).getCaregiverId());
        assertEquals(new Integer(1), slots.get(0).getHour());
        assertEquals("2", slots.get(0).getRoomId());
        assertEquals("Patient name", slots.get(0).getPatientName());
        assertTrue(DateUtils.isSameDay(slots.get(0).getDate(), today));
    }

    @Test
    public void insertSlots() throws Exception {

        // need to add first a caregiver because of the foreign key
        caregiverDao.insertAll(generateCaregivers(1));

        Date today = new Date();
        List<SlotEntity> newSlots = generateSlot(today, 1, "2", "1");
            newSlots.addAll(generateSlot(today, 2, "5", "1"));
        calendarDao.insertAll(newSlots);

        List<SlotEntity> slots = calendarDao.loadDailySlotsSync(today);

        assertEquals(2, slots.size());
        assertEquals("1", slots.get(0).getCaregiverId());
        assertEquals(new Integer(1), slots.get(0).getHour());
        assertEquals("2", slots.get(0).getRoomId());
        assertEquals("Patient name", slots.get(0).getPatientName());
        assertTrue(DateUtils.isSameDay(slots.get(0).getDate(), today));

        assertEquals("1", slots.get(1).getCaregiverId());
        assertEquals(new Integer(2), slots.get(1).getHour());
        assertEquals("5", slots.get(1).getRoomId());
        assertEquals("Patient name", slots.get(1).getPatientName());
        assertTrue(DateUtils.isSameDay(slots.get(1).getDate(), today));
    }

    @Test
    public void loadDailySlotsSync() throws Exception {
        // need to add first a caregiver because of the foreign key
        caregiverDao.insertAll(generateCaregivers(1));

        Date today = new Date();
        calendarDao.insertAll(generateSlot(today, 1, "2", "1"));

        List<SlotEntity> slots = calendarDao.loadDailySlotsSync(today);

        assertEquals(1, slots.size());

        // try to modify date time (should ot affect the query)
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        slots = calendarDao.loadDailySlotsSync(cal.getTime());

        assertEquals(1, slots.size());
    }

    @Test
    public void deleteSlot() throws Exception {

        // need to add first a caregiver because of the foreign key
        caregiverDao.insertAll(generateCaregivers(1));

        Date today = new Date();
        List<SlotEntity> newSlots = generateSlot(today, 1, "2", "1");
        newSlots.addAll(generateSlot(today, 2, "5", "1"));
        calendarDao.insertAll(newSlots);

        //before
        List<SlotEntity> slots = calendarDao.loadDailySlotsSync(today);
        assertEquals(2, slots.size());
        calendarDao.deleteSlot(slots.get(0));
        //after
        slots = calendarDao.loadDailySlotsSync(today);
        assertEquals(1, slots.size());
    }

    @Test
    public void deleteSlots() throws Exception {

        // need to add first a caregiver because of the foreign key
        caregiverDao.insertAll(generateCaregivers(1));

        Date today = new Date();
        List<SlotEntity> newSlots = generateSlot(today, 1, "2", "1");
        newSlots.addAll(generateSlot(today, 2, "5", "1"));
        calendarDao.insertAll(newSlots);

        //before
        List<SlotEntity> slots = calendarDao.loadDailySlotsSync(today);
        assertEquals(2, slots.size());
        calendarDao.deleteSlots(slots);
        //after
        slots = calendarDao.loadDailySlotsSync(today);
        assertEquals(0, slots.size());
    }

    @Test
    public void updateSlot() throws Exception {

        // need to add first a caregiver because of the foreign key
        caregiverDao.insertAll(generateCaregivers(2));

        Date today = new Date();
        List<SlotEntity> newSlots = generateSlot(today, 1, "2", "1");
        newSlots.addAll(generateSlot(today, 2, "5", "1"));
        calendarDao.insertAll(newSlots);

        //before
        List<SlotEntity> slots = calendarDao.loadDailySlotsSync(today);
        assertEquals(2, slots.size());
            slots.get(0).setCaregiverId("2");
            slots.get(0).setRoomId("9");
        calendarDao.updateSlot(slots.get(0));
        //after
        slots = calendarDao.loadDailySlotsSync(today);
        assertEquals(2, slots.size());
        assertEquals("2", slots.get(0).getCaregiverId());
        assertEquals(new Integer(1), slots.get(0).getHour());
        assertEquals("9", slots.get(0).getRoomId());
        assertEquals("Patient name", slots.get(0).getPatientName());
    }

    @Test
    public void loadDailySlotCaregiversSync() throws Exception {
        // need to add first a caregiver because of the foreign key
        caregiverDao.insertAll(generateCaregivers(2));

        Date today = new Date();
        List<SlotEntity> newSlots = generateSlot(today, 1, "2", "1");
        newSlots.addAll(generateSlot(today, 2, "5", "2"));
        calendarDao.insertAll(newSlots);

        List<SlotCaregiver> scs = calendarDao.loadDailySlotCaregiversSync(today);
        assertEquals(2, scs.size());
    }

    @Test
    public void loadSlotCaregiverSync() throws Exception {
        // need to add first a caregiver because of the foreign key
        caregiverDao.insertAll(generateCaregivers(2));

        Date today = new Date();
        List<SlotEntity> newSlots = generateSlot(today, 1, "2", "1");
        newSlots.addAll(generateSlot(today, 2, "5", "2"));
        calendarDao.insertAll(newSlots);

        // non existing
        SlotCaregiver sc = calendarDao.loadSlotCaregiverSync(today, 1, "4");
        assertNull(sc);

        // existing one
        sc = calendarDao.loadSlotCaregiverSync(today, 2, "5");
        assertNotNull(sc);
        assertEquals("2", sc.caregiverId);
    }

    @Test
    public void getBusyRooms() throws Exception {
        // need to add first a caregiver because of the foreign key
        caregiverDao.insertAll(generateCaregivers(2));

        Date today = new Date();
        List<SlotEntity> newSlots = generateSlot(today, 1, "2", "1");
        newSlots.addAll(generateSlot(today, 2, "5", "2"));
        calendarDao.insertAll(newSlots);

        LiveData<List<String>> rooms = calendarDao.getBusyRooms(today, 1);
        Observer<List<String>> observer = ids -> assertEquals(1, ids.size());
        rooms.observeForever(observer);

        rooms = calendarDao.getBusyRooms(today, 3);
        observer = ids -> assertEquals(0, ids.size());
        rooms.observeForever(observer);

        rooms = calendarDao.getBusyRooms(today, 2);
        observer = ids -> assertEquals(1, ids.size());
        rooms.observeForever(observer);
    }


    // region utility methods
    private List<SlotEntity> generateSlot(Date date, int hour, String room, String caregiverId) {
        // add a slot for a caregiver, today
        SlotEntity slot = new SlotEntity();
            slot.setCaregiverId(caregiverId);
            slot.setDate(date);
            slot.setHour(hour);
            slot.setRoomId(room);
            slot.setPatientName("Patient name");

        List<SlotEntity> slots = new ArrayList<>();
            slots.add(slot);
        return slots;
    }

    private List<CaregiverEntity> generateCaregivers(int howMany) {
        List<CaregiverEntity> list = new ArrayList<>();
        for(int i=1; i<=howMany; i++) {
            final String ID = "" + i;
            final String NAME = "Name " + i;
            final String SURNAME = "Surname " + i;
            final String PHOTO = "Photo " + i;
            final String THUMB = "Thumb " + i;

            CaregiverEntity c = new CaregiverEntity();
            c.setId(ID);
            c.setName(NAME);
            c.setSurname(SURNAME);
            c.setPhoto(PHOTO);
            c.setThumbnail(THUMB);

            list.add(c);
        }
        return list;
    }
    // endregion
}
