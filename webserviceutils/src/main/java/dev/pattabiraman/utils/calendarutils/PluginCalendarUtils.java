/*
 * Created by Pattabi Raman on 30/05/23, 2:16 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 30/05/23, 2:16 PM
 */

package dev.pattabiraman.utils.calendarutils;

import android.app.DatePickerDialog;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dev.pattabiraman.utils.callback.OnCalenderFilter;

public class PluginCalendarUtils {
    private static PluginCalendarUtils mInstance;

    public static PluginCalendarUtils getInstance() {
        return mInstance == null ? mInstance = new PluginCalendarUtils() : mInstance;
    }

    /**
     * select start date from calender
     */
    public void onGetStartDateCalnder(final AppCompatActivity activity, final OnCalenderFilter onCalenderFilter) {
        DatePickerDialog mStartdatePickerDialog;
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDate = calendar.get(Calendar.DAY_OF_MONTH);
        mStartdatePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, final int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(0);
                cal.set(year, month, dayOfMonth, 0, 0, 0);
                getStringFormattedDate(cal, onCalenderFilter);
            }
        }, mYear, mMonth, mDate);
        mStartdatePickerDialog.show();
        /*below line is to restrict date picking till particular maximim date  */
//        mStartdatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        /**/
        mStartdatePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
    }

    /***
     * Get String formattted date
     */
    public String getStringFormattedDate(final Calendar cal, @Nullable final OnCalenderFilter onCalenderFilter) {
        String chosenDateString = "";
        try {
            Date chosenDate = cal.getTime();
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
            chosenDateString = dateFormat.format(chosenDate);
            if (onCalenderFilter != null) {
                onCalenderFilter.onSelectDate(chosenDateString, chosenDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chosenDateString;
    }

    /***
     * Get SimpleFormatDateString for Date objectisUserProfileValidate
     */

    public String getDateInRequiredFormat(final String requiredFormat, final Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(requiredFormat);
        return dateFormat.format(date);
    }

    /***
     * Get formatted date from string
     */
    public String getReadableDateStringForDateStampString(final String inputDateString, final String outputDateFormatString) {
        Date date = null;
        String outputDateString = "";
        try {
            SimpleDateFormat inputDateFormatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat outputDateFormatDate = new SimpleDateFormat(outputDateFormatString, Locale.US);
            date = inputDateFormatDate.parse(inputDateString);
            outputDateString = outputDateFormatDate.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            outputDateString = "Not Available";
        }
        return outputDateString;
    }

    public String getReadableDatefFromCustomDateStampString(final String inputDateString, final String outputDateFormatString) {
        Date date = null;
        String outputDateString = "";
        try {
            SimpleDateFormat inputDateFormatDate = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
            SimpleDateFormat outputDateFormatDate = new SimpleDateFormat(outputDateFormatString, Locale.US);
            date = inputDateFormatDate.parse(inputDateString);
            outputDateString = outputDateFormatDate.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDateString;
    }

    /***
     * Get formatted time from string
     */
    public String getReadableTimeStringForDateStampString(final String inputTimeString, final String outputTimeFormatString) {
        Date date = null;
        String outputDateString = "";
        try {
            SimpleDateFormat inputDateFormatDate = new SimpleDateFormat("HH:mm", Locale.US);
            SimpleDateFormat outputDateFormatDate = new SimpleDateFormat(outputTimeFormatString, Locale.US);
            date = inputDateFormatDate.parse(inputTimeString);
            outputDateString = outputDateFormatDate.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDateString;
    }

    /**
     * select end date from calender
     */
    public void onGetEndDateCalnder(final AppCompatActivity activity, final OnCalenderFilter onCalenderFilter) {
        DatePickerDialog mEnddatePickerDialog;
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDate = calendar.get(Calendar.DAY_OF_MONTH);
        mEnddatePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, final int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(0);
                cal.set(year, month, dayOfMonth, 0, 0, 0);
                getStringFormattedDate(cal, onCalenderFilter);
            }
        }, mYear, mMonth, mDate);
        mEnddatePickerDialog.show();
        mEnddatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }
}
