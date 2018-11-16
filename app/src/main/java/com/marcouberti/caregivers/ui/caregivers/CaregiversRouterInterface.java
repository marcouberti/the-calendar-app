package com.marcouberti.caregivers.ui.caregivers;

import android.content.Context;

import com.marcouberti.caregivers.model.Caregiver;

public interface CaregiversRouterInterface {
    void navigateToDetail(Context ctx, Caregiver c);
    void navigateBackWithSelection(Context ctx, Caregiver c);
}
