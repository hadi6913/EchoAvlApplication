package com.ar.echoafcavlapplication.Enums;

public enum StuffActivityType {
    OPEN_ADMIN_LINE_PAGE("open_admin_page", 1), OPEN_SHIFT("open_shift",
            2), OPEN_SUMMARY_REPORT("open_summary_report", 3), OPEN_HARDWARE_TEST("open_hardware_test", 4), EXIT_ADMIN_PAGE("exit_admin_page", 5), OPEN_MAINTENANCE("open_maintenance", 6) , CLOSE_SHIFT("close_shift", 7), ENTER_OPERATOR_PAGE("enter_operator_page", 8), EXIT_OPERATOR_PAGE("exit_operator_page", 9), ENTER_UPLOAD_DOWNLOAD_PAGE("enter_upload_download_page", 10), ENTER_REGISTER_PAGE("enter_register_page", 11), EXIT_DRIVER_PAGE("exit_driver_page", 12), ENTER_DRIVER_PAGE("enter_driver_page", 13);

    private String label;
    private int value;

    private StuffActivityType(String label, int value) {
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

    public static StuffActivityType activityTypeValueOf(String k) {
        for (StuffActivityType c : StuffActivityType.values()) {
            if (c.getLabel().equals(k)) {
                return c;
            }
        }
        return null;
    }

    public static StuffActivityType getByInteger(int val) {
        for (StuffActivityType c : StuffActivityType.values()) {
            if (c.getValue() == val) {
                return c;
            }
        }
        return null;
    }
}
