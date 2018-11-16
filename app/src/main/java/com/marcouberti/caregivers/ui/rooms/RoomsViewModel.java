package com.marcouberti.caregivers.ui.rooms;

import android.app.Application;

import com.marcouberti.caregivers.CaregiversApplication;
import com.marcouberti.caregivers.repository.CalendarRepository;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class RoomsViewModel extends AndroidViewModel {

    private CalendarRepository repository;

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<List<String>> mObservableBusyRooms;

    public RoomsViewModel(Application application, final Filter filter) {
        super(application);

        repository = ((CaregiversApplication)application).getCalendarRepository();

        mObservableBusyRooms = new MediatorLiveData<>();
        mObservableBusyRooms.setValue(null);

        LiveData<List<String>> ids = repository.getBusyRooms(filter.date, filter.hour);
        mObservableBusyRooms.addSource(ids, mObservableBusyRooms::setValue);
    }

    public LiveData<List<String>> getBusyRooms() {
        return mObservableBusyRooms;
    }

    static class Filter {
        Date date;
        int hour;
    }

    /**
     * This is to inject the filter into the ViewModel, instead of using a public method.
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private Filter filter;

        public Factory(@NonNull Application application, Filter filter) {
            mApplication = application;
            this.filter = filter;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new RoomsViewModel(mApplication, filter);
        }
    }
}
