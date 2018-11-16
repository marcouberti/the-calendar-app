
package com.marcouberti.caregivers.db.dao;

import androidx.lifecycle.LiveData;

import com.marcouberti.caregivers.db.entity.CaregiverEntity;

import java.util.Date;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface CaregiverDao {
    @Query("SELECT * FROM caregivers")
    List<CaregiverEntity> loadAllCaregiversSync();

    @Query("select * from caregivers where id NOT IN "
         + "(select caregiverId from slots where date = :date AND hour = :hour )")
    LiveData<List<CaregiverEntity>> loadAllCaregiversAvailableForDateAndHour(Date date, int hour);

    @Query("select * from caregivers where id NOT IN "
            + "(select caregiverId from slots where date = :date AND hour = :hour )")
    List<CaregiverEntity> loadAllCaregiversAvailableForDateAndHourSync(Date date, int hour);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CaregiverEntity> caregivers);

    @Query("select * from caregivers where id = :caregiverId")
    LiveData<CaregiverEntity> loadCaregiver(String caregiverId);

    @Query("select * from caregivers where id = :caregiverId")
    CaregiverEntity loadCaregiverSync(String caregiverId);

    @Query("select * from caregivers as c where id IN(:ids) AND " +
            "(select count(*) from slots where caregiverId = c.id AND date BETWEEN :dateStart AND :dateEnd) < :maxSlotPerWeek")
    List<CaregiverEntity> filterWithMaxSlotsInDateRangeSync(Date dateStart, Date dateEnd, int maxSlotPerWeek, List<String> ids);

    @Query("select * from caregivers as c where id IN(:ids) AND " +
            "(select count(*) from slots where caregiverId = c.id AND date = :date) > 0")
    List<CaregiverEntity> filterWorkingInDateSync(Date date, List<String> ids);

    @Query("select * from caregivers as c where id IN(:ids) "
            + "ORDER BY (select count(*) from slots where caregiverId = c.id AND date BETWEEN :dateStart AND :dateEnd) ASC")
    List<CaregiverEntity> filterLessWorkingInRangeSync(Date dateStart, Date dateEnd, List<String> ids);
}
