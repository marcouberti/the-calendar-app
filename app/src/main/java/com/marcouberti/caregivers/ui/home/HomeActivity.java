package com.marcouberti.caregivers.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.marcouberti.caregivers.R;
import com.marcouberti.caregivers.ui.home.HomeFragment;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, HomeFragment.newInstance())
                    .commitNow();
        }
    }
}
