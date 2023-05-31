package dev.pattabiraman.utils.model;

import java.io.Serializable;
import java.util.Date;

public class DateRangeFilterModel implements Serializable {
    private String mStarteDate;
    private String mEndDate;
    private Date chosenStartDate;
    private Date chosenEndDate;
    private int differenceInMonths;

    public DateRangeFilterModel(String mStarteDate, String mEndDate, Date chosenStartDate, Date chosenEndDate){
        setmStarteDate(mStarteDate);
        setmEndDate(mEndDate);
        setChosenStartDate(chosenStartDate);
        setChosenEndDate(chosenEndDate);
    }

    public Date getChosenStartDate() {
        return chosenStartDate;
    }

    public void setChosenStartDate(Date chosenStartDate) {
        this.chosenStartDate = chosenStartDate;
    }

    public Date getChosenEndDate() {
        return chosenEndDate;
    }

    public void setChosenEndDate(Date chosenEndDate) {
        this.chosenEndDate = chosenEndDate;
    }

    public Date getChosenDate() {
        return chosenStartDate;
    }

    public void setChosenDate(Date chosenDate) {
        this.chosenStartDate = chosenDate;
    }

    public String getmStarteDate() {
        return mStarteDate;
    }

    public void setmStarteDate(String mStarteDate) {
        this.mStarteDate = mStarteDate;
    }

    public String getmEndDate() {
        return mEndDate;
    }

    public void setmEndDate(String mEndDate) {
        this.mEndDate = mEndDate;
    }

    public int getDifferenceInMonths() {
        return differenceInMonths;
    }

    public void setDifferenceInMonths(int differenceInMonths) {
        this.differenceInMonths = differenceInMonths;
    }
}
