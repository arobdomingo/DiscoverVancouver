package com.discovervancouver.service;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.discovervancouver.dto.WeatherResponse;
import com.discovervancouver.exception.WeatherUnavailableException;

@Service
public class WeatherService {
    
    private final RestClient restClient;

    public WeatherService(
        @Value("${app.weather.base-url}") String baseUrl,
        @Value("${app.weather.connect-timeout}") Duration connectTimeout,
        @Value("${app.weather.read-timeout}") Duration readTimeout
    ) {
        HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(connectTimeout)
            .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);

        requestFactory.setReadTimeout(readTimeout);

        this.restClient = RestClient.builder()
            .baseUrl(baseUrl)
            .requestFactory(requestFactory)
            .build();
    }

    public WeatherResponse getCurrentWeatherResponse(double latitude, double longitude){
        try {
            Map<?, ?> response = restClient.get()
                .uri(uriBuilder-> uriBuilder
                    .path("/forecast")
                    .queryParam("latitude", latitude)
                    .queryParam("longitude", longitude)
                    .queryParam(
                        "current",
                        "temperature_2m,precipitation,rain,weather_code"
                    )
                    .build()
                )
                .retrieve()
                .body(Map.class);

            if (response == null) {
                throw new IllegalStateException(
                    "Open-Meteo returned an empty response"
                );
            }

            Object currentValue = response.get("current");

            if(!(currentValue instanceof Map<?, ?> current)) {
                throw new IllegalStateException(
                    "Open-Meteo response did not contain current weather"
                );
            }

            double temperature = getNumber(
                current,
                "temperature_2m"
            ).doubleValue();

            double precipitation = getNumber(
                current,
                "precipitation"
            ).doubleValue();

            double rain = getNumber(
                current,
                "rain"
            ).doubleValue();

            int weatherCode = getNumber(
                current,
                "weather_code"
            ).intValue();

            boolean rainy = precipitation > 0 || rain > 0;

            return new WeatherResponse(
                temperature,
                precipitation,
                weatherCode,
                rainy
            );

        } catch (RestClientException | IllegalStateException exception) {
            throw new WeatherUnavailableException(
                "Current weather is temporarily unavailable",
                exception
            );
        }
    }

    private Number getNumber(
        Map<?, ?> values,
        String fieldName
    ) {
        Object value = values.get(fieldName);

        if(!(value instanceof Number number)) {
            throw new IllegalStateException(
                "Open-Meteo response contains an invalid field: "
                    + fieldName
            );
        }

        return number;
    }

}
