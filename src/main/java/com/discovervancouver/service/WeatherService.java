package com.discovervancouver.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.discovervancouver.dto.WeatherResponse;

@Service
public class WeatherService {
    
    private final RestClient restClient;

    public WeatherService(){
        this.restClient = RestClient.builder()
            .baseUrl("https://api.open-meteo.com/v1")
            .build();
    }

    public WeatherResponse getCurrentWeatherResponse(double latitude, double longitude){
        Map response = restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/forecast")
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("current", "temperature_2m,precipitation,rain,weather_code")
                .build())
            .retrieve()
            .body(Map.class);
        
        Map current = (Map) response.get("current");

        double temperature = ((Number) current.get("temperature_2m")).doubleValue();
        double precipitation = ((Number) current.get("precipitation")).doubleValue();
        double rain = ((Number) current.get("rain")).doubleValue();
        int weatherCode = ((Number) current.get("weather_code")).intValue();

        boolean rainy = precipitation > 0 || rain > 0;

        return new WeatherResponse(
            temperature,
            precipitation,
            weatherCode,
            rainy
        );

    }

}
