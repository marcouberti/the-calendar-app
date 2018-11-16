
package com.marcouberti.caregivers.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marcouberti.caregivers.AppExecutors;
import com.marcouberti.caregivers.db.converter.DateNoTimeConverter;
import com.marcouberti.caregivers.db.dao.CaregiverDao;
import com.marcouberti.caregivers.db.dao.CalendarDao;
import com.marcouberti.caregivers.db.entity.CaregiverEntity;
import com.marcouberti.caregivers.db.entity.SlotEntity;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {CaregiverEntity.class, SlotEntity.class}, version = 1, exportSchema = false)
@TypeConverters(DateNoTimeConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;

    @VisibleForTesting
    public static final String DATABASE_NAME = "caregivers-db";

    public abstract CaregiverDao caregiverDao();
    public abstract CalendarDao calendarDao();

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static AppDatabase getInstance(final Context context, final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext(), executors);
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static AppDatabase buildDatabase(final Context appContext,
            final AppExecutors executors) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIO().execute(() -> {

                            AppDatabase database = AppDatabase.getInstance(appContext, executors);

                            // notify that the database was created and it's ready to be used
                            database.setDatabaseCreated();
                        });
                    }
                }).build();
    }

    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated(){
        mIsDatabaseCreated.postValue(true);
    }


    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }
}
