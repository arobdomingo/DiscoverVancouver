package com.discovervancouver.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.discovervancouver.dto.ApiErrorResponse;
import com.discovervancouver.exception.WeatherUnavailableException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleRequestBodyValidation(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
    ) {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        exception.getBindingResult()
            .getFieldErrors()
            .forEach(fieldError -> addError(
                errors,
                fieldError.getField(),
                fieldError.getDefaultMessage()
            ));

        ApiErrorResponse response = createBadRequestResponse(
            "Request body validation failed",
            request.getRequestURI(),
            errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleParameterValidation(
        HandlerMethodValidationException exception,
        HttpServletRequest request
    ){
        Map<String, List<String>> errors = new LinkedHashMap<>();

        for(ParameterValidationResult result : exception.getParameterValidationResults()){
            String parameterName = result
                .getMethodParameter()
                .getParameterName();

            if (parameterName == null){
                parameterName = "parameter";
            }

            for (MessageSourceResolvable error : result.getResolvableErrors()){
                addError(errors, parameterName, error.getDefaultMessage());
            }
        }

        ApiErrorResponse response = createBadRequestResponse(
            "Request parameter validation failed",
            request.getRequestURI(),
            errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    private void addError(
    Map<String, List<String>> errors, String field, String message
    ) {
        errors.computeIfAbsent(field, ignored -> new ArrayList<>()
        ).add(message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
        MethodArgumentTypeMismatchException exception,
        HttpServletRequest request
    ) {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        addError(
            errors,
            exception.getName(),
            "Invalid value: " + exception.getValue()
        );

        ApiErrorResponse response = createBadRequestResponse(
            "A request parameter has the wrong type",
            request.getRequestURI(),
            errors
        );

        return ResponseEntity.badRequest().body(response);

    }

    private ApiErrorResponse createBadRequestResponse(
        String message,
        String path,
        Map<String, List<String>> errors
    ) {
        return new ApiErrorResponse(
            Instant.now(),
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            message,
            path,
            errors
        );
    }

    @ExceptionHandler(WeatherUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleWeatherUnavailable(
        WeatherUnavailableException exception,
        HttpServletRequest request
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
            Instant.now(),
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
            exception.getMessage(),
            request.getRequestURI(),
            Map.of()
        );

        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(response);
    }

}
