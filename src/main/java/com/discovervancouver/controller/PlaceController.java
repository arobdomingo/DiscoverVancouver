package com.discovervancouver.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;

import com.discovervancouver.dto.CreatePlaceRequest;
import com.discovervancouver.dto.PlaceResponse;
import com.discovervancouver.dto.UpdatePlaceRequest;
import com.discovervancouver.mapper.PlaceMapper;

import com.discovervancouver.model.Place;
import com.discovervancouver.service.PlaceService;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService placeService;
    private final PlaceMapper placeMapper;
    
    public PlaceController(PlaceService placeService, PlaceMapper placeMapper) {
        this.placeService = placeService;
        this.placeMapper = placeMapper;
    }

    @GetMapping
    public List<PlaceResponse> getPlaces(@RequestParam(required = false) String category) {
        return placeService.getPlaces(category)
            .stream()
            .map(placeMapper::toResponse)
            .toList();
    }

    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceResponse> getPlaceById(@PathVariable String placeId) {
        return placeService.getPlaceById(placeId)
            .map(placeMapper::toResponse)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PlaceResponse> createPlace(@Valid @RequestBody CreatePlaceRequest request, UriComponentsBuilder uriBuilder){

        Place place = placeMapper.toEntity(request);
        Place savedPlace = placeService.createPlace(place);

        URI location = uriBuilder
            .path("/api/places/{placeId}")
            .buildAndExpand(savedPlace.getId())
            .toUri();

        return ResponseEntity
            .created(location)
            .body(placeMapper.toResponse(savedPlace));
    }

    @PutMapping("/{placeId}")
    public ResponseEntity<PlaceResponse> updatePlace(@PathVariable String placeId, @Valid @RequestBody UpdatePlaceRequest request){
        Place updatedPlace = placeMapper.toEntity(placeId, request);

        return placeService.updatePlace(placeId, updatedPlace)
            .map(placeMapper::toResponse)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{placeId}")
    public ResponseEntity<Void> deletePlace(@PathVariable String placeId){
        boolean deleted = placeService.deletePlace(placeId);

        if(!deleted){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}

