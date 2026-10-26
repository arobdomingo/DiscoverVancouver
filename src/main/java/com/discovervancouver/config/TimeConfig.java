package com.discovervancouver.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfig {

    @Bean
    Clock applicationClock() {
        return Clock.systemUTC();
    }

    @Bean
    ZoneId applicationZone(@Value("${app.time-zone}") String timeZone) {
        return ZoneId.of(timeZone);
    }
}
