package com.marcouberti.caregivers.repository;

import com.marcouberti.caregivers.db.AppDatabase;
import com.marcouberti.caregivers.db.dao.CaregiverDao;
import com.marcouberti.caregivers.webservice.CaregiversService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

public class CaregiversRepositoryUnitTest {

    @Mock
    CaregiversService mockWebService;
    @Mock
    AppDatabase mockDatabase;
    @Mock
    CaregiverDao mockDao;

    CaregiversRepository repository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        repository = new CaregiversRepository(mockDatabase);
        repository.service = mockWebService;

        when(mockDatabase.caregiverDao()).thenReturn(mockDao);
    }

    @Test
    public void fetchCaregiversSuccess() {
        when(mockWebService.listCaregivers(anyString(), anyInt(), anyInt())).thenReturn(new Call<String>() {
            @Override
            public Response<String> execute() throws IOException {
                return Response.success("{}");
            }

            @Override
            public void enqueue(Callback<String> callback) {

            }

            @Override
            public boolean isExecuted() {
                return false;
            }

            @Override
            public void cancel() {

            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Override
            public Call<String> clone() {
                return null;
            }

            @Override
            public Request request() {
                return null;
            }
        });

        repository.fetchCaregivers(1, 10);

        // web service is called
        verify(mockWebService, times(1)).listCaregivers(anyString(), anyInt(), anyInt());

        // database is updated
        verify(mockDao).insertAll(anyList());
    }

    @Test
    public void fetchCaregiversFail() {
        when(mockWebService.listCaregivers(anyString(), anyInt(), anyInt())).thenReturn(null);

        repository.fetchCaregivers(1, 10);

        // web service is called
        verify(mockWebService, times(1)).listCaregivers(anyString(), anyInt(), anyInt());

        // database is not called
        verify(mockDao, never()).insertAll(anyList());
    }

    @Test
    public void getCaregiversSync() {
        repository.getCaregiversSync();
        verify(mockDao, times(1)).loadAllCaregiversSync();
    }

    @Test
    public void getCaregiver() {
        repository.getCaregiver("1");
        verify(mockDao, times(1)).loadCaregiver(anyString());
    }

    @Test
    public void getCaregiverAsync() {
        repository.getCaregiverAsync("1");
        verify(mockDao, times(1)).loadCaregiverSync(anyString());
    }

    @Test
    public void loadAllCaregiversAvailableForDateAndHour() {
        repository.loadAllCaregiversAvailableForDateAndHour(new Date(), 9);
        verify(mockDao, times(1)).loadAllCaregiversAvailableForDateAndHour(any(Date.class), anyInt());
    }

    @Test
    public void loadAllCaregiversAvailableForDateAndHourSync() {
        repository.loadAllCaregiversAvailableForDateAndHourSync(new Date(), 9);
        verify(mockDao, times(1)).loadAllCaregiversAvailableForDateAndHourSync(any(Date.class), anyInt());
    }

    @Test
    public void filterWithMaxSlotsInDateRangeSync() {
        repository.filterWithMaxSlotsInDateRangeSync(new Date(), new Date(), 5, new ArrayList<>());
        verify(mockDao, times(1)).filterWithMaxSlotsInDateRangeSync(any(Date.class), any(Date.class), anyInt(), anyList());
    }

    @Test
    public void filterWorkingInDateSync() {
        repository.filterWorkingInDateSync(new Date(), new ArrayList<>());
        verify(mockDao, times(1)).filterWorkingInDateSync(any(Date.class), anyList());
    }

    @Test
    public void filterLessWorkingInRangeSync() {
        repository.filterLessWorkingInRangeSync(new Date(), new Date(), new ArrayList<>());
        verify(mockDao, times(1)).filterLessWorkingInRangeSync(any(Date.class), any(Date.class), anyList());
    }
}
