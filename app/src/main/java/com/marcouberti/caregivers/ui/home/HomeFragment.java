package com.marcouberti.caregivers.ui.home;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.marcouberti.caregivers.R;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    private HomeRouter router = new HomeRouter();

    private Button caregiversButton, calendarButton;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.home_fragment, container, false);

        caregiversButton = rootView.findViewById(R.id.caregivers_button);
        calendarButton = rootView.findViewById(R.id.calendar_button);

        caregiversButton.setOnClickListener(v -> router.navigateToCaregivers(getContext()));
        calendarButton.setOnClickListener(v -> router.navigateToCalendar(getContext()));
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
    }

}
