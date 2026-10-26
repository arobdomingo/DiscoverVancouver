package com.discovervancouver.service;

import java.time.Clock;
import java.time.LocalTime;
import java.time.ZoneId;

import org.springframework.stereotype.Service;

@Service
public class TimeOfDayService {

    private final Clock clock;
    private final ZoneId applicationZone;

    public TimeOfDayService(Clock clock, ZoneId applicationZone) {
        this.clock = clock;
        this.applicationZone = applicationZone;
    }

    public String currentTag() {
        LocalTime time = LocalTime.now(clock.withZone(applicationZone));

        if (time.isBefore(LocalTime.of(6, 0))) {
            return "late-night";
        }
        if (time.isBefore(LocalTime.NOON)) {
            return "morning";
        }
        if (time.isBefore(LocalTime.of(17, 0))) {
            return "afternoon";
        }
        if (time.isBefore(LocalTime.of(22, 0))) {
            return "evening";
        }
        return "late-night";
    }
}
