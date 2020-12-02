package com.ar.echoafcavlapplication.Models;

public class Bus {
    private Long IMEI;
    private Long code;

    public Bus(Long IMEI, Long code) {
        this.IMEI = IMEI;
        this.code = code;
    }

    public Long getIMEI() {
        return IMEI;
    }

    public void setIMEI(Long IMEI) {
        this.IMEI = IMEI;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }
}
