package com.marcouberti.caregivers.ui.rooms;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.marcouberti.caregivers.R;
import com.marcouberti.caregivers.ui.caregivers.CaregiversFragment;
import com.marcouberti.caregivers.ui.rooms.RoomsFragment;

public class RoomsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rooms_activity);
        if (savedInstanceState == null) {

            RoomsFragment fragment = RoomsFragment.newInstance();
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
