// Copyright 2018 Kartik Krishnanand. All Rights Reserved.
package com.iheartmedia.model;

import com.iheartmedia.IHeartMedia;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 * Unit test to validate {@link Station}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= {IHeartMedia.class})
public class StationTest {

    @Autowired
    private Validator validator;

    private Station station;

    @Before
    public void setUp() throws Exception {
        this.station = new Station();
    }

    @Test
    public void testValidator_valid() {
        this.station.setStationId("WNYL");
        this.station.setStationName("Alt 92.3");
        this.station.setCallSign("WXRK");
        Set<ConstraintViolation<Station>> stationConstraints =
            this.validator.validate(this.station);
        MatcherAssert.assertThat(stationConstraints.isEmpty(), Is.is(true));
    }

    @Test
    public void testValidator_allNulls() {
        Set<ConstraintViolation<Station>> constraintViolations =
            this.validator.validate(this.station);
        MatcherAssert.assertThat(constraintViolations.isEmpty(), Is.is(false));
        MatcherAssert.assertThat(constraintViolations.size(), Matchers.equalTo(3));
        Set<String> actualMessages = new HashSet<>();
        for (ConstraintViolation<Station> constraintViolation : constraintViolations) {
            actualMessages.add(constraintViolation.getMessage());
        }
        MatcherAssert.assertThat(actualMessages,
            Matchers.containsInAnyOrder("Station call sign can not be empty.",
                "Station ID can not be empty.", "Station name can not be empty."));
    }

    @Test
    public void testValidator_stationId_EastCoast_Valid() throws Exception {
        this.station.setStationId("WNYL");
        this.station.setStationName("Alt 92.3");
        this.station.setCallSign("KXRK");
        Set<ConstraintViolation<Station>> stationConstraints =
            this.validator.validate(this.station);
        MatcherAssert.assertThat(stationConstraints.isEmpty(), Is.is(true));
    }

    @Test
    public void testValidator_stationId_invalid() throws Exception {
        this.station.setStationId("XYZ");
        this.station.setStationName("Alt 92.3");
        this.station.setCallSign("XY93");
        Set<ConstraintViolation<Station>> stationConstraints =
            this.validator.validate(this.station);
        MatcherAssert.assertThat(stationConstraints.size(), Is.is(Matchers.equalTo(1)));
        ConstraintViolation<Station> stationIdConstraint = stationConstraints.iterator().next();
        MatcherAssert.assertThat(
            stationIdConstraint.getMessage(),
            Matchers.equalTo(
                "Station ID " + this.station.getStationId() +
                    " is not valid. Station ID should " + "start with either W or K."));
    }

    @Test
    public void testValidator_callSign_invalidLength() throws Exception {
        this.station.setStationId("WNYL");
        this.station.setStationName("Alt 92.3");
        this.station.setCallSign("XYZ93");
        Set<ConstraintViolation<Station>> stationConstraints =
            this.validator.validate(this.station);
        MatcherAssert.assertThat(stationConstraints.size(), Is.is(Matchers.equalTo(1)));
        ConstraintViolation<Station> stationIdConstraint = stationConstraints.iterator().next();
        MatcherAssert.assertThat(
            stationIdConstraint.getMessage(),
            Matchers.equalTo(
                "Call sign "+ this.station.getCallSign()  + " is of invalid length. Station call "
                    + "sign must be 4 characters long."));
    }
}
