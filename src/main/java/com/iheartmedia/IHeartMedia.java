// Copyright 2018 Kartik Krishnanand. All Rights Reserved.

package com.iheartmedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Represents the entry point to the com.iheartmedia.IHeartMedia web application.
 */
@SpringBootApplication
@EnableJpaRepositories(considerNestedRepositories = true)
public class IHeartMedia {

    public static void main(String[] args) {
        SpringApplication.run(IHeartMedia.class, args);
    }

}
