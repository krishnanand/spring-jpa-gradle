// Copyright 2018 Kartik Krishnanand. All Rights Reserved.
package com.iheartmedia.controller;

import com.iheartmedia.dto.CreateStationMixin;
import com.iheartmedia.dto.StationMixin;
import com.iheartmedia.dto.UpdateStationMixin;
import com.iheartmedia.model.Station;
import com.iheartmedia.service.IStationService;
import com.iheartmedia.utils.StationMixinValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Instance of this class encapsulates all the nahndler methods.
 */
@RestController
@RequestMapping("/iheartmedia")
public class StationController {

  private final IStationService stationService;

  @Autowired
  private StationMixinValidator stationMixinValidator;

  private static final Log LOG = LogFactory.getLog(StationController.class);

  @Autowired
  public StationController(IStationService stationService) {
    this.stationService = stationService;
  }

  /**
   * Returns all the stations.
   * @return list of stations
   */
  @GetMapping("/stations")
  public List<Station> retrieveAllStations() {
    return this.stationService.retrieveAllStations();
  }

  /**
   * Returns the stations by station id
   *
   * @param request request
   * @param stationId station id
   * @return mixin object
   */
  @GetMapping("/station/id/{stationId}")
  public ResponseEntity<StationMixin> findByStationId(HttpServletRequest request,
      @PathVariable String stationId) {
    StationMixin mixin = this.stationService.retrieveStationByStationId(stationId,
        RequestContextUtils.getLocale(request));
    return new ResponseEntity<>(mixin, mixin.hasErrors() ? HttpStatus.BAD_REQUEST: HttpStatus.OK);
  }

  /**
   * Returns the stations by station id
   *
   * @param request request
   * @param stationName station id
   * @return mixin object
   */
  @GetMapping("/station/name/{stationName}")
  public ResponseEntity<StationMixin> findByStationName(HttpServletRequest request,
      @PathVariable String stationName) {
    StationMixin mixin = this.stationService.retrieveStationByStationName(stationName,
        RequestContextUtils.getLocale(request));
    return new ResponseEntity<>(mixin, mixin.hasErrors() ? HttpStatus.BAD_REQUEST: HttpStatus.OK);
  }

  @PostMapping("/station")
  public ResponseEntity<StationMixin> createStation(
      @RequestBody @Valid Station stationObject, BindingResult result) {
    LOG.info("Creating stations");
    if (result.hasErrors()) {
      LOG.warn("Failure to validate station objects.");
      StationMixin mixin = new CreateStationMixin();
      mixin.buildMixin(result);
      LOG.warn("Unable to create a station : " + stationObject);
      return  new ResponseEntity<>(mixin, HttpStatus.BAD_REQUEST);
    }
    StationMixin mixin = this.stationService.createStation(stationObject);
    LOG.info("Station has been created");
    return new ResponseEntity<>(mixin, HttpStatus.CREATED);
  }

  @DeleteMapping("/station/{stationId}")
  public ResponseEntity<StationMixin> deleteStation(HttpServletRequest request, 
      @PathVariable String stationId) {
    StationMixin mixin = 
        this.stationService.deleteStationByStationId(stationId,
            RequestContextUtils.getLocale(request));
      return new ResponseEntity<>(mixin, mixin.hasErrors() ? HttpStatus.BAD_REQUEST:
          HttpStatus.OK);
  }

  @PutMapping("/station/{stationId}")
  public ResponseEntity<StationMixin> updateStation(
      HttpServletRequest request, @PathVariable String stationId,
      @RequestBody UpdateStationMixin updateMixin, BindingResult result) {
    LOG.info("Updating the stations.");
    this.stationMixinValidator.validate(updateMixin, result);
    if (result.hasErrors()) {
      LOG.warn("Validation of the station object failed");
      return new ResponseEntity<>(
          this.stationMixinValidator.createMixin(result, RequestContextUtils.getLocale(request)),
          HttpStatus.BAD_REQUEST);
    }
    StationMixin mixin = this.stationService
        .updateStation(stationId, updateMixin, RequestContextUtils.getLocale(request));
    return new ResponseEntity<>(mixin, mixin.hasErrors() ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
  }

}
