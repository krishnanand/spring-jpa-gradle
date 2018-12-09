// Copyright 2018 Kartik Krishnanand. All Rights Reserved.
package com.iheartmedia.model;

/**
 * Enum of supported error codes.
 */
public enum ErrorCodes {
    BAD_REQUEST(400),
    NOT_FOUND(404),
    CONFLICT(409);

    private final int code;

    private ErrorCodes(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
