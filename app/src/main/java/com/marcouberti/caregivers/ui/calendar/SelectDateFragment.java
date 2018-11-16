package com.marcouberti.caregivers.ui.calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import java.util.Calendar;
import java.util.Date;

import androidx.fragment.app.DialogFragment;

// Date picker
public class SelectDateFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener onDateSetListener;

    static DialogFragment getInstance(Date date, DatePickerDialog.OnDateSetListener onDateSetListener) {
        SelectDateFragment pickerFragment = new SelectDateFragment();
        pickerFragment.setOnDateSetListener(onDateSetListener);

        //Pass the date in a bundle.
        Bundle bundle = new Bundle();
        bundle.putSerializable("DATE", date);
        pickerFragment.setArguments(bundle);
        return pickerFragment;
    }

    private void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        this.onDateSetListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        Date initialDate = new Date();
        if(getArguments() != null) {
            initialDate = (Date) getArguments().getSerializable("DATE");
        }

        final Calendar c = Calendar.getInstance();
        c.setTime(initialDate);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), onDateSetListener, year, month, day);
        return dialog;
    }

}
