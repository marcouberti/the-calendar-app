package com.marcouberti.caregivers.ui.slot;

import android.content.Context;

import java.util.Date;

public interface SlotRouterInterface {
    void navigateToCaregiverSelection(Context ctx, Date date, int hour);
    void navigateToRoomSelection(Context ctx, Date date, int hour);
}
