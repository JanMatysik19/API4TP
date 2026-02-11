package com.example.api4tp.Models;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MapService {
    @GET("geocode/search")
    Call<ApiResponse> searchPlaces(
            @Query("api_key") String apiKey,
            @Query("text") String text,
            @Query("focus.point.lat") double fLat,
            @Query("focus.point.lon") double fLon,
            @Query("boundary.circle.lat") double bLat,
            @Query("boundary.circle.lon") double bLon,
            @Query("boundary.circle.radius") int radius
    );
}
