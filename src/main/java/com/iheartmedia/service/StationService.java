// Copyright 2018 Kartik Krishnanand. All Rights Reserved.
package com.iheartmedia.service;

import com.iheartmedia.dto.CreateStationMixin;
import com.iheartmedia.dto.DeleteStationMixin;
import com.iheartmedia.dto.GetStationMixin;
import com.iheartmedia.dto.StationMixin;
import com.iheartmedia.dto.UpdateStationMixin;
import com.iheartmedia.model.ErrorCodes;
import com.iheartmedia.model.Station;
import com.iheartmedia.repository.IStationRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

/**
 * Stategy implementation of services encapsulating all the functionality specific to the service
 * layer.
 */
@Service
public class StationService implements IStationService {

  private static final Log LOG = LogFactory.getLog(StationService.class);

  private final IStationRepository stationRepository;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  public StationService(IStationRepository stationRepository) {
    this.stationRepository = stationRepository;
  }

  /**
   * Persists the entity to the database.
   *
   * @param valueObject value object to be inserted
   * @return mixin object created mixin
   */
  @Transactional
  @Override
  public StationMixin createStation(Station valueObject) {
    LOG.info("Creating the object");
    StationMixin mixin = new CreateStationMixin();
    Station savedEntity = this.stationRepository.save(valueObject);
    mixin.buildMixin(savedEntity);
    LOG.info("Object created successfully");
    return mixin;
  }

  @Transactional
  @Override
  public List<Station> retrieveAllStations() {
    return this.stationRepository.findAll();
  }

  private void updateState(Station stationObject, StationMixin mixin) {
    if (mixin.getCallSign() != null && !mixin.getCallSign().isEmpty()) {
      stationObject.setCallSign(mixin.getCallSign());
    }
    if (mixin.getStationName() != null && !mixin.getStationName().isEmpty()) {
      stationObject.setStationName(mixin.getStationName());
    }
    if (mixin.getHdEnabled() != null
        && !mixin.getHdEnabled().equals(stationObject.getHdEnabled())) {
      stationObject.setHdEnabled(mixin.getHdEnabled());
    }
    stationObject.setUpdatedTimestamp(LocalDateTime.now(ZoneOffset.UTC));
  }

  /**
   * Transforms the station object to a mixin object.
   *
   * @param station station object to be transformec
   * @param locale locale
   * @param errorMessageKey error message keuy
   * @param errorArgs error arguments to be interpolated in message function
   * @return mixin object
   */
  private StationMixin transformStationToMixin(Station station, Locale locale,
      String errorMessageKey, Object ... errorArgs) {
    StationMixin mixin = new GetStationMixin();
    if (station == null) {
      mixin.addError(ErrorCodes.NOT_FOUND,
          this.messageSource.getMessage(errorMessageKey, errorArgs, locale));
      return mixin;
    }
    mixin.buildMixin(station);
    return mixin;
  }

  @Transactional
  @Override
  public StationMixin retrieveStationByStationId(String stationId, Locale locale) {
    Station station =  this.stationRepository.findByStationId(stationId);
    return this.transformStationToMixin(
        station, locale, "station.not.found.station.id", stationId);
  }

  @Transactional
  @Override
  public StationMixin retrieveStationByStationName(String stationName, Locale locale) {
    Station station = this.stationRepository.findByStationName(stationName);
    return this.transformStationToMixin(
        station, locale, "station.not.found.station.name", stationName);
  }

  /**
   * Validates the fetched entity.
   *
   * <p>The station entity is checked for state, and for {@code null}. It is recommended that the
   * hibernate validation is implemented outside the transaction.
   *
   * @param locale locale locale
   * @param fetchedStation station to be checked for nulls
   * @param messageKey message key
   * @param errorArgs error arguments
   * @return mixin object
   */
   private StationMixin validateStationOrSave(Locale locale,
       Station fetchedStation, StationMixin stateToBeMerged, String messageKey,
       Object ... errorArgs) {
     LOG.info("Initiating the validation and serving objects");
     if (fetchedStation == null) {
       StationMixin mixin = new UpdateStationMixin();
       LOG.warn("No objects to be updated");
       mixin.addError(
           ErrorCodes.NOT_FOUND,
           this.messageSource.getMessage(messageKey, errorArgs, locale));
       return mixin;
     }
     updateState(fetchedStation, stateToBeMerged);
     Station updatedEntity = this.stationRepository.save(fetchedStation);
     StationMixin mixin = new UpdateStationMixin();
     mixin.setTimestamp(updatedEntity.getUpdatedTimestamp());
     mixin.buildMixin(updatedEntity);
     LOG.info("Entity " + fetchedStation.getStationId() + " has been updated.");
     return mixin;
  }


  /**
   * Updates the station entity by station id.
   *
   * @param stationId station id associated with the station
   * @param stationMixin station instance encapsulation the data to be updated
   * @param locale locale instance
   * @return mixin object
   */
  @Override
  @Transactional
  public StationMixin updateStation(
      String stationId, StationMixin stationMixin, Locale locale) {
    Station fetchedStation = this.stationRepository.findByStationId(stationId);
    LOG.info("Updating the station by station id: " + stationId);
    return validateStationOrSave( locale, fetchedStation, stationMixin,
        "station.not.found.station.id", stationId);
  }

  @Override
  @Transactional
  public List<Station> retrieveHdEnabledStations() {
    return this.stationRepository.findHdEnabledStations();
  }

  @Override
  @Transactional
  public StationMixin deleteStationByStationId(String stationId, Locale locale) {
    List<Station> deletedStations = this.stationRepository.removeByStationId(stationId);
    LOG.info("Deleting the station by station id: " + stationId);
    StationMixin mixin = new DeleteStationMixin();
    if (deletedStations.isEmpty()) {
      LOG.warn("Unable to delete station by station id: " + stationId);
      mixin.addError(
          ErrorCodes.NOT_FOUND,
          this.messageSource.getMessage("station.not.found.station.id",
              new Object[] {stationId}, locale));
    } else {
      // Station id is supposed to be unique so there should only be one instance.
      LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC);
      mixin.setTimestamp(currentTime);
      Station deletedStation = deletedStations.iterator().next();
      mixin.buildMixin(deletedStation);
    }
    return mixin;
  }
}
