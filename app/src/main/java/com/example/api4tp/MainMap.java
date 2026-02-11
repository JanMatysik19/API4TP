package com.example.api4tp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

public class MainMap {
    public static final int MAX_RADIUS_KM = 15;
    public static final double MAX_ZOOM = 22;
    public static final double MIN_ZOOM = 13;
    public static final double INIT_ZOOM = 19;
    public static final GeoPoint ZSE_LOCATION = new GeoPoint(52.733646, 15.238512);
    public static final String SCHOOL_LOCATION_TEXT = "Tu praca jest oceniana";

    private final Context ctx;
    private final MapView mainMv;
    private final IMapController mainMvController;
    private CustomMarker currentLocationMarker;

    private final Drawable SEARCH_LOCATION_MARK;
    private final Drawable SCHOOL_LOCATION_MARK;
    private final Drawable CURRENT_LOCATION_MARK;

    public MainMap(Context ctx, MapView mainMv) {
        this.ctx = ctx;
        this.mainMv = mainMv;
        this.mainMvController = mainMv.getController();

        SEARCH_LOCATION_MARK = AppCompatResources.getDrawable(ctx, R.drawable.location_mark);
        SCHOOL_LOCATION_MARK = AppCompatResources.getDrawable(ctx, R.drawable.school);
        CURRENT_LOCATION_MARK = AppCompatResources.getDrawable(ctx, R.drawable.person_mark);

        mainMv.setTileSource(TileSourceFactory.MAPNIK);
        mainMv.setHorizontalMapRepetitionEnabled(false);
        mainMv.setVerticalMapRepetitionEnabled(false);

        mainMv.setBuiltInZoomControls(false);
        mainMv.setMultiTouchControls(true);
        mainMv.setMaxZoomLevel(MAX_ZOOM);
        mainMv.setMinZoomLevel(MIN_ZOOM);

        mainMvController.setZoom(INIT_ZOOM);

        setCurrentMarker(ZSE_LOCATION);
        setCurrentMarkerDisplay(false);
        setSchoolMarker(ZSE_LOCATION, SCHOOL_LOCATION_TEXT);
        invalidate();
    }

    public void zoom() {
        mainMvController.setZoom(INIT_ZOOM);
    }

    public void setMapLocation(GeoPoint location) {
        mainMvController.setCenter(location);
        limitToRadius(location, MAX_RADIUS_KM);
    }

    public void setLocation(GeoPoint location) {
        mainMvController.setCenter(location);
    }

    private void setSchoolMarker(GeoPoint location, String name) {
        final var text = new CustomText(name, location, 35, true, OverlayType.CONSTANT_TEXT);
        mainMv.getOverlays().add(text);
        final var marker = new CustomMarker(location, SCHOOL_LOCATION_MARK, Marker.ANCHOR_BOTTOM, OverlayType.CONSTANT_MARKER);
        mainMv.getOverlays().add(marker);
    }

    private void setCurrentMarker(GeoPoint location) {
        currentLocationMarker = new CustomMarker(location, CURRENT_LOCATION_MARK, Marker.ANCHOR_BOTTOM, OverlayType.CURRENT_MARKER);
        mainMv.getOverlays().add(currentLocationMarker);
    }

    public void setSearchMarker(GeoPoint location, String name) {
        final var text = new CustomText(name, location, 35, true, OverlayType.SEARCH_TEXT);
        mainMv.getOverlays().add(text);
        final var marker = new CustomMarker(location, SEARCH_LOCATION_MARK, Marker.ANCHOR_BOTTOM, OverlayType.SEARCH_MARKER);
        mainMv.getOverlays().add(marker);
    }

    public void setCurrentMarkerDisplay(boolean display) {
        currentLocationMarker.setVisible(display);
    }

    public boolean isCurrentMarkerDisplayed() {
        return currentLocationMarker.isDisplayed();
    }

    public void moveCurrentMarker(GeoPoint location) {
        currentLocationMarker.setPosition(location);
    }

    public void clearSearchMarkers() {
        for(final var overlay : mainMv.getOverlays()) {
            if(!(overlay instanceof ICustomOverlay)) mainMv.getOverlays().remove(overlay);
            else {
                final var o = (ICustomOverlay) overlay;
                if(o.getType() == OverlayType.SEARCH_MARKER ||
                        o.getType() == OverlayType.SEARCH_TEXT) mainMv.getOverlays().remove(overlay);
            }
        }
    }

    public void invalidate() {
        mainMv.postInvalidate();
    }

    private void limitToRadius(GeoPoint point, double radius) {
        var lat = point.getLatitude();
        var lon = point.getLongitude();

        final double latDeg = 40075.00 / 360.00; // Obwód Ziemii / 360 stopni  [km/deg]
        final double rLat = radius / latDeg;
        final double lonDeg = latDeg * Math.cos(Math.toRadians(lat));
        final double rLon = radius / lonDeg;

        final double n = lat + rLat;
        final double s = lat - rLat;
        final double e = lon + rLon;
        final double w = lon - rLon;

        mainMv.setScrollableAreaLimitLatitude(n, s, 0);
        mainMv.setScrollableAreaLimitLongitude(w, e, 0);
    }



    private class CustomText extends Overlay implements ICustomOverlay {
        private final String text;
        private final GeoPoint point;
        private final int size;
        private final boolean bold;
        private final OverlayType type;

        public CustomText(String text, GeoPoint point, int size, boolean bold, OverlayType type) {
            this.text = text;
            this.point = point;
            this.size = size;
            this.bold = bold;
            this.type = type;
        }
        @Override
        public OverlayType getType() {
            return type;
        }
        @Override
        public void draw(Canvas pCanvas, MapView pMapView, boolean pShadow) {
            if(pShadow) return;

            var p = pMapView.getProjection();
            var point = new Point();
            p.toPixels(this.point, point);

            var paint = new Paint();
            paint.setColor(ContextCompat.getColor(ctx, R.color.dark));
            paint.setTextSize(size);
            paint.setFakeBoldText(bold);
            paint.setTextAlign(Paint.Align.CENTER);
            pCanvas.drawText(text, point.x, point.y + size/2, paint);
        }
    }

    private class CustomMarker extends Marker implements ICustomOverlay {
        private final OverlayType type;
        public CustomMarker(GeoPoint point, Drawable icon, float anchor, OverlayType type) {
            super(mainMv);
            this.type = type;
            setPosition(point);
            setAnchor(Marker.ANCHOR_CENTER, anchor);
            setIcon(icon);
        }
        @Override
        public OverlayType getType() {
            return type;
        }
        @Override
        protected boolean onMarkerClickDefault(Marker marker, MapView mapView) {
            return false;
        }
    }

    private interface ICustomOverlay {
        OverlayType getType();
    }

    private enum OverlayType {
        SEARCH_MARKER, CONSTANT_MARKER, CURRENT_MARKER, SEARCH_TEXT, CONSTANT_TEXT
    }
}
