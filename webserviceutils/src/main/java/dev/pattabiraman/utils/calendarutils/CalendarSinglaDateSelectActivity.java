/*
 * Created by Pattabi Raman on 30/05/23, 3:06 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 30/05/23, 3:06 PM
 */

package dev.pattabiraman.utils.calendarutils;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;
import java.util.Objects;

import dev.pattabiraman.utils.PluginAppUtils;
import dev.pattabiraman.utils.callback.OnCalenderFilter;
import dev.pattabiraman.utils.model.DateRangeFilterModel;
import dev.pattabiraman.utils.permissionutils.PluginBaseAppCompatActivity;
import dev.pattabiraman.webserviceutils.databinding.ActivityCalendarSelectADateBinding;

public class CalendarSinglaDateSelectActivity extends PluginBaseAppCompatActivity {
    private AppCompatActivity mActivity;
    private ActivityCalendarSelectADateBinding binding;
    private Date chosenStartDate, chosenEndDate;
    private int requestCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarSelectADateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mActivity = CalendarSinglaDateSelectActivity.this;
        requestCode = getIntent().getExtras().getInt("requestCode");

        binding.ivBack.setOnClickListener(v -> {
            activity.finish();
        });

        binding.startDate.setOnClickListener(v -> {
            PluginCalendarUtils.getInstance().onGetStartDateCalnder(activity, new OnCalenderFilter() {
                @Override
                public void onSelectDate(String mStart_date, Date chosenDate) {
                    binding.startDate.setText(mStart_date);
                    chosenStartDate = chosenDate;
                }
            });
        });
        binding.endDate.setOnClickListener(v -> {
            PluginCalendarUtils.getInstance().onGetEndDateCalnder(mActivity, new OnCalenderFilter() {
                @Override
                public void onSelectDate(String mEnd_date, Date chosenDate) {
                    binding.endDate.setText(mEnd_date);
                    chosenEndDate = chosenDate;
                }
            });
        });

        binding.submitCalendarFilter.setOnClickListener(v -> {
            if (onValidation()) {
//            Date Range Validation for start and end
                PluginCalendarConstants.getInstance().mSelectedStartDateModel = new DateRangeFilterModel(Objects.requireNonNull(binding.startDate.getText()).toString(), Objects.requireNonNull(binding.endDate.getText()).toString(), chosenStartDate, chosenEndDate);
                Intent onActivityResultIntent = new Intent();
                onActivityResultIntent.putExtra("result", PluginCalendarConstants.getInstance().mSelectedStartDateModel);
                setResult(RESULT_OK, onActivityResultIntent);
                mActivity.finish();
            }
        });
    }

    public boolean onValidation() {
        if (binding.startDate.getText().toString().equalsIgnoreCase("start date")) {
            PluginAppUtils.getInstance(activity).showToast(mActivity, "Select a date");
            return false;
        }

        return true;
    }
}
