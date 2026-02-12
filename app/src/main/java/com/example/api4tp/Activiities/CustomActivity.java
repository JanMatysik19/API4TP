package com.example.api4tp.Activiities;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomActivity extends AppCompatActivity {
    private ActivityResultLauncher<String[]> permissionLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> { }
        );
    }

    protected void displayError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    protected Drawable getDrawableResource(int r) {
        try {
            return AppCompatResources.getDrawable(this, r);
        } catch (Exception ignore) { }
        return null;
    }

    protected void requirePermissions(String[] permissions) {
        List<String> tmp = new ArrayList<>();
        for(final var p : permissions)
            if(ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) tmp.add(p);

        String[] filtered = new String[tmp.size()];
        for(int i = 0; i < tmp.size(); i++)
            filtered[i] = tmp.get(0);

        permissionLauncher.launch(filtered);
    }
}
