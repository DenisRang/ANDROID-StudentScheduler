package com.sansara.develop.studentscheduler.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.sansara.develop.studentscheduler.utils.DateTimeInterfase;
import com.sansara.develop.studentscheduler.utils.DateTimeUtils;

import java.util.Calendar;

/**
 * Created by den on 28.02.2018.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private static final String BUNDLE_IS_START_TIME = "BUNDLE_IS_START_TIME";

    public static TimePickerFragment newInstance(boolean isStartTime) {
        TimePickerFragment frag = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putBoolean(BUNDLE_IS_START_TIME, isStartTime);
        frag.setArguments(args);
        return frag;
    }

    public TimePickerFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.set(0, 0, 0, hourOfDay, minute);

        EditText editText = null;
        if (getArguments().getBoolean(BUNDLE_IS_START_TIME))
            editText = ((DateTimeInterfase) getActivity()).getEditTextStartTime();
        else editText = ((DateTimeInterfase) getActivity()).getEditTextEndTime();

        editText.setText(DateTimeUtils.getTime(timeCalendar.getTimeInMillis()));
        editText.setContentDescription(String.valueOf(timeCalendar.getTimeInMillis()));
    }

}