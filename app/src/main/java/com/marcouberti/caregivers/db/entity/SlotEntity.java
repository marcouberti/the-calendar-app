package com.marcouberti.caregivers.db.entity;

import com.marcouberti.caregivers.model.Slot;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "slots",
        foreignKeys = {
            @ForeignKey(entity = CaregiverEntity.class,
                            parentColumns = "id",
                            childColumns = "caregiverId",
                            onDelete = ForeignKey.CASCADE)},
        indices = {@Index(value = {"caregiverId", "date", "hour"}, unique = true),
                   @Index(value = {"roomId", "date", "hour"}, unique = true)})
public class SlotEntity implements Slot {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    private String caregiverId;
    @NonNull
    private Date date;
    @NonNull
    private String roomId;
    @NonNull
    private Integer hour;
    @NonNull
    private String patientName;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public String getCaregiverId() {
        return caregiverId;
    }

    @Override
    public String getRoomId() {
        return roomId;
    }

    @Override
    public String getPatientName() {
        return patientName;
    }

    @Override
    public Integer getHour() {
        return hour;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCaregiverId(String caregiverId) {
        this.caregiverId = caregiverId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public void setPatientName(@NonNull String patientName) {
        this.patientName = patientName;
    }
}

