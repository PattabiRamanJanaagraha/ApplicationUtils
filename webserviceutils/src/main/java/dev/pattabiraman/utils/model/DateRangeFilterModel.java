package dev.pattabiraman.utils.model;

import java.io.Serializable;
import java.util.Date;

 /**
  * The `DateRangeFilterModel` class is a serializable Java class that represents a date range filter,
  * with properties for a date string and a date object.
  */
 public class DateRangeFilterModel implements Serializable {
    private String mDateString;
    private Date mDateObject;

/**
 *  The `public DateRangeFilterModel(String mStarteDate, Date chosenStartDate)` constructor is
     initializing a new instance of the `DateRangeFilterModel` class. It takes two parameters:
     `mStarteDate` of type `String` and `chosenStartDate` of type `Date`.*/
    public DateRangeFilterModel(String mStarteDate, Date chosenStartDate) {
        setmDateString(mStarteDate);
        setmDateObject(chosenStartDate);
    }

    public Date getmDateObject() {
        return mDateObject;
    }

    public void setmDateObject(Date mDateObject) {
        this.mDateObject = mDateObject;
    }

    public Date getChosenDate() {
        return mDateObject;
    }

    public void setChosenDate(Date chosenDate) {
        this.mDateObject = chosenDate;
    }

    public String getmDateString() {
        return mDateString;
    }

    public void setmDateString(String mDateString) {
        this.mDateString = mDateString;
    }

    
}
