package com.discovervancouver.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.discovervancouver.model.Place;
import com.discovervancouver.repository.PlaceRepository;

@Configuration
public class DataSeeder {
    
    @Bean
    CommandLineRunner seedPlaces(PlaceRepository placeRepository){
        return args -> {
            if(placeRepository.count() > 0) {
                return;
            }

            placeRepository.saveAll(List.of(
                new Place(
                        "place1",
                        "Breka Bakery & Cafe",
                        "cafe",
                        "Casual cafe with pastries and study-friendly seating.",
                        "855 Davie St, Vancouver, BC",
                        1,
                        true,
                        true,
                        49.2810,
                        -123.1170,
                        List.of("cafe", "study", "casual", "late-night", "wifi")
                ),
                new Place(
                        "place2",
                        "Vancouver Public Library Central",
                        "study",
                        "Iconic central library with quiet indoor work areas.",
                        "350 W Georgia St, Vancouver, BC",
                        0,
                        true,
                        true,
                        49.2797,
                        -123.1156,
                        List.of("study", "quiet", "free", "rainy-day", "indoor")
                ),
                new Place(
                        "place3",
                        "Nelson Park",
                        "park",
                        "Small downtown green space for a calm outdoor reset.",
                        "1030 Bute St, Vancouver, BC",
                        0,
                        false,
                        true,
                        49.2813,
                        -123.1308,
                        List.of("park", "quiet", "walk", "free", "outdoor")
                )
            ));

        };
    }

}
