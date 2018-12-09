// Copyright 2018 Kartik Krishnanand. All Rights Reserved.
package com.iheartmedia.utils;

import com.iheartmedia.dto.StationMixin;
import com.iheartmedia.dto.UpdateStationMixin;
import com.iheartmedia.model.ErrorCodes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import java.util.Locale;

/**
 * Validates the mixin object.
 */
@Component
public class StationMixinValidator implements Validator {

  @Autowired
  private MessageSource messageSource;


  @Override public boolean supports(Class<?> clazz) {
    return StationMixin.class.isAssignableFrom(clazz);
  }

  /**
   * Applies the validation condition listed below, but empty or null fields are simply ignored.
   *
   * <p>
   *   <ul>
   *     <li>1. Call sign  length must be 4 characters </li>
   *   </ul>
   * </p>
   *
   * @param target object to be valdiated
   * @param errors error object
   */
  @Override public void validate(Object target, Errors errors) {
    StationMixin mixin = (StationMixin) target;
    if (mixin.getCallSign() != null && mixin.getCallSign().length() != 4) {
      errors.rejectValue("callSign", "callsign.size.invalid", new Object[] {mixin.getCallSign()},
          null); ;
    }
  }

  /**
   * Returns a list of errors
   *
   * @param errors error objects
   * @return mixin object
   */
  public StationMixin createMixin(Errors errors, Locale locale) {
    StationMixin mixin = new UpdateStationMixin();
    for (FieldError fieldError : errors.getFieldErrors()) {
      mixin.addError(ErrorCodes.BAD_REQUEST, this.messageSource.getMessage(fieldError.getCode(),
          fieldError.getArguments(), locale));
    }
    return mixin;
  }
}
