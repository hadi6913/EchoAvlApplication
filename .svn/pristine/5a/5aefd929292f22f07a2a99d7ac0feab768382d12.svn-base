package com.ar.echoafcavlapplication.Enums;

public enum LocalCalendarDateType {
    HOLIDAY("holiday", 1), WORKDAY("workday",
            2), WEEKEND("weekend", 3), RESERVE("reserve", 4);

    private String label;
    private int value;

    private LocalCalendarDateType(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static LocalCalendarDateType dateTypeValueOf(String k) {
        for (LocalCalendarDateType c : LocalCalendarDateType.values()) {
            if (c.getLabel().equals(k)) {
                return c;
            }
        }
        return null;
    }

    public static LocalCalendarDateType getByInteger(int val) {
        for (LocalCalendarDateType c : LocalCalendarDateType.values()) {
            if (c.getValue() == val) {
                return c;
            }
        }
        return null;
    }
}
