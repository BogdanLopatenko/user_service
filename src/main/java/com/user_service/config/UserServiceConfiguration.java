package com.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class UserServiceConfiguration {

    @Bean
    public Clock clockBean(){

        return Clock.systemUTC();
    }
}
