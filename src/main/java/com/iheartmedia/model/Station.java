// Copyright 2018 Kartik Krishnanand. All Rights Reserved.
package com.iheartmedia.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * An instance of this class encapsulates properties representing a specific detail related to
 * radio stations.
 */
@Data
@Table(name = "user_station", indexes = {
    @Index(columnList = "station_id", name = "station_index_station_id"),
    @Index(columnList = "station_name", name="station_index_name"),
    @Index(columnList = "hd_enabled", name = "station_index_hd_enabled")
}, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id", "version"})
})
@Entity
@EqualsAndHashCode(exclude = {"createdTimeStamp", "updatedTimestamp", "id", "version"}, callSuper =
    true)
@ToString(exclude = {"createdTimeStamp", "updatedTimestamp", "id"}, callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Station extends IError {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    // Represents a station id for the user.
    @Column(name="station_id", nullable = false, unique = true)
    @NotEmpty(message = "{station.id.empty}")
    @Pattern(regexp = "^([KW])[A-Za-z0-9\\-].*$", message = "{station.id.format.not.valid}")
    private String stationId;

    @Column(name="station_name", nullable = false, unique = true)
    @JsonProperty("name")
    @NotEmpty(message = "{station.name.empty}")
    private String stationName;

    @Column(name = "hd_enabled")
    private Boolean hdEnabled;

    @Column(name="call_sign", nullable = false)
    @NotEmpty(message = "{station.call.sign.empty}")
    @Size(max = 4, min = 4, message = "{callsign.size}")
    private String callSign;

    @Column(name="user_created_timestamp", nullable = false)
    @JsonIgnore
    private LocalDateTime createdTimeStamp;

    @Column(name="user_modified_timestamp", nullable = false)
    @JsonIgnore
    private LocalDateTime updatedTimestamp;

    @Column(name="version", columnDefinition="INT default '0'")
    @JsonIgnore
    private Integer version = 0;

    /**
     * Initialises the timestamps prior to update or insertions.
     *
     * <p>The implementation ensures that time stamps would always reflect the time when entities
     * were persisted or updated.
     */
    @PrePersist
    @PreUpdate
    public void setTimestamps() {
        LocalDateTime utcNow = LocalDateTime.now(ZoneOffset.UTC);
        if (this.createdTimeStamp == null) {
            this.createdTimeStamp = utcNow;
        }
        this.updatedTimestamp = utcNow;
        if (this.hdEnabled == null) {
            this.hdEnabled = Boolean.FALSE;
        }
        this.version += 1;
    }
}
