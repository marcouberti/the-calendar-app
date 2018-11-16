package com.marcouberti.caregivers.model;

import java.util.Date;

public interface Slot {
    Integer getId();
    Date getDate();
    String getCaregiverId();
    String getRoomId();
    String getPatientName();
    Integer getHour();
}
