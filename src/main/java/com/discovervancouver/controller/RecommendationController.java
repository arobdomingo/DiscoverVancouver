package com.discovervancouver.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.discovervancouver.dto.RecommendationResponse;
import com.discovervancouver.service.RecommendationService;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService){
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public List<RecommendationResponse> getRecommendations(
        @RequestParam double lat,
        @RequestParam double lng,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Boolean indoorPreferred,
        @RequestParam(defaultValue = "20") int maxWalkingMinutes,
        @RequestParam(required = false) String budgetPreference,
        @RequestParam(required = false) String atmosphere
    ){
        return recommendationService.getRecommendations(lat, lng, category, indoorPreferred, maxWalkingMinutes, budgetPreference, atmosphere);
    }
    
}
