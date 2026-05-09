package com.discovervancouver.dto;

public record WeatherResponse (
    double temperature,
    double precipitation,
    int weatherCode,
    boolean rainy
) { }