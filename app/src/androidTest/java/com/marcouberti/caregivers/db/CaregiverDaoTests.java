package com.marcouberti.caregivers.db;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.marcouberti.caregivers.db.dao.CalendarDao;
import com.marcouberti.caregivers.db.dao.CaregiverDao;
import com.marcouberti.caregivers.db.entity.CaregiverEntity;
import com.marcouberti.caregivers.db.entity.SlotEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

@RunWith(AndroidJUnit4.class)
public class CaregiverDaoTests {

    private AppDatabase mDb;
    private CaregiverDao caregiverDao;
    private CalendarDao calendarDao;

    @Before
    public void createDb() {
        mDb = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().getContext(),
                AppDatabase.class).build();
        caregiverDao = mDb.caregiverDao();
        calendarDao = mDb.calendarDao();
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }

    @Test
    public void insertCaregiver() throws Exception {

        caregiverDao.insertAll(generateCaregivers(1));

        List<CaregiverEntity> caregivers = caregiverDao.loadAllCaregiversSync();

        assertEquals(1, caregivers.size());
        assertEquals(caregivers.get(0).getId(), "1");
        assertEquals(caregivers.get(0).getName(), "Name 1");
        assertEquals(caregivers.get(0).getSurname(), "Surname 1");
        assertEquals(caregivers.get(0).getPhoto(), "Photo 1");
        assertEquals(caregivers.get(0).getThumbnail(), "Thumb 1");
    }

    @Test
    public void insertCaregivers() throws Exception {

        caregiverDao.insertAll(generateCaregivers(10));

        List<CaregiverEntity> caregivers = caregiverDao.loadAllCaregiversSync();

        assertEquals(10, caregivers.size());
        assertEquals(caregivers.get(9).getId(), "10");
        assertEquals(caregivers.get(9).getName(), "Name 10");
        assertEquals(caregivers.get(9).getSurname(), "Surname 10");
        assertEquals(caregivers.get(9).getPhoto(), "Photo 10");
        assertEquals(caregivers.get(9).getThumbnail(), "Thumb 10");
    }

    @Test
    public void loadAllCaregivers() throws Exception {
        caregiverDao.insertAll(generateCaregivers(10));
        assertEquals(10, caregiverDao.loadAllCaregiversSync().size());
    }


    @Test
    public void loadAllCaregiversAvailableForDateAndHourSync() throws Exception {
        caregiverDao.insertAll(generateCaregivers(10));
        // if no slot yet, all caregiver should be available
        assertEquals(10, caregiverDao.loadAllCaregiversAvailableForDateAndHourSync(new Date(), 9).size());

        // reserve a slot for caregiver 1 in today slot at 9:00
        calendarDao.insertAll(generateSlot(new Date(), 9, "5", "1"));

        // now the available caregivers should be N - 1 for this slot
        assertEquals(9, caregiverDao.loadAllCaregiversAvailableForDateAndHourSync(new Date(), 9).size());
    }

    @Test
    public void filterWithMaxSlotsInDateRangeSync() throws Exception {
        final int N = 10;

        // insert 10 caregivers
        caregiverDao.insertAll(generateCaregivers(N));
        // get all caregivers ids
        List<String> ids = Stream.of(caregiverDao.loadAllCaregiversSync()).map(CaregiverEntity::getId).collect(Collectors.toList());
        assertEquals(N, ids.size());

        // no slot yet, so all should be available
        Date today = new Date();
        assertEquals(N, caregiverDao.filterWithMaxSlotsInDateRangeSync(today, today, 1, ids).size());

        // add a slot for a caregiver, today
        calendarDao.insertAll(generateSlot(new Date(), 9, "5", "1"));

        // now the available caregiver for today should be N - 1
        assertEquals(N - 1, caregiverDao.filterWithMaxSlotsInDateRangeSync(today, today, 1, ids).size());

        // add another slot for another caregiver, today
        calendarDao.insertAll(generateSlot(new Date(), 10, "5", "2"));

        // now the available caregiver for today should be N - 2
        assertEquals(N - 2, caregiverDao.filterWithMaxSlotsInDateRangeSync(today, today, 1, ids).size());

        // check the filter too
        assertEquals(0, caregiverDao.filterWithMaxSlotsInDateRangeSync(today, today, 1, new ArrayList<>()).size());
    }

    @Test
    public void filterWorkingInDateSync() throws Exception {
        final int N = 10;

        // insert 10 caregivers
        caregiverDao.insertAll(generateCaregivers(N));
        // get all caregivers ids
        List<String> ids = Stream.of(caregiverDao.loadAllCaregiversSync()).map(CaregiverEntity::getId).collect(Collectors.toList());
        assertEquals(N, ids.size());

        // no slot yet, so no one should be working in today date
        Date today = new Date();
        assertEquals(0, caregiverDao.filterWorkingInDateSync(today, ids).size());

        // add a slot for a caregiver, today
        calendarDao.insertAll(generateSlot(new Date(), 9, "5", "1"));

        // now the working caregivers should be 1
        assertEquals(1, caregiverDao.filterWorkingInDateSync(today, ids).size());

        // add another slot for another caregiver, today
        calendarDao.insertAll(generateSlot(new Date(), 10, "5", "2"));

        // now the working caregivers should be 2
        assertEquals(2, caregiverDao.filterWorkingInDateSync(today, ids).size());

        // check the filter too
        assertEquals(0, caregiverDao.filterWorkingInDateSync(today, new ArrayList<>()).size());
    }

    @Test
    public void filterLessWorkingInRangeSync() throws Exception {
        final int N = 10;

        // insert 10 caregivers
        caregiverDao.insertAll(generateCaregivers(N));
        // get all caregivers ids
        List<String> ids = Stream.of(caregiverDao.loadAllCaregiversSync()).map(CaregiverEntity::getId).collect(Collectors.toList());
        assertEquals(N, ids.size());

        // no slot yet, so no one should be working in today date
        Date today = new Date();

        // add a slot for a caregiver, today
        calendarDao.insertAll(generateSlot(new Date(), 9, "1", "1")); // 1 has 2 slots, 5 has 0 slots, the others one
        calendarDao.insertAll(generateSlot(new Date(), 8, "2", "1"));
        calendarDao.insertAll(generateSlot(new Date(), 8, "3", "2"));
        calendarDao.insertAll(generateSlot(new Date(), 8, "4", "3"));
        calendarDao.insertAll(generateSlot(new Date(), 8, "5", "4"));
        calendarDao.insertAll(generateSlot(new Date(), 8, "6", "10"));
        calendarDao.insertAll(generateSlot(new Date(), 8, "7", "6"));
        calendarDao.insertAll(generateSlot(new Date(), 8, "8", "7"));
        calendarDao.insertAll(generateSlot(new Date(), 8, "9", "8"));
        calendarDao.insertAll(generateSlot(new Date(), 8, "10", "9"));

        // filter and get the caregivers ordered ASC based on how much they worked in the date range
        List<CaregiverEntity> filtered = caregiverDao.filterLessWorkingInRangeSync(today, today, ids);
        assertEquals(N, filtered.size());

        // the caregiver "1" that worked more than the others should be the last one
        assertEquals("1", filtered.get(N-1).getId());

        // the caregiver "5" that worked less than the others should be the first one
        assertEquals("5", filtered.get(0).getId());

        // check the filter too
        assertEquals(0, caregiverDao.filterLessWorkingInRangeSync(today, today, new ArrayList<>()).size());
    }

    @Test
    public void loadSingleCaregiver() throws Exception {
        final String ID = "1";

        // load not existing caregiver
        CaregiverEntity c = caregiverDao.loadCaregiverSync(ID);
        assertNull(c);

        // insert one
        caregiverDao.insertAll(generateCaregivers(1));

        // load existing one
        c = caregiverDao.loadCaregiverSync(ID);
        assertNotNull(c);
        assertEquals(c.getId(), "1");
        assertEquals(c.getName(), "Name 1");
        assertEquals(c.getSurname(), "Surname 1");
        assertEquals(c.getPhoto(), "Photo 1");
        assertEquals(c.getThumbnail(), "Thumb 1");
    }

    // region test utility methods
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
    // endregion
}
