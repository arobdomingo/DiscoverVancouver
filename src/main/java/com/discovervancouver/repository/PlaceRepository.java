package com.discovervancouver.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.discovervancouver.model.Place;

public interface PlaceRepository extends MongoRepository<Place, String> {

    List<Place> findByCategoryIgnoreCase(String category);
    
}
