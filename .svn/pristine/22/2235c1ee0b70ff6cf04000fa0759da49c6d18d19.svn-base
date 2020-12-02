package com.ar.echoafcavlapplication.Models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LastShiftData extends RealmObject {

    @PrimaryKey
    long id = 0;
    private Long cardSerial = 0L;
    private Integer validationCount = 0;
    private Date startShiftDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LastShiftData(Long cardSerial, Integer validationCount, Date startShiftDate) {
        this.cardSerial = cardSerial;
        this.validationCount = validationCount;
        this.startShiftDate = startShiftDate;
    }

    public LastShiftData() {
    }

    public Long getCardSerial() {
        return cardSerial;
    }

    public void setCardSerial(Long cardSerial) {
        this.cardSerial = cardSerial;
    }

    public Integer getValidationCount() {
        return validationCount;
    }

    public void setValidationCount(Integer validationCount) {
        this.validationCount = validationCount;
    }

    public Date getStartShiftDate() {
        return startShiftDate;
    }

    public void setStartShiftDate(Date startShiftDate) {
        this.startShiftDate = startShiftDate;
    }
}
