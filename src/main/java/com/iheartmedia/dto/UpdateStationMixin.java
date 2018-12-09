// Copyright 2018 Kartik Krishnanand. All Rights Reserved.
package com.iheartmedia.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

/**
 * An instance of this value encapsulates any metadata pertaining to status of an update database
 * operation.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, exclude = {"updatedTimestamp", "id"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UpdateStationMixin extends StationMixin {

    @JsonProperty("updatedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedTimestamp;

    @Override public void setTimestamp(LocalDateTime localDateTime) {
        this.updatedTimestamp = localDateTime;
    }

    @Override public LocalDateTime getTimestamp() {
        return this.updatedTimestamp;
    }
}
