
package com.marcouberti.caregivers.db.dao;
import com.marcouberti.caregivers.db.entity.SlotEntity;
import com.marcouberti.caregivers.db.entity.pojo.SlotCaregiver;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface CalendarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<SlotEntity> slots);

    @Query("select * from slots where date = :date")
    LiveData<List<SlotEntity>> loadDailySlots(Date date);

    @Query("select * from slots where date = :date")
    List<SlotEntity> loadDailySlotsSync(Date date);

    @Delete
    void deleteSlot(SlotEntity... slots);

    @Delete
    void deleteSlots(List<SlotEntity> slots);

    @Update
    void updateSlot(SlotEntity slot);

    @Query("SELECT "
            + "slots.id AS id, "
            + "slots.date AS date, "
            + "slots.roomId AS roomId, "
            + "slots.hour AS hour, "
            + "slots.patientName AS patientName, "
            + "caregivers.id AS caregiverId, "
            + "caregivers.name AS caregiverName, "
            + "caregivers.surname AS caregiverSurname, "
            + "caregivers.photo AS caregiverPhoto, "
            + "caregivers.thumbnail AS caregiverThumbnail "
            + "FROM slots, caregivers "
            + "WHERE slots.caregiverId = caregivers.id AND slots.date = :date")
    LiveData<List<SlotCaregiver>> loadDailySlotCaregivers(Date date);

    @Query("SELECT "
            + "slots.id AS id, "
            + "slots.date AS date, "
            + "slots.roomId AS roomId, "
            + "slots.hour AS hour, "
            + "slots.patientName AS patientName, "
            + "caregivers.id AS caregiverId, "
            + "caregivers.name AS caregiverName, "
            + "caregivers.surname AS caregiverSurname, "
            + "caregivers.photo AS caregiverPhoto, "
            + "caregivers.thumbnail AS caregiverThumbnail "
            + "FROM slots, caregivers "
            + "WHERE slots.caregiverId = caregivers.id AND slots.date = :date")
    List<SlotCaregiver> loadDailySlotCaregiversSync(Date date);

    @Query("SELECT "
            + "slots.id AS id, "
            + "slots.date AS date, "
            + "slots.roomId AS roomId, "
            + "slots.hour AS hour, "
            + "slots.patientName AS patientName, "
            + "caregivers.id AS caregiverId, "
            + "caregivers.name AS caregiverName, "
            + "caregivers.surname AS caregiverSurname, "
            + "caregivers.photo AS caregiverPhoto, "
            + "caregivers.thumbnail AS caregiverThumbnail "
            + "FROM slots, caregivers "
            + "WHERE slots.caregiverId = caregivers.id AND slots.date = :date "
            + "AND slots.hour = :hour "
            + "AND slots.roomId = :room")
    LiveData<SlotCaregiver> loadSlotCaregiver(Date date, int hour, String room);

    @Query("SELECT "
            + "slots.id AS id, "
            + "slots.date AS date, "
            + "slots.roomId AS roomId, "
            + "slots.hour AS hour, "
            + "slots.patientName AS patientName, "
            + "caregivers.id AS caregiverId, "
            + "caregivers.name AS caregiverName, "
            + "caregivers.surname AS caregiverSurname, "
            + "caregivers.photo AS caregiverPhoto, "
            + "caregivers.thumbnail AS caregiverThumbnail "
            + "FROM slots, caregivers "
            + "WHERE slots.caregiverId = caregivers.id AND slots.date = :date "
            + "AND slots.hour = :hour "
            + "AND slots.roomId = :room")
    SlotCaregiver loadSlotCaregiverSync(Date date, int hour, String room);

    @Query("select roomId from slots where date = :date AND hour = :hour")
    LiveData<List<String>> getBusyRooms(Date date, int hour);
}
