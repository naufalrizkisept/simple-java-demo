package com.example.constant;

public class GeneralConstant {
    private GeneralConstant() {
    }

    // TIME FORMATTER
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // STATUS CODE
    public static final String STATUS_CODE_SUCCESS = "200";
    public static final String STATUS_CODE_BAD_REQUEST = "400";
    public static final String STATUS_CODE_NOT_FOUND = "404";
    public static final String STATUS_CODE_SYSTEM_ERROR = "500";

    // STATUS MESSAGE
    public static final String STATUS_MESSAGE_SUCCESS = "Success";
    public static final String ERROR_REQUEST_MANDATORY = "Mandatory request required.";
    public static final String ERROR_DATA_EXIST = "There are existing values inside this data.";
    public static final String ERROR_SYSTEM_ERROR = "You just can't expect anything goes smoothly.";
    public static final String ERROR_DATA_DUPLICATE = "Data already exists.";
    public static final String ERROR_DATA_NOT_FOUND = "Data not found or already inactive.";
    public static final String ERROR_STRING_FORMAT = "String format does not match.";
    public static final String ERROR_ACCESS = "Access denied.";
}
