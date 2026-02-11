package com.example.api4tp.Controllers;

import android.Manifest;
import android.view.View;
import android.widget.ImageView;

import com.example.api4tp.Models.HttpClient;
import com.example.api4tp.Models.GeoClient;
import com.example.api4tp.UI_Helpers.MainMap;
import com.example.api4tp.UI_Helpers.MainSearch;

import org.osmdroid.util.GeoPoint;

import java.util.function.Consumer;

public class MainController {
    private final Consumer<String[]> permissionSupplier;
    private final MainSearch mainSearch;
    private final MainMap mainMap;
    private final HttpClient apiClient;
    private final GeoClient geoClient;

    public MainController(Consumer<String[]> permissionSupplier, Consumer<String> errorHandler,
                          ImageView zseIv, MainSearch mainSearch, MainMap mainMap, HttpClient httpClient, GeoClient geoClient) {
        this.mainSearch = mainSearch;
        this.mainMap = mainMap;
        this.apiClient = httpClient;
        this.geoClient = geoClient;
        this.permissionSupplier = permissionSupplier;

        zseIv.setOnClickListener(this::handleZse);
        mainSearch.setQuerySubmitHandler(this::handleSearch);
        mainSearch.setSearchCloseHandler(this::handleSearchClose);
        httpClient.setErrorHandler(errorHandler);
        geoClient.setNewLocationHandler(this::handleNewCurrentLocation);
        geoClient.setLocaterAvailableHandler(this::handleLocaterAvailable);
        geoClient.setLocateButtonHandler(this::handleLocate);
    }

    private void handleZse(View v) {
        mainSearch.clearFocus();
        mainMap.setLocation(MainMap.ZSE_LOCATION);
        mainMap.zoom();
    }

    private boolean handleSearch(String query) {
        GeoPoint location;
        if(geoClient.isWorking() && mainMap.isCurrentMarkerDisplayed() && geoClient.getLastLocation() != null) location = geoClient.getLastLocation();
        else location = MainMap.ZSE_LOCATION;

        final var lat = location.getLatitude();
        final var lon = location.getLongitude();
        apiClient.searchPlace(query, lat, lon, MainMap.MAX_RADIUS_KM, places -> {
            mainMap.clearSearchMarkers();
            mainSearch.clearFocus();

            places.sort((o1, o2) -> (int) (o1.getDistance() - o2.getDistance()));
            for(final var place : places) mainMap.setSearchMarker(new GeoPoint(place.getLat(), place.getLon()), place.getName());
            mainMap.invalidate();

            final var nearestSearchPlace = places.get(0);
            mainMap.setLocation(new GeoPoint(nearestSearchPlace.getLat(), nearestSearchPlace.getLon()));
            mainMap.zoom();
        });

        return true;
    }

    private void handleSearchClose() {
        mainMap.clearSearchMarkers();
        mainMap.invalidate();
    }

    private void handleLocate(View v) {
        if(geoClient.isWorking()) {
            geoClient.disableFetchingLoop();
            mainMap.setCurrentMarkerDisplay(false);
            mainMap.invalidate();
        } else {
            requireLocationPermissions();
            geoClient.setupFetchingLoop();
        }
    }

    private void handleNewCurrentLocation(GeoPoint location) {
        mainMap.moveCurrentMarker(location);
        mainMap.invalidate();
    }

    private void handleLocaterAvailable(GeoPoint firstLocation) {
        mainMap.setCurrentMarkerDisplay(true);
        mainMap.invalidate();
        mainMap.setLocation(firstLocation);
        mainMap.zoom();
    }

    private void requireLocationPermissions() {
        permissionSupplier.accept(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }
}
