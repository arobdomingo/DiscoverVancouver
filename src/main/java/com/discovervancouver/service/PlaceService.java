package com.discovervancouver.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.discovervancouver.model.Place;
import com.discovervancouver.repository.PlaceRepository;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    
    public PlaceService(PlaceRepository placeRepository){
        this.placeRepository = placeRepository;
    }

    public List<Place> getPlaces(String category){
        if(category == null || category.isBlank()){
            return placeRepository.findAll();
        }
        return placeRepository.findByCategoryIgnoreCase(category);
    }
    
    public Optional<Place> getPlaceById(String placeId){
        return placeRepository.findById(placeId);
    }

    public Place createPlace(Place place){
        return placeRepository.save(place);
    }


    public boolean deletePlace(String placeId){
        if(!placeRepository.existsById(placeId)){
            return false;
        }

        placeRepository.deleteById(placeId);
        return true;
    }

    public Optional<Place> updatePlace(String placeId, Place updatedPlace){
        if(!placeRepository.existsById(placeId)){
            return Optional.empty();
        }

        updatedPlace.setId(placeId);
        return Optional.of(placeRepository.save(updatedPlace));
    }
}
