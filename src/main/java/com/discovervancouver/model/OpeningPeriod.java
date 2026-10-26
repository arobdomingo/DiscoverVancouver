package com.discovervancouver.model;

import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

public record OpeningPeriod(
    @NotNull LocalTime opensAt,
    @NotNull LocalTime closesAt
) {
}
