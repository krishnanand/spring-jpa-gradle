// Copyright 2018 Kartik Krishnanand. All Rights Reserved.
package com.iheartmedia.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Provides a error handling template that is inherited by all sub classes.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@EqualsAndHashCode
@ToString()
public abstract class IError {

    @Data
    @ToString
    @EqualsAndHashCode
    @Getter
    public static final class Error {
        private final int code;

        private final String message;

        public Error(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    private List<Error> errors;

    protected IError() {
        this.errors = new ArrayList<>();
    }

    /**
     * Returns a list of errors.
     */
    public List<Error> getErrors() {
        return this.errors;
    }

    /**
     * Adds error the list of errors.
     *
     * @param code error code
     * @param message error message
     */
    public void addError(ErrorCodes code,  String message) {
        this.errors.add(new Error(code.getCode(), message));
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }
}
