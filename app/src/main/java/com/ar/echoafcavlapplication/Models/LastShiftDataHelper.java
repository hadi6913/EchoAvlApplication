package com.ar.echoafcavlapplication.Models;

import java.util.Date;

public class LastShiftDataHelper {

    private Long cardSerial = 0L;
    private Integer validationCount = 0;
    private Date startShiftDate;

    public LastShiftDataHelper(Long cardSerial, Integer validationCount, Date startShiftDate) {
        this.cardSerial = cardSerial;
        this.validationCount = validationCount;
        this.startShiftDate = startShiftDate;
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
