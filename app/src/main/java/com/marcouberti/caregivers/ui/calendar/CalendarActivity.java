package com.marcouberti.caregivers.ui.calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.marcouberti.caregivers.R;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, CalendarFragment.newInstance())
                    .commitNow();
        }
    }
}
