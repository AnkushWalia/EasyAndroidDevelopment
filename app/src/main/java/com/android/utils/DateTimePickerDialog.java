package com.android.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by singhparamveer on 4/3/2018.
 * Utility class to show default date and time dialog and handle callback
 */

public class DateTimePickerDialog {
    private boolean showTimePicker = true;
    private boolean showDatePicker = true;
    private Date defaultDate = new Date();
    private DateTimeSetListener dateTimeSetListener;

    public void build(@NonNull final Context context, @NonNull DateTimeSetListener dateSetListener) {
        dateTimeSetListener = dateSetListener;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(defaultDate);
        if (showDatePicker) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(defaultDate);
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    defaultDate = calendar.getTime();
                    if (showTimePicker)
                        showTimePickerDialog(context);
                    else if (dateTimeSetListener != null)
                        dateTimeSetListener.onDateTimeSet(defaultDate);
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        } else if (showTimePicker) {
            showTimePickerDialog(context);
        }
    }

    private void showTimePickerDialog(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(defaultDate);
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(defaultDate);
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                defaultDate = calendar.getTime();
                if (dateTimeSetListener != null)
                    dateTimeSetListener.onDateTimeSet(defaultDate);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    public DateTimePickerDialog showTimePicker(boolean showTimePicker) {
        this.showTimePicker = showTimePicker;
        return this;
    }

    public DateTimePickerDialog showDatePicker(boolean showDatePicker) {
        this.showDatePicker = showDatePicker;
        return this;
    }

    public DateTimePickerDialog setDefaultDate(Date defaultDate) {
        this.defaultDate = defaultDate;
        return this;
    }

}

