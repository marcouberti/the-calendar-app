package com.marcouberti.caregivers.ui.caregivers;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.marcouberti.caregivers.AppExecutors;
import com.marcouberti.caregivers.CaregiversApplication;
import com.marcouberti.caregivers.R;
import com.marcouberti.caregivers.db.entity.CaregiverEntity;
import com.marcouberti.caregivers.repository.CaregiversRepository;
import com.marcouberti.caregivers.util.livedata.SingleLiveEvent;

import java.util.Date;
import java.util.List;

public class CaregiversViewModel extends AndroidViewModel {

    private final int RESULTS_PER_PAGE = 10;
    private AppExecutors appExecutors;
    private CaregiversRepository repository;

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<List<CaregiverEntity>> mObservableCaregivers;
    private MutableLiveData<Boolean> isPicking;
    private SingleLiveEvent<String> toastEvent;

    public CaregiversViewModel(Application application, final Filter filter) {
        super(application);

        appExecutors = ((CaregiversApplication)application).getAppExecutors();
        repository = ((CaregiversApplication)application).getCaregiverRepository();

        mObservableCaregivers = new MediatorLiveData<>();
        // set by default null, until we get data from the database.
        mObservableCaregivers.setValue(null);

        LiveData<List<CaregiverEntity>> caregivers = repository.loadAllCaregiversAvailableForDateAndHour(filter.date, filter.hour);

        // observe the changes of the caregivers list from the database and forward them
        mObservableCaregivers.addSource(caregivers, mObservableCaregivers::setValue);

        isPicking = new MutableLiveData<>();
        toastEvent = new SingleLiveEvent<>();

        // ask for the online ones to refresh if available
        loadCaregivers();
    }

    public LiveData<List<CaregiverEntity>> getCaregivers() {
        return mObservableCaregivers;
    }
    public LiveData<Boolean> getIsPicking() {
        return isPicking;
    }
    public SingleLiveEvent<String> getToastEvent() {
        return toastEvent;
    }


    private void loadCaregivers() {
        appExecutors.networkIO().execute(() -> {
            Object result = repository.fetchCaregivers(1,RESULTS_PER_PAGE);
            if(result == null) toastEvent.postValue(getApplication().getString(R.string.no_internet_connection));
        });
    }

    void loadMoreCaregivers() {
        appExecutors.networkIO().execute(() -> {
            int page = mObservableCaregivers.getValue()!=null?mObservableCaregivers.getValue().size()/10 +1:1;
            repository.fetchCaregivers(page,RESULTS_PER_PAGE);
        });
    }

    public void setIsPicking(boolean b) {
        isPicking.setValue(b);
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
            return (T) new CaregiversViewModel(mApplication, filter);
        }
    }
}
