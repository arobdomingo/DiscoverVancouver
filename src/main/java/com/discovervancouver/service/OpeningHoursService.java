package com.discovervancouver.service;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.discovervancouver.model.OpeningPeriod;
import com.discovervancouver.model.Place;

@Service
public class OpeningHoursService {

    private final Clock clock;
    private final ZoneId applicationZone;

    public OpeningHoursService(Clock clock, ZoneId applicationZone) {
        this.clock = clock;
        this.applicationZone = applicationZone;
    }

    public boolean isOpenNow(Place place) {
        return isOpenAt(place, ZonedDateTime.now(clock));
    }

    public boolean isOpenAt(Place place, ZonedDateTime moment) {
        Map<DayOfWeek, List<OpeningPeriod>> hours = place.getOpeningHours();

        if (hours == null || hours.isEmpty()) {
            return false;
        }

        ZonedDateTime localMoment = moment.withZoneSameInstant(applicationZone);
        DayOfWeek today = localMoment.getDayOfWeek();
        LocalTime time = localMoment.toLocalTime();

        if (isWithinTodaysPeriod(hours.get(today), time)) {
            return true;
        }

        return isWithinPreviousDaysOvernightPeriod(
            hours.get(today.minus(1)),
            time
        );
    }

    private boolean isWithinTodaysPeriod(
        List<OpeningPeriod> periods,
        LocalTime time
    ) {
        if (periods == null) {
            return false;
        }

        return periods.stream().anyMatch(period -> {
            LocalTime opensAt = period.opensAt();
            LocalTime closesAt = period.closesAt();

            if (opensAt.equals(closesAt)) {
                return true;
            }

            if (opensAt.isBefore(closesAt)) {
                return !time.isBefore(opensAt) && time.isBefore(closesAt);
            }

            return !time.isBefore(opensAt);
        });
    }

    private boolean isWithinPreviousDaysOvernightPeriod(
        List<OpeningPeriod> periods,
        LocalTime time
    ) {
        if (periods == null) {
            return false;
        }

        return periods.stream().anyMatch(period ->
            period.opensAt().isAfter(period.closesAt())
                && time.isBefore(period.closesAt())
        );
    }
}
