// Copyright 2018 Kartik Krishnanand. All Rights Reserved.
package com.iheartmedia.utils;

import com.iheartmedia.dto.GetStationMixin;
import com.iheartmedia.dto.StationMixin;
import com.iheartmedia.model.ErrorCodes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * General exception handler for the entire application.
 *
 * <p>An instance of this class handles exceptions and returns an appropriate payload response.
 */
@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

  @Autowired
  private MessageSource messageSource;

  /**
   * Handles all exceptions related to persistence operation.
   *
   * @param ex exception raised
   * @param request web request
   * @return response entity returning a 409 error code
   */
  @ExceptionHandler(value
      = { DataIntegrityViolationException.class})
  protected ResponseEntity<StationMixin> handleException(RuntimeException ex, WebRequest request) {
    StationMixin mixin = new GetStationMixin();
    mixin.addError(
        ErrorCodes.CONFLICT,
        this.messageSource.getMessage("station.save.error", null,
            ((ServletWebRequest)request).getLocale()));
    return new ResponseEntity<>(mixin, HttpStatus.CONFLICT);
  }
}
