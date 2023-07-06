package dev.pattabiraman.utils.callback;

import java.util.Date;

 /**
  * The code is defining a Java interface called `OnCalenderFilter`.*/
public interface OnCalenderFilter {

    /**
     * The function onSelectDate takes a string representing a start date and a Date object
     * representing a chosen date as parameters.
     *
     * @param mStartDate A string representing the start date in a specific format.
     * @param chosenDate The chosenDate parameter is of type Date and represents the date that the user
     * has selected.
     */
    void onSelectDate(String mStartDate, Date chosenDate);
}
