package com.marcouberti.caregivers.ui.calendar;

import android.content.Context;

import com.marcouberti.caregivers.model.Caregiver;

import java.util.Date;

public interface CalendarRouterInterface {
    void navigateToSlot(Context ctx, Date date, int hour, String room);
}
