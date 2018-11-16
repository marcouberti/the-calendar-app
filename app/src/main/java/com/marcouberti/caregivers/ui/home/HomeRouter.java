package com.marcouberti.caregivers.ui.home;

import android.content.Context;
import android.content.Intent;

import com.marcouberti.caregivers.ui.caregivers.CaregiversActivity;
import com.marcouberti.caregivers.ui.calendar.CalendarActivity;

public class HomeRouter implements HomeRouterInterface{

    @Override
    public void navigateToCaregivers(Context ctx) {
        Intent intent = new Intent(ctx, CaregiversActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(intent);
    }

    @Override
    public void navigateToCalendar(Context ctx) {
        Intent intent = new Intent(ctx, CalendarActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(intent);
    }
}
