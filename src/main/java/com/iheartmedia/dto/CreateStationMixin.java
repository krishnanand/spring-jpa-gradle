// Copyright 2018 Kartik Krishnanand. All Rights Reserved.
package com.iheartmedia.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A value object that encapsulates the payload associated with the creating of station.
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = "creationTimestamp")
@ToString(callSuper = true)
public class CreateStationMixin extends StationMixin {

    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private LocalDateTime creationTimestamp;


    @Override public void setTimestamp(LocalDateTime localDateTime) {
        this.creationTimestamp = localDateTime;
    }

    @Override public LocalDateTime getTimestamp() {
        return this.creationTimestamp;
    }
}
