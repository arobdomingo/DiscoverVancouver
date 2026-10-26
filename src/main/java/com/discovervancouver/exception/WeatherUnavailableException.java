package com.discovervancouver.exception;

public class WeatherUnavailableException extends RuntimeException {

    public WeatherUnavailableException(
        String message,
        Throwable cause
    ) {
        super(message, cause);
    }

}