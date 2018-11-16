package com.marcouberti.caregivers.db.entity;

import com.marcouberti.caregivers.model.Caregiver;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "caregivers")
public class CaregiverEntity implements Caregiver {
    @PrimaryKey
    @NonNull
    private String id;
    private String name, surname, photo, thumbnail;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    @Override
    public String getPhoto() {
        return photo;
    }

    @Override
    public String getThumbnail() {
        return thumbnail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
