package com.dolphin.adminbackend.constants;

public enum CustomAPICode {

    MISSING_TOKEN("No token was provided in the request."),
    TOKEN_EXPIRED("The token provided has exceeded its limited time validity");

    private final String message;

    CustomAPICode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
