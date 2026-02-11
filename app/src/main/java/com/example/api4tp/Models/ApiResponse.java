package com.example.api4tp.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ApiResponse {
    // features[n].geometry.coordinates[0] - lon
    // features[n].geometry.coordinates[1] - lat
    // features[n].properties.name - name
    @SerializedName("features")
    private List<Feature> features;

    public List<Place> getPlaces() {
        final List<Place> places = new ArrayList<>();
        for(var feature : features) {
            final Place place = new Place();
            place.setLon(feature.geometry.coordinates.get(0));
            place.setLat(feature.geometry.coordinates.get(1));
            place.setName(feature.properties.name);
            place.setDistance(feature.properties.distance);
            places.add(place);
        }

        return places;
    }

    public static class Feature {
        @SerializedName("geometry")
        public Geometry geometry;
        @SerializedName("properties")
        public Properties properties;
    }

    public static class Geometry {
        @SerializedName("coordinates")
        public List<Double> coordinates;
    }

    public static class Properties {
        @SerializedName("name")
        public String name;
        @SerializedName("distance")
        public double distance;
    }
}
