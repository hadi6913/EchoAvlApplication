package com.ar.echoafcavlapplication.Models;

public class Line {
    private Integer fareClassCode;
    private String lastStop;
    private String firstStop;
    private String label;
    private Integer lineCode;

    public Integer getFareClassCode() {
        return fareClassCode;
    }

    public void setFareClassCode(Integer fareClassCode) {
        this.fareClassCode = fareClassCode;
    }

    public String getLastStop() {
        return lastStop;
    }

    public void setLastStop(String lastStop) {
        this.lastStop = lastStop;
    }

    public String getFirstStop() {
        return firstStop;
    }

    public void setFirstStop(String firstStop) {
        this.firstStop = firstStop;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getLineCode() {
        return lineCode;
    }

    public void setLineCode(Integer lineCode) {
        this.lineCode = lineCode;
    }
}
