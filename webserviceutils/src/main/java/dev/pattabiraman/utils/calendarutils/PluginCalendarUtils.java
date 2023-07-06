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
     * select a date from calender.<br/>
     * The function `onGetStartDateCalnder` is a Java method that displays a DatePickerDialog to allow
     * the user to select a start date, and then calls a callback function with the selected date.
     *
     * @param activity The activity parameter is an instance of the AppCompatActivity class. It
     * represents the current activity in which the date picker dialog will be displayed.
     * @param onCalenderFilter The onCalenderFilter parameter is an interface that allows you to define
     * a callback method to handle the selected date. It can be implemented as follows:
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

    /**
     * Get String formattted date.<br/>
     * The function takes a Calendar object and an optional filter, formats the date in medium format
     * using US locale, and returns the formatted date as a string.
     *
     * @param cal The cal parameter is a Calendar object that represents a specific date and time. It
     * is used to retrieve the chosen date for formatting.
     * @param onCalenderFilter The `onCalenderFilter` parameter is an instance of the
     * `OnCalenderFilter` interface. It is used to provide a callback method `onSelectDate()` that can
     * be called when a date is selected. This allows the caller of the `getStringFormattedDate()`
     * method to perform additional actions
     * @return The method is returning a String value, which is the formatted date.
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

    /**
     * The function takes a date and a required date format as input and returns the date in the
     * specified format.
     *
     * @param requiredFormat A string representing the desired format for the date. For example,
     * "yyyy-MM-dd" would represent the format "year-month-day".
     * @param date The "date" parameter is a Date object that represents the date you want to format.
     * @return The method is returning a string representation of the given date in the required
     * format.
     */
    public String getDateInRequiredFormat(final String requiredFormat, final Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(requiredFormat);
        return dateFormat.format(date);
    }

    /**
     * Get formatted date from string
     * The function takes an input date string in the format "yyyy-MM-dd" and converts it to a readable
     * date string in the specified output date format.
     *
     * @param inputDateString The inputDateString parameter is a string representing a date in the
     * format "yyyy-MM-dd".
     * @param outputDateFormatString The outputDateFormatString parameter is a string that specifies
     * the desired format for the output date string. It should follow the syntax of the
     * SimpleDateFormat class in Java. For example, "dd/MM/yyyy" represents the format of
     * day/month/year.
     * @return The method is returning a string that represents the input date string in the specified
     * output date format.
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

    /**
     * The function takes a custom date stamp string and an output date format string as input, and
     * returns a readable date string in the specified output format.
     *
     * @param inputDateString The inputDateString parameter is a string representing a date in the
     * format "MMMM d, yyyy".
     * @param outputDateFormatString The outputDateFormatString parameter is a string that represents
     * the desired format for the output date. It should follow the syntax of the SimpleDateFormat
     * class in Java. For example, "yyyy-MM-dd" represents the format "year-month-day".
     * @return The method is returning a string representation of the input date in the specified
     * output date format.
     */
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

    /**
     * Get formatted time from string.<br/>
     * The function takes a time string in a specific format and converts it to a readable time string
     * in a specified output format.
     *
     * @param inputTimeString The inputTimeString parameter is a string representing a time in the
     * format "HH:mm".
     * @param outputTimeFormatString The outputTimeFormatString parameter is a string that specifies
     * the desired format for the output time string. It should follow the pattern of the
     * SimpleDateFormat class in Java. For example, "hh:mm a" represents the time in 12-hour format
     * with AM/PM indicator.
     * @return The method is returning a string representation of the input time string in the
     * specified output time format.
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
     * select end date from calender.<br/>
     * The function displays a DatePickerDialog to select an end date and calls a callback function
     * with the selected date.
     *
     * @param activity The activity parameter is an instance of the AppCompatActivity class. It
     * represents the current activity in which the method is being called. It is used to create the
     * DatePickerDialog and show it on the screen.
     * @param onCalenderFilter The parameter "onCalenderFilter" is an instance of the interface
     * "OnCalenderFilter". It is used to pass a callback function that will be executed after the date
     * is selected. The purpose of this callback function is to perform some action with the selected
     * date.
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
