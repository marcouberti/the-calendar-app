package com.marcouberti.caregivers.ui.slot;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.marcouberti.caregivers.AppExecutors;
import com.marcouberti.caregivers.CaregiversApplication;
import com.marcouberti.caregivers.R;
import com.marcouberti.caregivers.db.entity.pojo.SlotCaregiver;
import com.marcouberti.caregivers.model.Caregiver;
import com.marcouberti.caregivers.repository.CaregiversRepository;
import com.marcouberti.caregivers.repository.CalendarRepository;
import com.marcouberti.caregivers.util.livedata.SingleLiveEvent;

import java.util.Date;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

public class SlotViewModel extends AndroidViewModel {

    private CalendarRepository repository;
    private AppExecutors appExecutors;

    private MutableLiveData<SlotCaregiver> mObservableSlot;
    private MutableLiveData<Boolean> editing;
    private MediatorLiveData<Boolean> canSave;
    private SingleLiveEvent<Boolean> exitEvent;

    public SlotViewModel(Application application) {
        super(application);
        appExecutors = ((CaregiversApplication)application).getAppExecutors();
        repository = ((CaregiversApplication)application).getCalendarRepository();
        mObservableSlot = new MutableLiveData<>();
        mObservableSlot.setValue(new SlotCaregiver());
        editing = new MutableLiveData<>();
        editing.setValue(false);
        canSave = new MediatorLiveData<>();
        canSave.addSource(mObservableSlot, sc -> {
            canSave.setValue(validateItem(sc));
        });
        exitEvent = new SingleLiveEvent<>();
    }

    void setStartingParams(Date date, int hour, String room) {

        SlotCaregiver defaultSc = new SlotCaregiver();
            defaultSc.date = date;
            defaultSc.hour = hour;
            defaultSc.roomId = room;
            defaultSc.patientName = getApplication().getString(R.string.patient_name);
        mObservableSlot.setValue(defaultSc);

        appExecutors.diskIO().execute(() -> {
            SlotCaregiver sc = repository.getSlotAsync(date, hour, room);
            if(sc != null) {
                editing.postValue(true);
                mObservableSlot.postValue(sc);
            }else {
                editing.postValue(false);
            }
        });
    }

    public LiveData<SlotCaregiver> getSlot() {
        return mObservableSlot;
    }
    public LiveData<Boolean> isEditing() {
        return editing;
    }
    public LiveData<Boolean> canSave() {
        return canSave;
    }
    public LiveData<Boolean> getExitEvent() {
        return exitEvent;
    }

    void onTapSave() {

        SlotCaregiver sc = mObservableSlot.getValue();
        if(sc == null || sc.caregiverId == null || sc.patientName == null || sc.roomId == null) {
            Context ctx = getApplication().getApplicationContext();
            Toast.makeText(ctx, ctx.getString(R.string.invalid_data), Toast.LENGTH_SHORT).show();
            return;
        }

        appExecutors.diskIO().execute(() -> {
            try {
                if(editing.getValue()) {
                    repository.update(mObservableSlot.getValue());
                }else {
                    repository.add(mObservableSlot.getValue());
                }

                exitEvent.call();
            } catch (SQLiteException se) {
                se.printStackTrace();
                Context ctx = getApplication().getApplicationContext();
                appExecutors.mainThread().execute(() -> Toast.makeText(ctx, ctx.getString(R.string.database_error), Toast.LENGTH_SHORT).show());
            }
        });
    }


    public void onTapDelete() {
        SlotCaregiver sc = mObservableSlot.getValue();
        if(sc == null || sc.caregiverId == null || sc.patientName == null || sc.roomId == null) {
            Context ctx = getApplication().getApplicationContext();
            Toast.makeText(ctx, ctx.getString(R.string.invalid_data), Toast.LENGTH_SHORT).show();
            return;
        }

        appExecutors.diskIO().execute(() -> {
            try {
                repository.delete(mObservableSlot.getValue());
                exitEvent.call();
            } catch (SQLiteException se) {
                se.printStackTrace();
                Context ctx = getApplication().getApplicationContext();
                appExecutors.mainThread().execute(() -> Toast.makeText(ctx, ctx.getString(R.string.database_error), Toast.LENGTH_SHORT).show());
            }
        });
    }

    void onCaregiverChanged(String caregiverId) {
        appExecutors.diskIO().execute(() -> {
            CaregiversRepository caregiverRepository = ((CaregiversApplication)getApplication()).getCaregiverRepository();
            Caregiver caregiver = caregiverRepository.getCaregiverAsync(caregiverId);

            SlotCaregiver sc = mObservableSlot.getValue();
                sc.caregiverId = caregiver.getId();
                sc.caregiverName = caregiver.getName();
                sc.caregiverSurname = caregiver.getSurname();
                sc.caregiverPhoto = caregiver.getPhoto();
                sc.caregiverThumbnail = caregiver.getThumbnail();
            mObservableSlot.postValue(sc);
        });
    }

    void onRoomChanged(String roomId) {
        SlotCaregiver sc = mObservableSlot.getValue();
        sc.roomId = roomId;
        mObservableSlot.setValue(sc);
    }

    void onPatientNameChanged(String patientName) {
        SlotCaregiver sc = mObservableSlot.getValue();
        sc.patientName = patientName;
        mObservableSlot.setValue(sc);
    }


    private boolean validateItem(SlotCaregiver sc) {
        if(sc != null && sc.caregiverId != null && sc.roomId != null && sc.patientName != null) return true;
        else return false;
    }
}