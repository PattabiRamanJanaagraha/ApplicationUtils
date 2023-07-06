/*
 * Created by Pattabi Raman on 30/05/23, 2:16 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 30/05/23, 2:16 PM
 */

package dev.pattabiraman.utils.calendarutils;

import dev.pattabiraman.utils.model.DateRangeFilterModel;

public class PluginCalendarConstants {
    private static PluginCalendarConstants mInstance;
    public DateRangeFilterModel mSelectedDateModel;

    /**
     * The getInstance() function returns an instance of the PluginCalendarConstants class, creating a
     * new instance if one does not already exist.
     *
     * @return The method is returning an instance of the PluginCalendarConstants class.
     */
    public static PluginCalendarConstants getInstance() {
        return mInstance == null ? mInstance = new PluginCalendarConstants() : mInstance;
    }
}
