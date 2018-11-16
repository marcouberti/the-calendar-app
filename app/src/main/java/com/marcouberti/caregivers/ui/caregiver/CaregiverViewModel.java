package com.marcouberti.caregivers.ui.caregiver;

import android.app.Application;

import com.marcouberti.caregivers.CaregiversApplication;
import com.marcouberti.caregivers.db.entity.CaregiverEntity;
import com.marcouberti.caregivers.repository.CaregiversRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CaregiverViewModel extends AndroidViewModel {

    private CaregiversRepository repository;

    private LiveData<CaregiverEntity> mObservableCaregiver;

    public CaregiverViewModel(Application application, final String caregiverId) {
        super(application);

        repository = ((CaregiversApplication)application).getCaregiverRepository();
        mObservableCaregiver = repository.getCaregiver(caregiverId);
    }

    public LiveData<CaregiverEntity> getCaregiver() {
        return mObservableCaregiver;
    }

    /**
     * This is to inject the caregiverId into the ViewModel, instead of using a public method.
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private String caregiverId;

        public Factory(@NonNull Application application, String id) {
            mApplication = application;
            caregiverId = id;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new CaregiverViewModel(mApplication, caregiverId);
        }
    }
}
