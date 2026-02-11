package com.example.api4tp.Models;

import androidx.annotation.NonNull;

import com.example.api4tp.BuildConfig;

import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpClient {
    private static final String API_KEY = BuildConfig.API_KEY;
    public static final String API_URL = "https://api.openrouteservice.org/";
    public static final String UNSUCCESSFUL_REQUEST = "Wyszukiwanie nie powiodło się";
    public static final String NO_RESULTS_FOUND = "Brak wyników w Twojej okolicy";
    public static final String OTHER_ISSUE = "Wyszukiwanie nie powiodło się - sprawdź swoje połączenie internetowe";
    private final MapService service;
    private Consumer<String> errorHandler;

    public HttpClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(MapService.class);
    }

    public void searchPlace(String query, double lat, double lon, int radius, Consumer<List<Place>> foundPlacesHandler) {
        Call<ApiResponse> call = service.searchPlaces(API_KEY, query, lat, lon, lat, lon, radius);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if(!response.isSuccessful() || response.body() == null) {
                    if(errorHandler != null) errorHandler.accept(UNSUCCESSFUL_REQUEST);
                    return;
                }

                final var places = response.body().getPlaces();
                if(places.isEmpty()) {
                    if(errorHandler != null) errorHandler.accept(NO_RESULTS_FOUND);
                    return;
                }

                foundPlacesHandler.accept(places);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                errorHandler.accept(OTHER_ISSUE);
            }
        });
    }

    public void setErrorHandler(Consumer<String> handler) {
        errorHandler = handler;
    }
}
