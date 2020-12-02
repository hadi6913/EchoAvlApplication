package com.ar.echoafcavlapplication.Enums;

public enum  RegisterStatus {
    REGISTERED_CONFIRMED("reged_confed" , 1) ,REGISTERED_NOT_CONFIRMED("reged_n_confed" , 2) ,NOT_REGISTERED("n_reged" , 3);

    private String label;
    private int value;

    private RegisterStatus(String label, int value) {
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

    public static RegisterStatus registerStatusLabelOf(String k) {
        for (RegisterStatus c : RegisterStatus.values()) {
            if (c.getLabel().equals(k)) {
                return c;
            }
        }
        return null;
    }

    public static RegisterStatus registerStatusValueOf(int value) {
        for (RegisterStatus c : RegisterStatus.values()) {
            if (c.getValue() == value) {
                return c;
            }
        }
        return null;
    }
}


