package com.discovervancouver.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.discovervancouver.dto.RecommendationResponse;
import com.discovervancouver.model.Place;

@Service
public class RecommendationService {
    
    private final PlaceService placeService;
    private final DistanceService distanceService;
    private final WeatherService weatherService;

    public RecommendationService(PlaceService placeService, DistanceService distanceService, WeatherService weatherService) {
        this.placeService = placeService;
        this.distanceService = distanceService;
        this.weatherService = weatherService;
    }

    public List<RecommendationResponse> getRecommendations(double latitude, double longitude, String category, Boolean indoorPreferred, int maxWalkingMinutes, String budgetPreference, String atmosphere) {
        boolean rainyWeather = weatherService.getCurrentWeatherResponse(latitude, longitude).rainy();

        return placeService.getPlaces(null).stream()
            .map(place -> scorePlace(place, latitude, longitude, category, indoorPreferred, maxWalkingMinutes, budgetPreference, atmosphere, rainyWeather))
            .filter(recommendation -> recommendation.walkingMinutes() <= maxWalkingMinutes)
            .sorted(Comparator.comparingInt(RecommendationResponse::score).reversed())
            .toList();
    }

    private RecommendationResponse scorePlace(Place place, double latitude, double longitude, String category, Boolean indoorPreferred, int maxWalkingMinutes, String budgetPreference, String atmosphere, boolean rainyWeather) {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        if(category != null && !category.isBlank() && place.getCategory().equalsIgnoreCase(category)){
            score += 25;
            reasons.add("Matches your " + category + " preference");
        }

        if(indoorPreferred != null && place.isIndoor() == indoorPreferred) {
            score += 15;
            reasons.add(indoorPreferred
                ? "Works well as an indoor option"
                : "Works well as an outdoor option"
            );
        }

        if(place.isOpenNow()){
            score += 20;
            reasons.add("Open now");
        }

        if(budgetPreference != null && !budgetPreference.isBlank()) {
            Integer preferredPriceLevel = priceLevelForBudget(budgetPreference);

            if(preferredPriceLevel != null && place.getPriceLevel() <= preferredPriceLevel){
                score += 10;
                reasons.add("Fits your " + budgetPreference + " budget preference");
            }
        }

        if(atmosphere != null && !atmosphere.isBlank() && place.getTags().stream().anyMatch(tag -> tag.equalsIgnoreCase(atmosphere))){
            score += 12;
            reasons.add("Matches your " + atmosphere +" atmosphere preference");
        }

        if(rainyWeather && place.isIndoor()){
            score += 10;
            reasons.add("Good indoor option for rainy weather");
        }

        if(rainyWeather && place.getTags().stream().anyMatch(tag -> tag.equalsIgnoreCase("rainy-day"))){
            score += 10;
            reasons.add("Tagged as a good rainy-day option");
        }

        int distanceMeters = distanceService.distanceMeters(
            latitude,
            longitude,
            place.getLatitude(),
            place.getLongitude()
        );

        int walkingMinutes = distanceService.walkingMinutes(distanceMeters);
        int distanceScore = Math.max(0, 20 - walkingMinutes);
        score += distanceScore;

        reasons.add("About " + walkingMinutes + " minutes away on foot");

        if(walkingMinutes <= maxWalkingMinutes) {
            reasons.add("Within your " + maxWalkingMinutes + " minute walking range");
        }

        return new RecommendationResponse(
            place.getId(),
            place.getName(),
            place.getCategory(),
            place.getDescription(),
            place.getAddress(),
            place.getPriceLevel(),
            place.isIndoor(),
            place.isOpenNow(),
            place.getTags(),
            score,
            distanceMeters,
            walkingMinutes,
            rainyWeather,
            reasons
        );
    }

    private Integer priceLevelForBudget(String budgetPreference){
        return switch (budgetPreference.toLowerCase()){
            case "free" -> 0;
            case "low" -> 1;
            case "medium" -> 2;
            case "high" -> 3;
            default -> null;
        };
    }

}