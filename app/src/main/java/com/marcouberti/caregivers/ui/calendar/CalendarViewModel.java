package com.marcouberti.caregivers.ui.calendar;

import android.app.Application;

import com.marcouberti.caregivers.AppExecutors;
import com.marcouberti.caregivers.CaregiversApplication;
import com.marcouberti.caregivers.db.entity.pojo.SlotCaregiver;
import com.marcouberti.caregivers.repository.CalendarRepository;
import com.marcouberti.caregivers.util.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

public class CalendarViewModel extends AndroidViewModel {

    private CalendarRepository repository;
    private AppExecutors appExecutors;

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<List<TimeSlot>> mObservableSlots;
    private final MutableLiveData<Date> mObservableDate;

    private LiveData<List<SlotCaregiver>> slotSource;

    public CalendarViewModel(Application application) {
        super(application);

        repository = ((CaregiversApplication)application).getCalendarRepository();
        appExecutors = ((CaregiversApplication)application).getAppExecutors();

        mObservableDate = new MutableLiveData<>();
        mObservableDate.setValue(new Date());

        mObservableSlots = new MediatorLiveData<>();
        // set by default null, until we get data from the database.
        mObservableSlots.setValue(null);

        // observe the changes of the slots from the database and forward them
        mObservableSlots.addSource(mObservableDate, date -> {
            if(slotSource != null) mObservableSlots.removeSource(slotSource);
            slotSource = repository.getSlots(mObservableDate.getValue());
            mObservableSlots.addSource(slotSource, slots -> {
                if(slots == null) return;
                List<TimeSlot> timeSlotsList = new ArrayList<>();
                for(int i=0; i<Constants.DAILY_HOURS; i++) {
                    TimeSlot ts = new TimeSlot();
                        ts.hour = i;
                        ts.date = date;
                    for (SlotCaregiver sc : slots) {
                        if(sc.hour == i) {
                            ts.roomSlots.put(sc.roomId, sc);
                        }
                    }
                    timeSlotsList.add(ts);
                }
                mObservableSlots.setValue(timeSlotsList);
            });
        });
    }

    public LiveData<List<TimeSlot>> getSlots() {
        return mObservableSlots;
    }

    public LiveData<Date> getDate() {
        return mObservableDate;
    }

    public void onTapPreviousDay() {
        Date d = mObservableDate.getValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);

        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date dayBefore = calendar.getTime();
        mObservableDate.setValue(dayBefore);
    }

    public void onTapNextDay() {
        Date d = mObservableDate.getValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date dayAfter = calendar.getTime();
        mObservableDate.setValue(dayAfter);
    }

    public void onDateChange(Date date) {
        mObservableDate.setValue(date);
    }

    class TimeSlot {
        public Date date;
        public int hour;
        public HashMap<String, SlotCaregiver> roomSlots = new HashMap<>();

        @Override
        public boolean equals(@Nullable Object obj) {
            if(obj instanceof TimeSlot) return false;
            TimeSlot ts = (TimeSlot)obj;
            if(date.getTime() != ts.date.getTime()) return false;
            if(hour != ts.hour) return false;
            for(int i=1; i<=Constants.ROOM_COUNT; i++) {
                SlotCaregiver sc1 = roomSlots.get(i);
                SlotCaregiver sc2 = ts.roomSlots.get(i);

                if(sc1 == null && sc2 != null) return false;
                if(sc2 == null && sc1 != null) return false;
                if(sc1 == null && sc2 == null) continue;

                if(sc1.roomId != sc2.roomId) return false;
                if(sc1.caregiverId != sc2.caregiverId) return false;
            }
            return true;
        }
    }
}
