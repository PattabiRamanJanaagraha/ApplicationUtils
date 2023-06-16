package dev.pattabiraman.utils.model;

import java.io.Serializable;
import java.util.Date;

public class DateRangeFilterModel implements Serializable {
    private String mDateString;
    private Date mDateObject;

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
