package com.marcouberti.caregivers.ui.caregiver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.marcouberti.caregivers.R;

public class CaregiverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caregiver_activity);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Bundle args = new Bundle();
                args.putString("CAREGIVER_ID", extras.getString("CAREGIVER_ID"));

                CaregiverFragment fragment = CaregiverFragment.newInstance();
                fragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .commitNow();
            }
        }
    }
}
