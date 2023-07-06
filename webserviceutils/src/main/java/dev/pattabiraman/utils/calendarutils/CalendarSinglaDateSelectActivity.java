/*
 * Created by Pattabi Raman on 30/05/23, 3:06 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 30/05/23, 3:06 PM
 */

package dev.pattabiraman.utils.calendarutils;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import dev.pattabiraman.utils.AppHelperMethods;
import dev.pattabiraman.utils.PluginAppUtils;
import dev.pattabiraman.utils.model.DateRangeFilterModel;
import dev.pattabiraman.utils.permissionutils.PluginBaseAppCompatActivity;
import dev.pattabiraman.webserviceutils.databinding.ActivityCalendarSelectADateBinding;

/**
 * @author Pattabi
 * @apiNote Must send intent.putExtra("isToSelectTime",Boolean)
 */
public class CalendarSinglaDateSelectActivity extends PluginBaseAppCompatActivity {
    private AppCompatActivity mActivity;
    private ActivityCalendarSelectADateBinding binding;
    private Date chosenDate;
    private int requestCode;
    private boolean isToSelectTime = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarSelectADateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mActivity = CalendarSinglaDateSelectActivity.this;
        requestCode = getIntent().getExtras().getInt("requestCode");

        /*is time picker to be configured */
        try {
            isToSelectTime = getIntent().getExtras().getBoolean("isToSelectTime");
        } catch (Exception e) {
            isToSelectTime = false;
        }
        binding.etDate.setText("");


        binding.ivBack.setOnClickListener(v -> {
            activity.finish();
        });

        binding.etDate.setOnClickListener(v -> {
            PluginCalendarUtils.getInstance().onGetStartDateCalnder(activity, (mStart_date, chosenDate) -> {
                PluginCalendarConstants.getInstance().mSelectedDateModel = new DateRangeFilterModel(Objects.requireNonNull(binding.etDate.getText()).toString(), chosenDate);
                CalendarSinglaDateSelectActivity.this.chosenDate = chosenDate;
                if (!isToSelectTime) {
                    PluginCalendarConstants.getInstance().mSelectedDateModel.setmDateString(mStart_date);
                    binding.etDate.setText(mStart_date);
                } else {
                    showTimePickerDialog(PluginCalendarConstants.getInstance().mSelectedDateModel);
                }
            });
        });

        binding.etDate.performClick();

        binding.submitCalendarFilter.setOnClickListener(v -> {
            if (onValidation()) {
                Intent onActivityResultIntent = new Intent();
                onActivityResultIntent.putExtra("result", PluginCalendarConstants.getInstance().mSelectedDateModel);
                setResult(RESULT_OK, onActivityResultIntent);
                mActivity.finish();
            }
        });
    }

    /**
     * The function checks if a date is selected and displays a toast message if it is not.
     * @return is selected date valid
     */
    public boolean onValidation() {
        if (TextUtils.isEmpty(binding.etDate.getText().toString())) {
            PluginAppUtils.getInstance(activity).showToast(mActivity, "Select a date");
            return false;
        }
        return true;
    }

    /**
     * The function `showTimePickerDialog` displays a TimePickerDialog and allows the user to select a
     * time, which is then used to update the chosenDate and mSelectedDateModel variables.
     *
     * @param mSelectDateModel The `mSelectDateModel` parameter is an instance of the
     * `DateRangeFilterModel` class. It is used to store and manipulate the selected date and time
     * values.
     */
    private void showTimePickerDialog(DateRangeFilterModel mSelectDateModel) {
        // Get the TimePicker view
        TimePicker timePicker = new TimePicker(activity);
        // Create a new TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            // Do something with the time that was selected
            AppHelperMethods.getInstance(activity).traceLog("TimePicker", "Time selected: " + hourOfDay + ":" + minute);
            Calendar selectedDateTime = Calendar.getInstance();
            selectedDateTime.setTime(mSelectDateModel.getmDateObject());
            selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDateTime.set(Calendar.MINUTE, minute);
            selectedDateTime.set(Calendar.SECOND, 0);

            chosenDate = new Date(selectedDateTime.getTimeInMillis());
            PluginCalendarConstants.getInstance().mSelectedDateModel.setChosenDate(chosenDate);

            final String selectedDateTimeString = PluginAppUtils.getInstance(activity).getDate(selectedDateTime.getTimeInMillis(), "dd MMMM yyyy") + " at " + PluginAppUtils.getInstance(activity).getDate(selectedDateTime.getTimeInMillis(), "hh:mm a");
            PluginCalendarConstants.getInstance().mSelectedDateModel.setmDateString(selectedDateTimeString);
            binding.etDate.setText(selectedDateTimeString);

        }, timePicker.getCurrentHour(), timePicker.getCurrentMinute(), false);

        // Show the TimePickerDialog
        timePickerDialog.show();
    }
}
