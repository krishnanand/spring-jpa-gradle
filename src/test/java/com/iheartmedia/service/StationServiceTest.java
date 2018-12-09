// Copyright 2018 Kartik Krishnanand. All Rights Reserved.

package com.iheartmedia.service;

import com.iheartmedia.IHeartMedia;
import com.iheartmedia.dto.CreateStationMixin;
import com.iheartmedia.dto.DeleteStationMixin;
import com.iheartmedia.dto.GetStationMixin;
import com.iheartmedia.dto.StationMixin;
import com.iheartmedia.dto.UpdateStationMixin;
import com.iheartmedia.model.ErrorCodes;
import com.iheartmedia.model.Station;
import com.iheartmedia.repository.IStationRepository;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Unit test for {@link StationService}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= {IHeartMedia.class})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class StationServiceTest {

  @Autowired
  private IStationService stationService;

  @Autowired
  private IStationRepository stationRepository;

  @Autowired
  private MessageSource messageSource;

  private Station hdStation;

  private Station nonHdStation;

  @Before
  public void setUp() throws Exception {
    Station hdStation = new Station();
    hdStation.setHdEnabled(Boolean.TRUE);
    hdStation.setStationId("WHTZ-FM");
    hdStation.setStationName("Z-100");
    hdStation.setCallSign("WHTZ");
    this.hdStation = this.stationRepository.save(hdStation);

    Station nonHdStation = new Station();
    nonHdStation.setStationId("KISS-FM");
    nonHdStation.setStationName("102.7");
    nonHdStation.setCallSign("KISS");
    this.nonHdStation = stationRepository.save(nonHdStation);
  }

  @After
  public void tearDown() {
    this.stationRepository.deleteAll();
  }

  @Test
  public void testRetrieveAllStations() throws Exception {
    List<Station> fetchedStations = this.stationService.retrieveAllStations();
    MatcherAssert.assertThat(fetchedStations, Matchers.containsInAnyOrder(this.nonHdStation,
        this.hdStation));
  }

  @Test
  public void testRetriveByStationId() throws Exception {
    StationMixin fetchedHdStation =
        this.stationService.retrieveStationByStationId(this.hdStation.getStationId(),
            Locale.getDefault());
    StationMixin hdMixin = new GetStationMixin();
    hdMixin.buildMixin(this.hdStation);
    MatcherAssert.assertThat(fetchedHdStation, Matchers.equalTo(hdMixin));

    StationMixin fetchedNonHdStatin =
        this.stationService.retrieveStationByStationId(this.nonHdStation.getStationId(),
            Locale.getDefault());
    GetStationMixin nonHdMixin = new GetStationMixin();
    nonHdMixin.buildMixin(this.nonHdStation);
    MatcherAssert.assertThat(fetchedNonHdStatin, Matchers.equalTo(nonHdMixin));
  }

  @Test
  public void testRetrieveByStationName() throws Exception {
    StationMixin fetchedHdStation =
        this.stationService.retrieveStationByStationName(this.hdStation.getStationName(),
            Locale.getDefault());
    StationMixin hdMixin = new GetStationMixin();
    hdMixin.buildMixin(this.hdStation);
    MatcherAssert.assertThat(fetchedHdStation, Matchers.equalTo(hdMixin));

    StationMixin fetchedNonHdStation =
        this.stationService.retrieveStationByStationName(this.nonHdStation.getStationName(),
            Locale.getDefault());
    StationMixin nonHdMixin = new GetStationMixin();
    nonHdMixin.buildMixin(this.nonHdStation);
    MatcherAssert.assertThat(fetchedNonHdStation, Matchers.equalTo(nonHdMixin));
  }

  @Test
  public void testCreateStation_success() throws Exception {
    Station newStation = new Station();
    newStation.setCallSign("KQED");
    newStation.setStationName("KQED Public Media for Northern CA");
    newStation.setStationId("KQED-FM");
    newStation.setHdEnabled(Boolean.TRUE);
    StationMixin savedStation = this.stationService.createStation(newStation);
    StationMixin expected = new CreateStationMixin();
    expected.buildMixin(newStation);
    expected.setTimestamp(newStation.getCreatedTimeStamp());
    MatcherAssert.assertThat(savedStation, Matchers.equalTo(expected));
    MatcherAssert.assertThat(newStation,
        Matchers.equalTo(this.stationRepository.findById(savedStation.getId()).get()));
  }

  @Test
  public void testUpdateStation_success() throws Exception {
    Station fetchedStation = this.stationRepository.findById(this.hdStation.getId()).get();
    StationMixin mixin = new UpdateStationMixin();
    mixin.setCallSign(fetchedStation.getCallSign());
    mixin.setHdEnabled(fetchedStation.getHdEnabled() ? Boolean.FALSE : Boolean.TRUE);
    mixin.setStationName(fetchedStation.getStationName() + "Update");
    mixin.setStationId(fetchedStation.getStationId());
    StationMixin updatedMixin =
        this.stationService.updateStation(fetchedStation.getStationId(), mixin,
            Locale.getDefault());
    MatcherAssert.assertThat(updatedMixin, Matchers.equalTo(mixin));
    Station updatedStation = this.stationRepository.findById(fetchedStation.getId()).get();
    MatcherAssert.assertThat(updatedStation.getStationId(), Matchers.equalTo(mixin.getStationId()));
    MatcherAssert.assertThat(updatedStation.getCallSign(), Matchers.equalTo(mixin.getCallSign()));
    MatcherAssert.assertThat(updatedStation.getStationName(), Matchers.equalTo(mixin.getStationName()));
    MatcherAssert.assertThat(updatedStation.getHdEnabled(), Matchers.equalTo(mixin.getHdEnabled()));
    Assert.assertNotNull(updatedMixin.getTimestamp());
  }

  @Test
  public void testUpdateStation_stationIdNotFound() throws Exception {
    StationMixin updatedMixin =
        this.stationService.updateStation("ABCD", new UpdateStationMixin(),
            Locale.getDefault());
    Assert.assertTrue(updatedMixin.hasErrors());
    StationMixin expected = new UpdateStationMixin();
    expected.addError(
        ErrorCodes.NOT_FOUND,
        this.messageSource.getMessage("station.not.found.station.id", new Object[] {"ABCD"},
            Locale.getDefault()));
    MatcherAssert.assertThat(updatedMixin, Matchers.equalTo(expected));
  }

  @Test
  public void testDeleteByStationId() throws Exception {
    StationMixin deletedMixin =
        this.stationService.deleteStationByStationId(this.hdStation.getStationId(),
            Locale.getDefault());
    StationMixin expected = new DeleteStationMixin();
    expected.setCallSign(this.hdStation.getCallSign());
    expected.setStationId(this.hdStation.getStationId());
    expected.setStationName(this.hdStation.getStationName());
    expected.setHdEnabled(this.hdStation.getHdEnabled());
    MatcherAssert.assertThat(deletedMixin, Matchers.equalTo(expected));
  }

  @Test
  public void testHdEnabledStations() throws Exception {
    List<Station> hdStations = this.stationService.retrieveHdEnabledStations();
    MatcherAssert.assertThat(hdStations, Matchers.equalTo(Arrays.asList(this.hdStation)));
  }
}
