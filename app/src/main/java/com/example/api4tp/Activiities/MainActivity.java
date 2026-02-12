package com.example.api4tp.Activiities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.api4tp.Models.HttpClient;
import com.example.api4tp.Models.GeoClient;
import com.example.api4tp.Controllers.MainController;
import com.example.api4tp.UI_Helpers.MainMap;
import com.example.api4tp.UI_Helpers.MainSearch;
import com.example.api4tp.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;

public class MainActivity extends CustomActivity {
    private MapView mainMv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main);
        mainMv = findViewById(R.id.ma_mainMv);
        CardView searchCv = findViewById(R.id.ma_searchCv);
        SearchView searchSv = findViewById(R.id.ma_searchSv);
        ConstraintLayout main = findViewById(R.id.ma_main);
        ImageView zseIv = findViewById(R.id.ma_zseIv);
        CardView locateCv = findViewById(R.id.ma_locateCv);
        ImageView locateIv = findViewById(R.id.ma_locateIv);
        ConstraintLayout pupilInfoCl = findViewById(R.id.ma_viewPupilCl);

        pupilInfoCl.setOnClickListener(this::handleViewPupilInfo);

        final var mainMap = new MainMap(this::getDrawableResource, mainMv);
        final var mainSearch = new MainSearch(main, searchCv, searchSv, getResources().getDisplayMetrics());
        final var client = new HttpClient();
        final var locater = new GeoClient(this, locateCv, locateIv);
        new MainController(this::requirePermissions, this::displayError,
                zseIv, mainSearch, mainMap, client, locater);

        mainMap.setMapLocation(MainMap.ZSE_LOCATION);
        mainMap.setLocation(MainMap.ZSE_LOCATION);
    }

    private void handleViewPupilInfo(View v) {
        final var intent = new Intent(MainActivity.this, PupilInfoActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainMv.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainMv.onPause();
    }
}