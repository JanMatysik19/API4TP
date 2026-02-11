package com.example.api4tp;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.osmdroid.util.GeoPoint;

import java.util.function.Consumer;

public class GeoClient {
    final public int MIN_ACCURACY = 20;
    final public int MIN_DISTANCE_UPDATE = 7; // In meters
    final private Context ctx;
    final private CardView locateCv;
    final private ImageView locateIv;
    final private ObjectAnimator locateSearchingAnimator;

    private Consumer<GeoPoint> locationHandler;
    private Consumer<GeoPoint> locaterAvailableHandler;
    final private FusedLocationProviderClient client;
    final private LocationRequest request;
    final private LocationCallback callback;
    private Location lastLocation;
    private boolean working = false;
    private boolean found = false;

    public GeoClient(Context ctx, CardView locateCv, ImageView locateIv) {
        this.ctx = ctx;
        this.locateCv = locateCv;
        this.locateIv = locateIv;

        locateSearchingAnimator = (ObjectAnimator) AnimatorInflater.loadAnimator(ctx, R.animator.tick_rotate);
        locateSearchingAnimator.setTarget(locateIv);

        client = LocationServices.getFusedLocationProviderClient(ctx);
        request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build();

        callback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                System.out.println("LOCATION FETCHED");
                if(!working) return;

                final var loc = locationResult.getLastLocation();
                if(loc == null || (loc.hasAccuracy() &&  loc.getAccuracy() > MIN_ACCURACY)) return;

                if(lastLocation != null) {
                    var tmpLast = new Location(loc);
                    tmpLast.setLatitude(lastLocation.getLatitude());
                    tmpLast.setLongitude(lastLocation.getLongitude());
                    if(loc.distanceTo(tmpLast) < MIN_DISTANCE_UPDATE) return;
                }

                final var location = new GeoPoint(loc.getLatitude(), loc.getLongitude());
                locationHandler.accept(location);

                if(!found) {
                    flagAvailable();
                    locaterAvailableHandler.accept(location);
                    found = true;
                }

                lastLocation = loc;
            }
        };
    }

    public void setupFetchingLoop() {
        if(working) return;

        flagSearching();
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            flagDisable();
        } else {
            client.requestLocationUpdates(request, callback, Looper.getMainLooper());
            working = true;
        }
    }

    public void disableFetchingLoop() {
        working = false;
        found = false;
        lastLocation = null;
        client.removeLocationUpdates(callback);
        flagDisable();
    }

    public boolean isWorking() {
        return working;
    }

    public GeoPoint getLastLocation() {
        if(lastLocation == null) return null;
        return new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude());
    }

    public void setNewLocationHandler(Consumer<GeoPoint> handler) {
        locationHandler = handler;
    }
    public void setLocaterAvailableHandler(Consumer<GeoPoint> handler) {
        locaterAvailableHandler = handler;
    }
    public void setLocateButtonHandler(View.OnClickListener handler) {
        locateCv.setOnClickListener(handler);
    }

    private void flagDisable() {
        locateIv.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.locate_disabled));
        locateSearchingAnimator.end();
    }
    private void flagSearching() {
        locateIv.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.locate_searching));
        locateSearchingAnimator.start();
    }
    private void flagAvailable() {
        locateIv.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.locate_found));
        locateSearchingAnimator.end();
    }
}
