// Copyright 2018 Kartik Krishnanand. All Rights Reserved.
package com.iheartmedia.service;

import com.iheartmedia.dto.StationMixin;
import com.iheartmedia.model.Station;

import java.util.List;
import java.util.Locale;

/**
 * Strategy definition of all the methods that define all the methods related to manipulation of
 * station related data.
 */
public interface IStationService {

    /**
     * A strategy definition of the method to create a station and to persist the data in the
     * repository.
     *
     * @return mixin instance instance encapsulating the details of the newly created station
     */
    StationMixin createStation(Station valueObject);

    /**
     * Strategy definition to return all stations.
     * @return set of all stations
     */
    List<Station> retrieveAllStations();

    /**
     * Strategy definition of the class invoked to update the stations by station id.
     *
     * @param stationId station id for which the data is to be updated
     * @param stationMixin station instance encapsulation the data to be updated
     * @return mixin instance representing the updated station information
     */

    StationMixin updateStation(String stationId, StationMixin stationMixin, Locale locale);

    /**
     * Returns station by station id.
     *
     * @param stationId station id
     * @param locale locale object
     * @return station object
     */
    StationMixin retrieveStationByStationId(String stationId, Locale locale);

    /**
     * Retrieves station by station name.
     */
    StationMixin retrieveStationByStationName(String stationName, Locale locale);

    /**
     * Retrieves all stations which are HD enabled.
     *
     * @return set of stations
     */
    List<Station> retrieveHdEnabledStations();

    /**
     * Stategy definition to delete station.
     *
     * @param stationId station object to be deted
     * @return mixin object
     */
    StationMixin deleteStationByStationId(String stationId, Locale locale);
}
