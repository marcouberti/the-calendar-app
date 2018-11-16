package com.marcouberti.caregivers.ui.calendar;

import android.content.Context;
import android.content.Intent;

import com.marcouberti.caregivers.ui.slot.SlotActivity;

import java.util.Date;


public class CalendarRouter implements CalendarRouterInterface{

    @Override
    public void navigateToSlot(Context ctx, Date date, int hour, String room) {
        Intent intent = new Intent(ctx, SlotActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("DATE_TS", date.getTime());
        intent.putExtra("HOUR", hour);
        intent.putExtra("ROOM", room);
        ctx.startActivity(intent);
    }
}
