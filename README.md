# Restful Webservice to allow the users to perform CRUD operations to create, view, update and delete stations.

## Table of Contents

- [Requirements](#requirements)
- [Setup](#setup)
- [Deployment](#deployment)
- [API Endpoints](#api-endpoints)
  * [Create Station](#create-station)
      + [Request Body](#request-body)
      + [Response Body](#response-body)
      + [Error Response Body](#error-response-body)
  * [Update Station](#update-station)
      + [Request Body](#request-body-1)
      + [Response Body](#response-body-1)
      + [Error Response Body](#error-response-body-1)
  * [Delete Station](#delete-station)
      + [Response Body](#response-body-2)
      + [Error Response Body](#error-response-body-2)
  * [Fetch All Stations](#fetch-all-stations)
      + [Response Body](#response-body-3)
  * [Fetch By Station Id](#fetch-by-station-id)
      + [Response Body](#response-body-4)
      + [Error Response Body](#error-response-body-3)
  * [Fetch By Station Name](#fetch-by-station-name)
      + [Response Body](#response-body-5)
      + [Error Response Body](#error-response-body-4)
  * [Swagger API](#swagger-api)
  * [Actuator](#actuator)

# Requirements

* Java 8

* Gradle

# Setup

* Clone the repository.

* Execute `cd spring-jpa-gradle`

# Deployment

There are two ways to deploy the web application.

As this is a Maven project, it can be imported in an IDE such as Eclipse or IntelliJ as a Maven Project.
Create a Run configuration -> Java Application. Once done, select App as a main-class. The application will be deployed to an embedded TOMCAT container

Build and compile the project from command line. Navigate to the project root using the command line, and execute the following command `gradle bootRun`. You will need maven plugin for that.

The RESTful services can be invoked after either steps is performed.

IMPORTANT: these two instructions are mutually exclusive.


# API Endpoints

## Create Station

`POST /iheartmedia/station`

Creates a station.

### Request Body ###

| Name | Type | Description | Required  |
| :---         |     :---:      |          :--- |      :---:      |
| stationId  | string | Unique station id |true
| stationName | string | Station name |true
| callSign | string | Unique call sign |true
| hdEnabled | string | HD Enabled |false

The sample request is given below:

```
{
    "stationId": "KISS-FM",
    "hdEnabled": false,
    "callSign": "KISS",
    "name": "102.7 KIIS-FM-Los Angeles",
    "createdAt": <UTC Timestamp when the station was created.>
}
```

### Response Body

A response status code will be 201 Created and will have the following characteristics.

| Name | Type | Description | Required  |
| :---         |     :---:      |          :--- |      :---:      |
| stationId  | string | Unique station id |true
| stationName | string | Station name |true
| callSign | string | Unique call sign |true
| hdEnabled | string | Indicator as whether the statino is HD enabled |true
| createdAt| | timestamp | UTC Timestamp| true

### Error Response Body

The error response body represents an error of errors returned as 200 OK

| Name | Type | Description | Read only |
| :---         |     :---:      |          :--- |      :---:      |
| errors  | array | array of error objects |true |

where each error object consists of the following fields

| Name | Type | Description | Read only |
| :---         |     :---:      |          :--- |      :---:      |
| error code  | number | error code indicating the kind of error|true |
| error message  | string | user friendly message |true |

Some of the sample responses are given below

##### Station already created

```
{
    "errors": [
        {
            "code": 409,
            "message": "Station information can not be persisted. Check if the station information already exists."
        }
    ]
}
```

## Update Station

`PUT /iheartmedia/station/<stationId>`

Updates the station information. Station ID is considered to be unique, and can not be updated, once updated.

### Request Body

The request body can only consist of maximum of three fields given. A missing field is ignored by the system. Station ID can not be updated. Some of the expected fields are given below.

| Name | Type | Description | Required  |
| :---         |     :---:      |          :--- |      :---:      
| stationName | string | Station name |false
| callSign | string | Unique call sign |false
| hdEnabled | string | HD Enabled |false

The request body is given below

```
{
    "hdEnabled": false,
    "callSign": "KISS",
    "name": "102.7 KIIS-FM-Los Angeles"
}
```

### Response Body

The response will return a 200 OK status. The response body will have the following attributes.

| Name | Type | Description | Required  |
| :---         |     :---:      |          :--- |      :---:      |
| stationId  | string | Unique station id |true
| stationName | string | Station name |true
| callSign | string | Unique call sign |true
| hdEnabled | string | Indicator as whether the station is HD enabled |true
| updatedAt| timestamp | UTC Timestamp| true

A sample response body is given below.

```
{
    "stationId": "KISS-FM",
    "hdEnabled": true,
    "callSign": "New2",
    "name": "Updated Station name",
    "updatedAt": "2018-12-09T11:54:52.407"
}
```

### Error Response Body

The error response encapsulates an array of errors.

| Name | Type | Description | Read only |
| :---         |     :---:      |          :--- |      :---:      |
| errors  | array | array of error objects |true |

where each error object consists of the following data sets.

| Name | Type | Description | Read only |
| :---         |     :---:      |          :--- |      :---:      |
| error code  | number | error code indicating the kind of error|true |
| error message  | string | user friendly message |true |

Some of the sample responses are given below

#### Station ID not found

This will return a 404 error if no record for the quiz was found in the database.

```
{
    "errors": [
        {
            "code": 404,
            "message": "Station was not found for station id: KISS-FM-1."
        }
    ]
}
```

#### Invalid Call Sign Length

```
{
    "errors": [
        {
            "code": 400,
            "message": "Call sign New23 is of invalid length. Station call sign must be 4 characters long."
        }
    ]
}
```

### Delete Station

`DELETE /iheartmedia/station/<stationId>`

Deletes the station by station id and returns the deleted station entity.

#### Response Body

The response will return a 200 OK status. The response body will have the following attributes.

| Name | Type | Description | Required  |
| :---         |     :---:      |          :--- |      :---:      |
| stationId  | string | Unique station id |true
| stationName | string | Station name |true
| callSign | string | Unique call sign |true
| hdEnabled | string | Indicator as whether the station is HD enabled |true
| deletedAt| timestamp | UTC Timestamp| true

The sample response is given below:

```
{
    "stationId": "KISS-FM",
    "hdEnabled": true,
    "callSign": "New2",
    "name": "Updated Station name",
    "deletedAt": "2018-12-09T12:03:03.59"
}
```

#### Error Response Body

The error response encapsulates an array of errors.

| Name | Type | Description | Read only |
| :---         |     :---:      |          :--- |      :---:      |
| errors  | array | array of error objects |true |

where each error object consists of the following data sets.

| Name | Type | Description | Read only |
| :---         |     :---:      |          :--- |      :---:      |
| error code  | number | error code indicating the kind of error|true |
| error message  | string | user friendly message |true |

Some of the sample responses are given below

##### Station Id Not Found

```
{
    "errors": [
        {
            "code": 404,
            "message": "Station was not found for station id: KISS-FM."
        }
    ]
}
```

### Fetch All Stations

`GET /iheartmedia/stations`

Returns all stations

#### Response Body

The response will return a 200 OK status. The response body will comprise an array of station objects, each of which will encapsulate the following attributes.


| Name | Type | Description | Required  |
| :---         |     :---:      |          :--- |      :---:      |
| stationId  | string | Unique station id |true
| stationName | string | Station name |true
| callSign | string | Unique call sign |true
| hdEnabled | string | Indicator as whether the station is HD enabled |true

The sample response is given below

```
[
    {
        "stationId": "KISS-FM",
        "hdEnabled": true,
        "callSign": "WISS",
        "name": "102.7 KIIS-FM-Los Angeles"
    }
]
```

### Fetch By Station Id

`GET /iheartmedia/station/id/<stationId>`

Returns unique station by station id

#### Response Body

The response will return a 200 OK status. The response body comprise of the following attributes

| Name | Type | Description | Required  |
| :---         |     :---:      |          :--- |      :---:      |
| stationId  | string | Unique station id |true
| stationName | string | Station name |true
| callSign | string | Unique call sign |true
| hdEnabled | string | Indicator as whether the station is HD enabled |true

The sample response is given below

```
    {
        "stationId": "KISS-FM",
        "hdEnabled": true,
        "callSign": "WISS",
        "name": "102.7 KIIS-FM-Los Angeles"
    }
```

#### Error Response Body

The error response encapsulates an array of errors.

| Name | Type | Description | Read only |
| :---         |     :---:      |          :--- |      :---:      |
| errors  | array | array of error objects |true |

where each error object consists of the following data sets.

| Name | Type | Description | Read only |
| :---         |     :---:      |          :--- |      :---:      |
| error code  | number | error code indicating the kind of error|true |
| error message  | string | user friendly message |true |

Some of the sample responses are given below

##### Station Id Not Found

```
{
    "errors": [
        {
            "code": 404,
            "message": "Station was not found for station id: KISS-FM."
        }
    ]
}
```

### Fetch By Station Name

`GET /iheartmedia/station/name/<stationId>`

Returns unique station by station name

#### Response Body

The response will return a 200 OK status. The response body will comprise of the following attributes

| Name | Type | Description | Required  |
| :---         |     :---:      |          :--- |      :---:      |
| stationId  | string | Unique station id |true
| stationName | string | Station name |true
| callSign | string | Unique call sign |true
| hdEnabled | string | Indicator as whether the station is HD enabled |true

The sample response is given below

```
    {
        "stationId": "KISS-FM",
        "hdEnabled": true,
        "callSign": "WISS",
        "name": "102.7 KIIS-FM-Los Angeles"
    }
```

#### Error Response Body

The error response encapsulates an array of errors.

| Name | Type | Description | Read only |
| :---         |     :---:      |          :--- |      :---:      |
| errors  | array | array of error objects |true |

where each error object consists of the following data sets.

| Name | Type | Description | Read only |
| :---         |     :---:      |          :--- |      :---:      |
| error code  | number | error code indicating the kind of error|true |
| error message  | string | user friendly message |true |

Some of the sample responses are given below

##### Station Name Not Found

```
{
    "errors": [
        {
            "code": 404,
            "message": "Station was not found for station name: 102.7 KIIS-FM-Los Angeles-2."
        }
    ]
}
```

### Swagger API

`GET /swagger-ui.html`

### Actuator

`GET /actuator/health`

`GET /actuator/info`