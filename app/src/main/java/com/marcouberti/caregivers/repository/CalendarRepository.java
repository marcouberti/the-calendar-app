package com.marcouberti.caregivers.repository;

import com.marcouberti.caregivers.db.AppDatabase;
import com.marcouberti.caregivers.db.entity.SlotEntity;
import com.marcouberti.caregivers.db.entity.pojo.SlotCaregiver;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;

public class CalendarRepository {

    private static CalendarRepository sInstance;
    private final AppDatabase mDatabase;

    public static CalendarRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (CalendarRepository.class) {
                if (sInstance == null) {
                    sInstance = new CalendarRepository(database);
                }
            }
        }
        return sInstance;
    }

    public CalendarRepository(final AppDatabase database) {
        mDatabase = database;
    }

    public LiveData<List<SlotCaregiver>> getSlots(Date date) {
        return mDatabase.calendarDao().loadDailySlotCaregivers(date);
    }

    public List<SlotEntity> getSlotsSync(Date date) {
        return mDatabase.calendarDao().loadDailySlotsSync(date);
    }

    public LiveData<SlotCaregiver> getSlot(Date date, int hour, String room) {
        return mDatabase.calendarDao().loadSlotCaregiver(date, hour, room);
    }

    public SlotCaregiver getSlotAsync(Date date, int hour, String room) {
        return mDatabase.calendarDao().loadSlotCaregiverSync(date, hour, room);
    }

    public void add(SlotCaregiver sc) {
        if(sc != null) {
            SlotEntity se = new SlotEntity();
                se.setCaregiverId(sc.caregiverId);
                se.setHour(sc.hour);
                se.setRoomId(sc.roomId);
                se.setDate(sc.date);
                se.setPatientName(sc.patientName);

                List<SlotEntity> slots = new ArrayList<>();
                slots.add(se);
                mDatabase.calendarDao().insertAll(slots);
        }
    }

    public LiveData<List<String>> getBusyRooms(Date date, int hour) {
        return mDatabase.calendarDao().getBusyRooms(date, hour);
    }

    public void delete(SlotCaregiver sc) {
        if(sc != null) {
            SlotEntity se = new SlotEntity();
                se.setId(sc.id);
                se.setCaregiverId(sc.caregiverId);
                se.setHour(sc.hour);
                se.setRoomId(sc.roomId);
                se.setDate(sc.date);
                se.setPatientName(sc.patientName);

            mDatabase.calendarDao().deleteSlot(se);
        }
    }

    public void update(SlotCaregiver sc) {
        if(sc != null) {
            SlotEntity se = new SlotEntity();
            se.setId(sc.id);
            se.setCaregiverId(sc.caregiverId);
            se.setHour(sc.hour);
            se.setRoomId(sc.roomId);
            se.setDate(sc.date);
            se.setPatientName(sc.patientName);

            mDatabase.calendarDao().updateSlot(se);
        }
    }

    public void deleteAll(Date date) {
        List<SlotEntity> list = mDatabase.calendarDao().loadDailySlotsSync(date);
        mDatabase.calendarDao().deleteSlots(list);
    }
}
