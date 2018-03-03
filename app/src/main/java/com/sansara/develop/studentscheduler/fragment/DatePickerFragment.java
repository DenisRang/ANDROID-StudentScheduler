package com.sansara.develop.studentscheduler.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import com.sansara.develop.studentscheduler.utils.DateTimeInterfase;
import com.sansara.develop.studentscheduler.utils.DateTimeUtils;

import java.util.Calendar;

/**
 * Created by den on 28.02.2018.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private static final String BUNDLE_IS_START_DATE = "BUNDLE_IS_START_DATE";

    public static DatePickerFragment newInstance(boolean isStartDate) {
        DatePickerFragment frag = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putBoolean(BUNDLE_IS_START_DATE, isStartDate);
        frag.setArguments(args);
        return frag;
    }

    public DatePickerFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.set(year, monthOfYear, dayOfMonth);

        EditText editText = null;
        if (getArguments().getBoolean(BUNDLE_IS_START_DATE))
            editText = ((DateTimeInterfase) getActivity()).getEditTextStartDate();
        else editText = ((DateTimeInterfase) getActivity()).getEditTextEndDate();

        editText.setText(DateTimeUtils.getDate(dateCalendar.getTimeInMillis()));
        editText.setContentDescription(String.valueOf(dateCalendar.getTimeInMillis()));
    }
}
