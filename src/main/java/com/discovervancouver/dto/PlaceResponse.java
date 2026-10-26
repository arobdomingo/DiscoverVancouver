package com.discovervancouver.dto;

import java.util.List;

public record PlaceResponse(
    String id,
    String name,
    String category,
    String description,
    String address,
    int priceLevel,
    boolean indoor,
    boolean openNow,
    double latitude,
    double longitude,
    List<String> tags
) {
}
