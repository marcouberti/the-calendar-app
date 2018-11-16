package com.marcouberti.caregivers.ui.caregivers;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.marcouberti.caregivers.R;

public class CaregiversActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caregivers_activity);
        if (savedInstanceState == null) {

            CaregiversFragment fragment = CaregiversFragment.newInstance();
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                fragment.setArguments(extras);
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commitNow();
        }
    }
}
