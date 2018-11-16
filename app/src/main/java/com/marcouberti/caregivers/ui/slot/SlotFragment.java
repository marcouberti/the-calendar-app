package com.marcouberti.caregivers.ui.slot;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.marcouberti.caregivers.R;
import com.marcouberti.caregivers.util.Constants;
import com.marcouberti.caregivers.util.DateUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;


public class SlotFragment extends Fragment {

    private SlotViewModel mViewModel;
    private SlotRouter router = new SlotRouter();

    private Toolbar toolbar;
    private TextView room, time;
    private EditText patient;
    private ImageView photo;
    private CollapsingToolbarLayout collapsingToolbar;
    private FloatingActionButton fabSave, fabDelete;
    private boolean skipOnTextChange = false;

    public static SlotFragment newInstance() {
        return new SlotFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.slot_fragment, container, false);

        toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_prev_arrow);
        toolbar.setNavigationOnClickListener(view -> {
            if(getActivity()!=null && isAdded()) getActivity().finish();
        });
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        collapsingToolbar = rootView.findViewById(R.id.collapsing_toolbar);

        fabSave = rootView.findViewById(R.id.fab_save);
        fabDelete = rootView.findViewById(R.id.fab_delete);

        fabSave.setOnClickListener(v ->  mViewModel.onTapSave());
        fabDelete.setOnClickListener(v -> mViewModel.onTapDelete());

        room = rootView.findViewById(R.id.room);
        time = rootView.findViewById(R.id.time);
        patient = rootView.findViewById(R.id.patient);
        photo = rootView.findViewById(R.id.photo);

        room.setOnClickListener(v -> navigateToRoomSelection());
        photo.setOnClickListener(v -> navigateToCaregiverSelection());
        patient.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                skipOnTextChange = true;
                mViewModel.onPatientNameChanged(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return rootView;
    }

    private void navigateToCaregiverSelection() {
        router.navigateToCaregiverSelection(getActivity(),
                Objects.requireNonNull(mViewModel.getSlot().getValue()).date,
                mViewModel.getSlot().getValue().hour);
    }

    private void navigateToRoomSelection() {
        router.navigateToRoomSelection(getActivity(),
                Objects.requireNonNull(mViewModel.getSlot().getValue()).date,
                mViewModel.getSlot().getValue().hour);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SlotViewModel.class);

        if(savedInstanceState == null) {
            long ts = getArguments().getLong("DATE_TS");
            Date date = new Date(ts);
            int hour = getArguments().getInt("HOUR");
            String room = getArguments().getString("ROOM");

            mViewModel.setStartingParams(date, hour, room);
        }

        mViewModel.getSlot().observe(this, slot -> {
            // update UI
            if(slot == null) return;
            if(slot.caregiverName == null) collapsingToolbar.setTitle(getString(R.string.select_a_caregiver));
            else collapsingToolbar.setTitle(slot.caregiverName + " " +slot.caregiverSurname);
            if(slot.caregiverPhoto != null) {
                Glide.with(getContext())
                        .load(slot.caregiverPhoto)
                        //.apply(RequestOptions.circleCropTransform())
                        .into(photo);
            }else photo.setImageDrawable(null);
            if(slot.patientName != null) {
                if(skipOnTextChange) {
                    skipOnTextChange = false;
                }else {
                    patient.setText(slot.patientName);
                    patient.setSelection(patient.getText().length());
                }
            }
            if(slot.roomId != null) room.setText(getString(R.string.room)+": "+slot.roomId);
            if(slot.date != null) {
                time.setText(String.format("%s %s",
                        DateUtils.prettyDate(getContext(),
                        slot.date, DateFormat.MEDIUM),
                        DateUtils.formatLocalTime(getContext(), slot.hour)));
            }
        });

        mViewModel.isEditing().observe(this, editing -> {
            if(editing) fabDelete.show();
            else fabDelete.hide();
        });

        mViewModel.canSave().observe(this, canSave -> {
            if(canSave) fabSave.show();
            else fabSave.hide();
        });

        mViewModel.getExitEvent().observe(this, exit -> {
            if(getActivity() != null && isAdded()) getActivity().finish();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.PICK_CAREGIVER) {
            if(resultCode == Activity.RESULT_OK && data != null){
                String caregiverId=data.getStringExtra("result");
                mViewModel.onCaregiverChanged(caregiverId);
            }
        }else if (requestCode == Constants.PICK_ROOM) {
            if(resultCode == Activity.RESULT_OK && data != null){
                String roomId=data.getStringExtra("result");
                mViewModel.onRoomChanged(roomId);
            }
        }
    }
}
