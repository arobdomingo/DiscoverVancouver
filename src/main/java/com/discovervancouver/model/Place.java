package com.discovervancouver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Document(collection = "places")
public class Place {
    
    @Id
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String category;

    @NotBlank
    private String description;

    @NotBlank
    private String address;

    @Min(0)
    @Max(3)
    private int priceLevel;

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private double latitude;
    
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private double longitude;

    @NotNull
    private List<String> tags;


    private boolean indoor;
    private boolean openNow;

    public Place(String id, String name, String category, String description, String address, int priceLevel, boolean indoor, boolean openNow, double latitude, double longitude, List<String> tags) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.address = address;
        this.priceLevel = priceLevel;
        this.indoor = indoor;
        this.openNow = openNow;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tags = tags;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public boolean isIndoor() {
        return indoor;
    }

    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }

    public List<String> getTags(){
        return tags;
    }

    public boolean isOpenNow(){
        return openNow;
    }

}
