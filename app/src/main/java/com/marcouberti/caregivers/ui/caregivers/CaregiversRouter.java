package com.marcouberti.caregivers.ui.caregivers;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.marcouberti.caregivers.model.Caregiver;
import com.marcouberti.caregivers.ui.caregiver.CaregiverActivity;

import androidx.fragment.app.FragmentActivity;

public class CaregiversRouter implements CaregiversRouterInterface{

    @Override
    public void navigateToDetail(Context ctx, Caregiver c) {
        Intent intent = new Intent(ctx, CaregiverActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("CAREGIVER_ID", c.getId());
        if(ctx instanceof FragmentActivity) {
            ctx.startActivity(intent);
        }
    }

    @Override
    public void navigateBackWithSelection(Context ctx, Caregiver c) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", c.getId());
        if(ctx instanceof FragmentActivity) {
            ((FragmentActivity)ctx).setResult(Activity.RESULT_OK, returnIntent);
            ((FragmentActivity)ctx).finish();
        }
    }
}
