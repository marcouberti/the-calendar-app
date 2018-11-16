package com.marcouberti.caregivers;

import android.app.Application;
import android.content.Context;

import com.marcouberti.caregivers.db.AppDatabase;
import com.marcouberti.caregivers.repository.CaregiversRepository;
import com.marcouberti.caregivers.repository.CalendarRepository;

public class CaregiversApplication extends Application {

    private AppExecutors appExecutors;
    private static Context context;

    public AppExecutors getAppExecutors() {
        if(appExecutors == null) appExecutors = new AppExecutors();
        return appExecutors;
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this, getAppExecutors());
    }

    public CaregiversRepository getCaregiverRepository() {
        return CaregiversRepository.getInstance(getDatabase());
    }

    public CalendarRepository getCalendarRepository() {
        return CalendarRepository.getInstance(getDatabase());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CaregiversApplication.context = getApplicationContext();
    }

    /**
     * With this method we can get app {@link Context} from everywhere
     */
    public static Context getContext() {
        return CaregiversApplication.context;
    }
}
