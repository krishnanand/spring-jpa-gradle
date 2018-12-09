// Copyright 2018 Kartik Krishnanand. All Rights Reserved.
package com.iheartmedia.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.iheartmedia.model.ErrorCodes;
import com.iheartmedia.model.IError;
import com.iheartmedia.model.Station;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

/**
 * A value object that encapsulates the outcome of create, delete, and update operations.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@EqualsAndHashCode(callSuper = true, exclude = "id")
@ToString(callSuper = true)
@Setter(AccessLevel.NONE)
public abstract class StationMixin extends IError {

  @JsonIgnore
  private Long id;

  private String stationId;

  @JsonProperty("name")
  private String stationName;

  private Boolean hdEnabled;

  private String callSign;

  protected StationMixin() {}

  private StationMixin withStationId(String stationId) {
    this.stationId = stationId;
    return this;
  }

  private StationMixin withStationName(String stationName) {
    this.stationName = stationName;
    return this;
  }

  private StationMixin withCallSign(String callSign) {
    this.callSign = callSign;
    return this;
  }

  private StationMixin withHdEnabled(Boolean hdEnabled) {
    this.hdEnabled = hdEnabled;
    return this;
  }

  private StationMixin withId(Long id) {
    this.id = id;
    return this;
  }


  /**
   * Returns the timestamp when the operation was carried out successfully.
   *
   * <p>
   * The timestamp could vary depending upon the type of operation performed. For an insert
   * operation, the time stamp could be represented by the {@code createdTimestamp} attribute from
   * the repository model. For an update operation, the timestamp could be represented by
   * {@code updatedTimestamp} attributed from the repository model, whereas the current timestamp
   * could be used to represent the deleted model. An implementation of this method is responsible
   * for building the mixin object that will be serialised to return.
   *
   * @return time stamp when the operation was successfully carried out
   * @param localDateTime time stamp
   * @return mixin object
   */
  @JsonIgnore
  public abstract void setTimestamp(LocalDateTime localDateTime);

  /**
   * Returns the initialised time stamp.
   */
  public abstract LocalDateTime getTimestamp();

  /**
   * An implementation of this method is responsible for building the mixin object that will be
   * serialised to return.
   *
   * <p>
   * <em>IMPORTANT:</em>This function is agnostic of any validation errors, and should only be
   * invoked if there are no validation errors.
   *
   * @param station station object
   * @return mixin object
   */
  public StationMixin buildMixin(Station station) {
    this.withStationId(station.getStationId()).withStationName(station.getStationName())
        .withId(station.getId()).withCallSign(station.getCallSign())
        .withHdEnabled(station.getHdEnabled());
    return this;
  }

  /**
   * Builds the error messages from validation errors if applicable.
   *
   * @param errors validation errors
   */
  public void buildMixin(Errors errors) {
    for (FieldError fieldError : errors.getFieldErrors()) {
      this.addError(ErrorCodes.BAD_REQUEST, fieldError.getDefaultMessage());
    }
  }

  // Visible for testing.
  public void setStationId(String stationId) {
    this.stationId = stationId;
  }

  // Visible for testing.
  public void setStationName(String stationName) {
    this.stationName = stationName;
  }

  // Visible for testing.
  public void setHdEnabled(Boolean hdEnabled) {
    this.hdEnabled = hdEnabled;
  }

  // Visible for testing.
  public void setCallSign(String callSign) {
    this.callSign = callSign;
  }
}
