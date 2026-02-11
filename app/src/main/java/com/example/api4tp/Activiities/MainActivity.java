package com.example.api4tp.Activiities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.example.api4tp.HttpClient;
import com.example.api4tp.GeoClient;
import com.example.api4tp.Controllers.MainController;
import com.example.api4tp.MainMap;
import com.example.api4tp.MainSearch;
import com.example.api4tp.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String[]> permissionLauncher;
    private MapView mainMv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> { }
        );

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

        final var mainMap = new MainMap(this, mainMv);
        final var mainSearch = new MainSearch(main, searchCv, searchSv, getResources().getDisplayMetrics());
        final var client = new HttpClient();
        final var locater = new GeoClient(this, locateCv, locateIv);
        new MainController(this::requirePermissions, this::displayError, zseIv, mainSearch, mainMap, client, locater);

        mainMap.setMapLocation(MainMap.ZSE_LOCATION);
        mainMap.setLocation(MainMap.ZSE_LOCATION);
    }

    private void displayError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void handleViewPupilInfo(View v) {
        final var intent = new Intent(MainActivity.this, PupilInfoActivity.class);
        startActivity(intent);
    }

    private void requirePermissions(String[] permissions) {
        List<String> tmp = new ArrayList<>();
        for(final var p : permissions)
            if(ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) tmp.add(p);

        String[] filtered = new String[tmp.size()];
        for(int i = 0; i < tmp.size(); i++)
            filtered[i] = tmp.get(0);

        permissionLauncher.launch(filtered);
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