// Copyright 2018 Kartik Krishnanand. All Rights Reserved.
package com.iheartmedia.repository;

import com.iheartmedia.model.Station;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Proxy definition to handle CRUD operations for a user.
 */
@Repository
@Retryable(maxAttempts=5, value= {Exception.class}, backoff=@Backoff(delay=2000))
public interface IStationRepository extends JpaRepository<Station, Long> {

    /**
     * Proxy method to find station by station id.
     *
     * @param stationId station id
     * @return station
     */
    Station findByStationId(String stationId);

    /**
     * Proxy method to find station by station name.
     *
     * @param stationName station name to be used in query
     * @return list of stations matching the station name
     */
    Station findByStationName(String stationName);

    /**
     * Proxy method to return all hd enabled stations.
     * @return list of proxy stations
     */
    @Query("SELECT s FROM Station s WHERE s.hdEnabled=true")
    List<Station> findHdEnabledStations();

    /**
     * Proxy method to delete the station
     * @param stationId station id
     * @return list of deleted station
     */
    List<Station> removeByStationId(String stationId);
}
