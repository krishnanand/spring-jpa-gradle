// Copyright 2018 Kartik Krishnanand. All Rights Reserved.
package com.iheartmedia.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.iheartmedia.IHeartMedia;
import com.iheartmedia.dto.StationMixin;
import com.iheartmedia.dto.UpdateStationMixin;
import com.iheartmedia.model.Station;
import com.iheartmedia.repository.IStationRepository;
import com.iheartmedia.utils.AppExceptionHandler;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Locale;

/**
 * Unit Test for {@link StationController}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= {IHeartMedia.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StationControllerTest {

  @Autowired
  private StationController stationController;

  private MockMvc mockMvc;

  @Autowired
  private IStationRepository stationRepository;

  private Station hdStation;

  private Station nonHdStation;

  @Autowired
  private AppExceptionHandler handler;

  @Autowired
  private MessageSource messageSource;

  @Before
  public void setUp() throws Exception {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(this.stationController).setControllerAdvice(this.handler).build();
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
  public void tearDown() throws Exception {
    this.mockMvc = null;
    this.stationRepository.deleteAll();
  }

  @Test
  public void testCreateStation_success() throws Exception {
    Station station = new Station();
    station.setStationName("NPR News & Information");
    station.setStationId("KQED-FM");
    station.setCallSign("KQED");
    station.setHdEnabled(true);
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String body = ow.writeValueAsString(station);
    this.mockMvc.perform(
        MockMvcRequestBuilders.post("/iheartmedia/station").
            contentType(MediaType.APPLICATION_JSON_UTF8).content(body)).
            andExpect(MockMvcResultMatchers.jsonPath("$.name",
                Matchers.equalTo(station.getStationName()))).
            andExpect(MockMvcResultMatchers.jsonPath("$.stationId",
                Matchers.equalTo(station.getStationId()))).
            andExpect(MockMvcResultMatchers.jsonPath("$.callSign",
                Matchers.equalTo(station.getCallSign()))).
            andExpect(MockMvcResultMatchers.jsonPath("$.hdEnabled",
                Matchers.equalTo(station.getHdEnabled()))).
            andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist()).
            andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
  }

  @Test
  public void testCreationStation_Duplicate() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String body = ow.writeValueAsString(this.nonHdStation);
    this.mockMvc.perform(
        MockMvcRequestBuilders.post("/iheartmedia/station").
            contentType(MediaType.APPLICATION_JSON_UTF8).content(body)).
            andExpect(MockMvcResultMatchers.jsonPath("$.name").doesNotExist()).
            andExpect(MockMvcResultMatchers.jsonPath("$.stationId").doesNotExist()).
            andExpect(MockMvcResultMatchers.jsonPath("$.callSign").doesNotExist()).
            andExpect(MockMvcResultMatchers.jsonPath("$.hdEnabled").doesNotExist()).
            andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()",
                Matchers.equalTo(1))).
            andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].code",
                Matchers.equalTo(409))).
            andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message",
                Matchers.equalTo(this.messageSource.getMessage("station.save.error", null,
                    Locale.getDefault())))).andExpect(MockMvcResultMatchers.status().isConflict());
  }

  @Test
  public void testCreateStation_validationFailure() throws Exception {
    Station station = new Station();
    station.setStationId("XQED-FM");
    station.setCallSign("KQEDD");
    station.setHdEnabled(true);
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String body = ow.writeValueAsString(station);
    this.mockMvc.perform(
        MockMvcRequestBuilders.post("/iheartmedia/station").
                contentType(MediaType.APPLICATION_JSON_UTF8).content(body)).
            andExpect(MockMvcResultMatchers.jsonPath("$.name").doesNotExist()).
            andExpect(MockMvcResultMatchers.jsonPath("$.stationId").doesNotExist()).
            andExpect(MockMvcResultMatchers.jsonPath("$.callSign").doesNotExist()).
            andExpect(MockMvcResultMatchers.jsonPath("$.hdEnabled").doesNotExist()).
            andExpect(MockMvcResultMatchers.jsonPath("$.errors[*].message").value(
                Matchers.containsInAnyOrder(
                    "Station name can not be empty.",
                    "Station ID XQED-FM is not valid. Station ID should start with either W or K.",
                    "Call sign " + station.getCallSign() +
                        " is of invalid length. Station call sign must be 4 characters long."))).
            andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void testDeleteStation_stationIdNotFound() throws Exception {
    this.mockMvc.perform(
        MockMvcRequestBuilders.delete("/iheartmedia/station/W123").
                contentType(MediaType.APPLICATION_JSON_UTF8)).
            andExpect(MockMvcResultMatchers.jsonPath("$.name").doesNotExist()).
            andExpect(MockMvcResultMatchers.jsonPath("$.stationId").doesNotExist()).
            andExpect(MockMvcResultMatchers.jsonPath("$.callSign").doesNotExist()).
            andExpect(MockMvcResultMatchers.jsonPath("$.hdEnabled").doesNotExist()).
            andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()",
                Matchers.equalTo(1))).
            andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].code",
                Matchers.equalTo(404))).
            andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message",
                Matchers.equalTo(this.messageSource.getMessage("station.not.found.station.id",
                    new Object[] {"W123"}, Locale.getDefault())))).
            andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void testDeleteStation_success() throws Exception {
    this.mockMvc.perform(
        MockMvcRequestBuilders.delete(
                "/iheartmedia/station/" + this.hdStation.getStationId()).
            contentType(MediaType.APPLICATION_JSON_UTF8)).
        andExpect(MockMvcResultMatchers.jsonPath("$.name",
            Matchers.equalTo(this.hdStation.getStationName()))).
        andExpect(MockMvcResultMatchers.jsonPath("$.stationId",
            Matchers.equalTo(this.hdStation.getStationId()))).
        andExpect(MockMvcResultMatchers.jsonPath("$.callSign",
            Matchers.equalTo(this.hdStation.getCallSign()))).
        andExpect(MockMvcResultMatchers.jsonPath("$.hdEnabled",
            Matchers.equalTo(this.hdStation.getHdEnabled()))).
        andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist()).
        andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void testUpdateStation_success() throws Exception {
    StationMixin mixin = new UpdateStationMixin();
    mixin.setCallSign("AB12");
    mixin.setHdEnabled(this.hdStation.getHdEnabled() ? Boolean.FALSE : Boolean.TRUE);
    mixin.setStationName(this.hdStation.getStationName() + "Update");
    mixin.setStationId(this.hdStation.getStationId());
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String body = ow.writeValueAsString(mixin);
    this.mockMvc.perform(
        MockMvcRequestBuilders.put("/iheartmedia/station/" + this.hdStation.getStationId()).
                contentType(MediaType.APPLICATION_JSON_UTF8).content(body)).
            andExpect(MockMvcResultMatchers.jsonPath("$.name",
                Matchers.equalTo(mixin.getStationName()))).
            andExpect(MockMvcResultMatchers.jsonPath("$.stationId",
                Matchers.equalTo(mixin.getStationId()))).
            andExpect(MockMvcResultMatchers.jsonPath("$.callSign",
                Matchers.equalTo(mixin.getCallSign()))).
            andExpect(MockMvcResultMatchers.jsonPath("$.hdEnabled",
                Matchers.equalTo(mixin.getHdEnabled()))).
          andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist()).
          andExpect(MockMvcResultMatchers.status().isOk());
    Station station = this.stationRepository.findById(this.hdStation.getId()).get();
    // Updated two fields
    MatcherAssert.assertThat(station.getHdEnabled(), Matchers.equalTo(mixin.getHdEnabled()));
    MatcherAssert.assertThat(station.getStationName(), Matchers.equalTo(mixin.getStationName()));
    MatcherAssert.assertThat(station.getCallSign(), Matchers.equalTo("AB12"));
    MatcherAssert.assertThat(station.getStationId(), Matchers.equalTo(mixin.getStationId()));
  }

  @Test
  public void testUpdateStation_validationFailure() throws Exception {
    StationMixin mixin = new UpdateStationMixin();
    mixin.setCallSign("ABC123");
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String body = ow.writeValueAsString(mixin);
    this.mockMvc.perform(
        MockMvcRequestBuilders.put("/iheartmedia/station/" + this.hdStation.getStationId()).
                contentType(MediaType.APPLICATION_JSON_UTF8).content(body)).
        andExpect(MockMvcResultMatchers.jsonPath("$.name").doesNotExist()).
        andExpect(MockMvcResultMatchers.jsonPath("$.stationId").doesNotExist()).
        andExpect(MockMvcResultMatchers.jsonPath("$.callSign").doesNotExist()).
        andExpect(MockMvcResultMatchers.jsonPath("$.hdEnabled").doesNotExist()).
        andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()",
            Matchers.equalTo(1))).
        andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].code",
            Matchers.equalTo(400))).
        andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message",
            Matchers.equalTo(this.messageSource.getMessage("callsign.size.invalid",
                new Object[] {mixin.getCallSign()}, Locale.getDefault())))).
        andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void testRetrieveAllStations_success() throws Exception {
    String responseString = this.mockMvc.perform(
        MockMvcRequestBuilders.get("/iheartmedia/stations")).
            andReturn().getResponse().getContentAsString();
    ObjectMapper mapper = new ObjectMapper();
    List<Station> stations = mapper.readValue(responseString,
        new TypeReference<List<Station>>() {});
    MatcherAssert.assertThat(
        stations, Matchers.containsInAnyOrder(this.hdStation, this.nonHdStation));
  }

  @Test
  public void testRetrieveAllStations_noData() throws Exception {
    this.stationRepository.deleteAll();
    String responseString = this.mockMvc.perform(
        MockMvcRequestBuilders.get("/iheartmedia/stations")).
        andReturn().getResponse().getContentAsString();
    MatcherAssert.assertThat(responseString, Matchers.equalTo("[]"));
  }

  @Test
  public void testRetrieveStationByStationId() throws Exception {
    this.mockMvc.perform(
        MockMvcRequestBuilders.get(
                "/iheartmedia/station/id/" + this.nonHdStation.getStationId()).
                contentType(MediaType.APPLICATION_JSON_UTF8)).
        andExpect(MockMvcResultMatchers.jsonPath("$.name",
            Matchers.equalTo(this.nonHdStation.getStationName()))).
        andExpect(MockMvcResultMatchers.jsonPath("$.stationId",
            Matchers.equalTo(this.nonHdStation.getStationId()))).
        andExpect(MockMvcResultMatchers.jsonPath("$.callSign",
            Matchers.equalTo(this.nonHdStation.getCallSign()))).
        andExpect(MockMvcResultMatchers.jsonPath("$.hdEnabled",
            Matchers.equalTo(this.nonHdStation.getHdEnabled()))).
        andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist()).
        andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void testRetrieveStationByStationId_stationNotFound() throws Exception {
    this.mockMvc.perform(
        MockMvcRequestBuilders.get(
            "/iheartmedia/station/id/test").
            contentType(MediaType.APPLICATION_JSON_UTF8)).
        andExpect(MockMvcResultMatchers.jsonPath("$.name").doesNotExist()).
        andExpect(MockMvcResultMatchers.jsonPath("$.stationId").doesNotExist()).
        andExpect(MockMvcResultMatchers.jsonPath("$.callSign").doesNotExist()).
        andExpect(MockMvcResultMatchers.jsonPath("$.hdEnabled").doesNotExist()).
        andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()",
            Matchers.equalTo(1))).
        andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].code",
            Matchers.equalTo(404))).
        andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message",
            Matchers.equalTo(this.messageSource.getMessage("station.not.found.station.id",
                new Object[] {"test"}, Locale.getDefault())))).
        andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void testRetrieveStationByStationName() throws Exception {
    this.mockMvc.perform(
        MockMvcRequestBuilders.get(
            "/iheartmedia/station/name/" + this.hdStation.getStationName()).
            contentType(MediaType.APPLICATION_JSON_UTF8)).
        andExpect(MockMvcResultMatchers.jsonPath("$.name",
            Matchers.equalTo(this.hdStation.getStationName()))).
        andExpect(MockMvcResultMatchers.jsonPath("$.stationId",
            Matchers.equalTo(this.hdStation.getStationId()))).
        andExpect(MockMvcResultMatchers.jsonPath("$.callSign",
            Matchers.equalTo(this.hdStation.getCallSign()))).
        andExpect(MockMvcResultMatchers.jsonPath("$.hdEnabled",
            Matchers.equalTo(this.hdStation.getHdEnabled()))).
        andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist()).
        andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void testRetrieveStationByStationName_stationNotFound() throws Exception {
    this.mockMvc.perform(
        MockMvcRequestBuilders.get(
            "/iheartmedia/station/name/test").
            contentType(MediaType.APPLICATION_JSON_UTF8)).
        andExpect(MockMvcResultMatchers.jsonPath("$.name").doesNotExist()).
        andExpect(MockMvcResultMatchers.jsonPath("$.stationId").doesNotExist()).
        andExpect(MockMvcResultMatchers.jsonPath("$.callSign").doesNotExist()).
        andExpect(MockMvcResultMatchers.jsonPath("$.hdEnabled").doesNotExist()).
        andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()",
            Matchers.equalTo(1))).
        andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].code",
            Matchers.equalTo(404))).
        andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message",
            Matchers.equalTo(this.messageSource.getMessage("station.not.found.station.name",
                new Object[] {"test"}, Locale.getDefault())))).
        andExpect(MockMvcResultMatchers.status().isBadRequest());
  }
}
