package com.marcouberti.caregivers.worker;

import android.content.Context;
import android.widget.Toast;

import com.marcouberti.caregivers.CaregiversApplication;
import com.marcouberti.caregivers.R;
import com.marcouberti.caregivers.db.entity.CaregiverEntity;
import com.marcouberti.caregivers.repository.CaregiversRepository;
import com.marcouberti.caregivers.util.Constants;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class FetchAllCaregiversWorker extends Worker {

    private CaregiversRepository caregiversRepository;

    public FetchAllCaregiversWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {

        if(getApplicationContext() instanceof CaregiversApplication) {
            caregiversRepository = ((CaregiversApplication)getApplicationContext()).getCaregiverRepository();
        }else return Result.FAILURE;

        // If not already fetched the 100 caregivers, fetch them all
        List<CaregiverEntity> allCaregivers = caregiversRepository.getCaregiversSync();
        if(allCaregivers == null || allCaregivers.size() < Constants.CAREGIVERS_COUNT) {
            allCaregivers = caregiversRepository.fetchCaregivers(1, Constants.CAREGIVERS_COUNT);
        }

        if(allCaregivers == null || allCaregivers.size() < Constants.CAREGIVERS_COUNT) {
            Toast.makeText(getApplicationContext(),
                    getApplicationContext().getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT).show();
            return Result.FAILURE;
        }

        return Result.SUCCESS;
    }
}
