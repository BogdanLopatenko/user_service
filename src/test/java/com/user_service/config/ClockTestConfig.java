package com.user_service.config;

import com.user_service.constant.TestConstant;
import com.user_service.util.MutableClock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.ZoneOffset;

@TestConfiguration
public class ClockTestConfig {

    @Bean
    @Primary
    public MutableClock clock() {
        return new MutableClock(
                TestConstant.DEFAULT_INSTANT,
                ZoneOffset.UTC
        );
    }
}
