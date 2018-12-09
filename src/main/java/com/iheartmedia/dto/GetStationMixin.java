// Copyright 2018 Kartik Krishnanand. All Rights Reserved.
package com.iheartmedia.dto;

import java.time.LocalDateTime;

/**
 * A value object instance whose payload encapsulates all the data associated with a fetch.
 */
public class GetStationMixin extends StationMixin {
  @Override public void setTimestamp(LocalDateTime localDateTime) {}

  @Override public LocalDateTime getTimestamp() {
    return null;
  }
}
