package com.ar.echoafcavlapplication.Models;

import com.ar.echoafcavlapplication.Enums.LocalCalendarDateType;

import java.util.Date;

public class LocalCalendar {
    private Date date;
    private Integer day;
    private Integer month;
    private Integer year;
    private LocalCalendarDateType dateType;

    public LocalCalendar() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public LocalCalendarDateType getDateType() {
        return dateType;
    }

    public void setDateType(LocalCalendarDateType dateType) {
        this.dateType = dateType;
    }
}
