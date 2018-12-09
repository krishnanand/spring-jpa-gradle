// Copyright 2018 Kartik Krishnanand. All Rights Reserved.
package com.iheartmedia.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Utility class to encapsulate any custom initialisation that may be required.iber
 */
@Component
public class ConfigurationHelper {


    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:messages", "classpath:ValidationMessages");
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setCacheSeconds(5);
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public Validator validator() {
        return  new LocalValidatorFactoryBean();
    }
}
