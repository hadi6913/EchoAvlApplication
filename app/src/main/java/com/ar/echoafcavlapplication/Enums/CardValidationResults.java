package com.ar.echoafcavlapplication.Enums;

public enum CardValidationResults {
    SUCCESS,
    FAIL,
    BYPASS_TIME,
    READ_CARD_INFO_ERROR,
    INSUFFICIENT_BALANCE,
    INSUFFICIENT_BALANCE_CHECK_CARD,
    CALCULATE_FARE_ERROR,
    PROBLEM_WHILE_VALIDATING_CREW_CARD,
    NOT_A_CREW_CARD,
    CLOSE_SHIFT_FIRST,
    INVALID_DRIVER
}