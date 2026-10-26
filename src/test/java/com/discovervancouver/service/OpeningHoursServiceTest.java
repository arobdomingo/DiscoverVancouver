package com.discovervancouver.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.discovervancouver.model.OpeningPeriod;
import com.discovervancouver.model.Place;

class OpeningHoursServiceTest {

    private final ZoneId vancouver = ZoneId.of("America/Vancouver");
    private final OpeningHoursService openingHoursService =
        new OpeningHoursService(Clock.systemUTC(), vancouver);

    @Test
    void placeIsOpenDuringRegularHours() {
        Place place = placeWithHours(Map.of(
            DayOfWeek.MONDAY,
            List.of(period(9, 0, 17, 0))
        ));

        assertTrue(openingHoursService.isOpenAt(
            place,
            ZonedDateTime.of(2026, 10, 26, 12, 0, 0, 0, vancouver)
        ));
    }

    @Test
    void placeIsClosedAtItsClosingTime() {
        Place place = placeWithHours(Map.of(
            DayOfWeek.MONDAY,
            List.of(period(9, 0, 17, 0))
        ));

        assertFalse(openingHoursService.isOpenAt(
            place,
            ZonedDateTime.of(2026, 10, 26, 17, 0, 0, 0, vancouver)
        ));
    }

    @Test
    void overnightHoursContinueIntoTheFollowingDay() {
        Place place = placeWithHours(Map.of(
            DayOfWeek.FRIDAY,
            List.of(period(18, 0, 2, 0))
        ));

        assertTrue(openingHoursService.isOpenAt(
            place,
            ZonedDateTime.of(2026, 10, 31, 1, 30, 0, 0, vancouver)
        ));
        assertFalse(openingHoursService.isOpenAt(
            place,
            ZonedDateTime.of(2026, 10, 31, 2, 0, 0, 0, vancouver)
        ));
    }

    @Test
    void equalOpeningAndClosingTimesRepresentTwentyFourHours() {
        Place place = placeWithHours(Map.of(
            DayOfWeek.MONDAY,
            List.of(period(0, 0, 0, 0))
        ));

        assertTrue(openingHoursService.isOpenAt(
            place,
            ZonedDateTime.of(2026, 10, 26, 23, 59, 0, 0, vancouver)
        ));
    }

    @Test
    void emptyScheduleIsClosed() {
        Place place = placeWithHours(Map.of());

        assertFalse(openingHoursService.isOpenAt(
            place,
            ZonedDateTime.of(2026, 10, 26, 12, 0, 0, 0, vancouver)
        ));
    }

    @Test
    void incomingMomentIsConvertedToVancouverTime() {
        Place place = placeWithHours(Map.of(
            DayOfWeek.MONDAY,
            List.of(period(9, 0, 17, 0))
        ));

        assertTrue(openingHoursService.isOpenAt(
            place,
            ZonedDateTime.of(
                2026,
                10,
                26,
                19,
                0,
                0,
                0,
                ZoneId.of("UTC")
            )
        ));
    }

    private OpeningPeriod period(
        int openingHour,
        int openingMinute,
        int closingHour,
        int closingMinute
    ) {
        return new OpeningPeriod(
            LocalTime.of(openingHour, openingMinute),
            LocalTime.of(closingHour, closingMinute)
        );
    }

    private Place placeWithHours(
        Map<DayOfWeek, List<OpeningPeriod>> openingHours
    ) {
        return new Place(
            "place1",
            "Test Place",
            "test",
            "A test place.",
            "123 Test St, Vancouver, BC",
            1,
            true,
            openingHours,
            49.2827,
            -123.1207,
            List.of("test")
        );
    }
}
