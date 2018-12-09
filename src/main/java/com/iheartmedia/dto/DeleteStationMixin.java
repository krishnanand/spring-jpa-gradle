// Copyright 2018 Kartik Krishnanand. All Rights Reserved.
package com.iheartmedia.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

/**
 * An instance of this class encapsulates payload represening the outcome of a delete operation.
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = "deletedTimestamp")
@ToString(callSuper = true)
public class DeleteStationMixin extends StationMixin {

    @JsonProperty("deletedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Setter(AccessLevel.NONE)
    private LocalDateTime deletedTimestamp;

    @Override public void setTimestamp(LocalDateTime localDateTime) {
        this.deletedTimestamp = localDateTime;
    }

    @Override public LocalDateTime getTimestamp() {
        return this.deletedTimestamp;
    }
}
