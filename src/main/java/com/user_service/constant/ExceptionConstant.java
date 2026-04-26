package com.user_service.constant;

public final class ExceptionConstant {

    public static final String USER_NOT_FOUND_BY_ID = "User not found by ID: ";
    public static final String USER_NOT_FOUND_BY_USERNAME = "User not found by username: ";

    public static final String USERNAME_IS_ALREADY_EXIST = "Username is already exist: ";

    public static final String EMAIL_IS_ALREADY_EXIST = "Email is already exist: ";

    public static final String EMAIL_CONFIRMATION_NOT_FOUND_BY_TOKEN = "Email confirmation not found by token: ";

    public static final String EMAIL_CONFIRMATION_TOKEN_HAD_BEEN_EXPIRED = "Email confirmation token had been expired. An email with a new confirmation link was resent to the email address: ";

    public static final String EMAIL_ALREADY_ACTIVATED = "Email is already activated.";

    private ExceptionConstant() {
    }
}
