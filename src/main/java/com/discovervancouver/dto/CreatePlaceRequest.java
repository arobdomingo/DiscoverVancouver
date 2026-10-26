package com.discovervancouver.dto;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

import com.discovervancouver.model.OpeningPeriod;

public record CreatePlaceRequest(

    @NotBlank
    @Size(max = 120, message = "name must contain at most 120 characters")
    String name,

    @NotBlank
    @Size(max = 50, message = "category must contain at most 50 characters")
    String category,

    @NotBlank
    @Size(max = 1000, message = "description must contain at most 1000 characters")
    String description,

    @NotBlank
    @Size(max = 250, message = "address must contain at most 250 characters")
    String address,

    @Min(0)
    @Max(3)
    int priceLevel,

    boolean indoor,

    @NotNull
    Map<DayOfWeek, List<@Valid OpeningPeriod>> openingHours,

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    double latitude,

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    double longitude,

    @NotNull
    @Size(max = 20, message = "a place may contain at most 20 tags")
    List<
        @NotBlank(message = "tags must not be blank")
        @Size(max = 50, message = "each tag must contain at most 50 characters")
        String
        >
    tags

){
}
