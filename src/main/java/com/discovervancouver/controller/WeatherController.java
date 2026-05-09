package com.discovervancouver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.discovervancouver.dto.WeatherResponse;
import com.discovervancouver.service.WeatherService;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService){
        this.weatherService = weatherService;
    }

    @GetMapping("/current")
    public WeatherResponse getCurrentWeather(
        @RequestParam double lat,
        @RequestParam double lng
    ) {
        return weatherService.getCurrentWeatherResponse(lat, lng);
    }

}
