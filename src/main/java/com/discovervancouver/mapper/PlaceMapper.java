package com.discovervancouver.mapper;

import org.springframework.stereotype.Component;

import com.discovervancouver.model.Place;
import com.discovervancouver.dto.CreatePlaceRequest;
import com.discovervancouver.dto.UpdatePlaceRequest;
import com.discovervancouver.dto.PlaceResponse;

@Component
public class PlaceMapper{

    public Place toEntity(CreatePlaceRequest request){
        return new Place(
            null,
            request.name(),
            request.category(),
            request.description(),
            request.address(),
            request.priceLevel(),
            request.indoor(),
            request.openNow(),
            request.latitude(),
            request.longitude(),
            request.tags()
        );
    }

    public Place toEntity(String placeId, UpdatePlaceRequest request){
        return new Place(
            placeId,
            request.name(),
            request.category(),
            request.description(),
            request.address(),
            request.priceLevel(),
            request.indoor(),
            request.openNow(),
            request.latitude(),
            request.longitude(),
            request.tags()
        );
    }

    public PlaceResponse toResponse(Place place){
        return new PlaceResponse(
            place.getId(),
            place.getName(),
            place.getCategory(),
            place.getDescription(),
            place.getAddress(),
            place.getPriceLevel(),
            place.isIndoor(),
            place.isOpenNow(),
            place.getLatitude(),
            place.getLongitude(),
            place.getTags()
        );
    }

}
