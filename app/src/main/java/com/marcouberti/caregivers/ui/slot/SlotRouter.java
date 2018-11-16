package com.marcouberti.caregivers.ui.slot;


import android.content.Context;
import android.content.Intent;

import com.marcouberti.caregivers.ui.caregivers.CaregiversActivity;
import com.marcouberti.caregivers.ui.rooms.RoomsActivity;
import com.marcouberti.caregivers.util.Constants;

import java.util.Date;

import androidx.fragment.app.FragmentActivity;

public class SlotRouter implements SlotRouterInterface{

    @Override
    public void navigateToCaregiverSelection(Context ctx, Date date, int hour) {
        Intent intent = new Intent(ctx, CaregiversActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("DATE_TS", date.getTime());
        intent.putExtra("HOUR", hour);
        if(ctx instanceof FragmentActivity) {
            ((FragmentActivity)ctx).startActivityForResult(intent, Constants.PICK_CAREGIVER);
        }
    }

    @Override
    public void navigateToRoomSelection(Context ctx, Date date, int hour) {
        Intent intent = new Intent(ctx, RoomsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("DATE_TS", date.getTime());
        intent.putExtra("HOUR", hour);
        if(ctx instanceof FragmentActivity) {
            ((FragmentActivity)ctx).startActivityForResult(intent, Constants.PICK_ROOM);
        }
    }
}
