package com.marcouberti.caregivers.repository;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.marcouberti.caregivers.db.AppDatabase;
import com.marcouberti.caregivers.db.entity.CaregiverEntity;
import com.marcouberti.caregivers.webservice.CaregiversService;
import com.marcouberti.caregivers.webservice.parser.CaregiversServiceParser;

import java.io.IOException;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CaregiversRepository {

    private static final int MAX_CAREGIVERS = 100;
    private static CaregiversRepository sInstance;
    private final AppDatabase mDatabase;
    CaregiversService service;

    public static CaregiversRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (CaregiversRepository.class) {
                if (sInstance == null) {
                    sInstance = new CaregiversRepository(database);
                }
            }
        }
        return sInstance;
    }

    public CaregiversRepository(final AppDatabase database) {
        mDatabase = database;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://randomuser.me/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        service = retrofit.create(CaregiversService.class);
    }

    public List<CaregiverEntity> getCaregiversSync() {
        return mDatabase.caregiverDao().loadAllCaregiversSync();
    }

    public LiveData<CaregiverEntity> getCaregiver(String id) {
        return mDatabase.caregiverDao().loadCaregiver(id);
    }

    public CaregiverEntity getCaregiverAsync(String id) {
        return mDatabase.caregiverDao().loadCaregiverSync(id);
    }

    public List<CaregiverEntity> fetchCaregivers(int page, int results) {
        List<CaregiverEntity> output = null;

        //force limit to 100 caregivers
        if(page * results > MAX_CAREGIVERS) {
            return null;
        }

        Call<String> call = service.listCaregivers("empatica", page, results);

        if(call != null) {
            String json;
            try {
                json = call.execute().body();
                output = CaregiversServiceParser.parse(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(output != null) mDatabase.caregiverDao().insertAll(output);
        return output;
    }

    public LiveData<List<CaregiverEntity>> loadAllCaregiversAvailableForDateAndHour(Date date, int hour) {
        return mDatabase.caregiverDao().loadAllCaregiversAvailableForDateAndHour(date, hour);
    }

    public List<CaregiverEntity> loadAllCaregiversAvailableForDateAndHourSync(Date date, int hour) {
        return mDatabase.caregiverDao().loadAllCaregiversAvailableForDateAndHourSync(date, hour);
    }

    public List<CaregiverEntity> filterWithMaxSlotsInDateRangeSync(Date weekStart, Date weekEnd, int maxSlotPerWeek, List<CaregiverEntity> caregiver) {
        List<String> ids = Stream.of(caregiver).map(CaregiverEntity::getId).collect(Collectors.toList());
        return mDatabase.caregiverDao().filterWithMaxSlotsInDateRangeSync(weekStart, weekEnd, maxSlotPerWeek, ids);
    }

    public List<CaregiverEntity> filterWorkingInDateSync(Date date, List<CaregiverEntity> caregiver) {
        List<String> ids = Stream.of(caregiver).map(CaregiverEntity::getId).collect(Collectors.toList());
        return mDatabase.caregiverDao().filterWorkingInDateSync(date, ids);
    }

    public List<CaregiverEntity> filterLessWorkingInRangeSync(Date date, Date endDate, List<CaregiverEntity> caregiver) {
        List<String> ids = Stream.of(caregiver).map(CaregiverEntity::getId).collect(Collectors.toList());
        return mDatabase.caregiverDao().filterLessWorkingInRangeSync(date, endDate, ids);
    }
}
