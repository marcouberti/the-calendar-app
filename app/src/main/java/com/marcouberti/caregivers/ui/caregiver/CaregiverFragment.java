package com.marcouberti.caregivers.ui.caregiver;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.marcouberti.caregivers.R;

public class CaregiverFragment extends Fragment {

    private CaregiverViewModel mViewModel;

    private ImageView photo;
    private TextView name, surname;
    private CollapsingToolbarLayout collapsingToolbar;

    public static CaregiverFragment newInstance() {
        return new CaregiverFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.caregiver_fragment, container, false);

        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_prev_arrow);
        toolbar.setNavigationOnClickListener(view -> {
            if(getActivity()!=null && isAdded()) getActivity().finish();
        });
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        collapsingToolbar = rootView.findViewById(R.id.collapsing_toolbar);

        photo = rootView.findViewById(R.id.photo);
        name = rootView.findViewById(R.id.name);
        surname = rootView.findViewById(R.id.surname);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CaregiverViewModel.Factory factory = new CaregiverViewModel.Factory(
                getActivity().getApplication(), getArguments().getString("CAREGIVER_ID"));

        mViewModel = ViewModelProviders.of(this, factory)
                .get(CaregiverViewModel.class);

        mViewModel.getCaregiver().observe(this, caregiver -> {
            // update UI
            if(caregiver == null) return;
            collapsingToolbar.setTitle(caregiver.getName() + " " + caregiver.getSurname());
            name.setText(caregiver.getName());
            surname.setText(caregiver.getSurname());
            Glide.with(getContext())
                    .load(caregiver.getPhoto())
                    //.apply(RequestOptions.circleCropTransform())
                    .into(photo);
        });
    }

}
