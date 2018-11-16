package com.marcouberti.caregivers.ui.slot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.marcouberti.caregivers.R;

public class SlotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slot_activity);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Bundle args = new Bundle();
                    args.putLong("DATE_TS", extras.getLong("DATE_TS"));
                    args.putInt("HOUR", extras.getInt("HOUR"));
                    args.putString("ROOM", extras.getString("ROOM"));

                SlotFragment fragment = SlotFragment.newInstance();
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .commitNow();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        getSupportFragmentManager().findFragmentById(R.id.container).onActivityResult(requestCode, resultCode, data);
    }
}
