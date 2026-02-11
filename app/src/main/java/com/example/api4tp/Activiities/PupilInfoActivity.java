package com.example.api4tp.Activiities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.api4tp.R;

public class PupilInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pupil_info);
        ConstraintLayout backMapCl = findViewById(R.id.pi_backMapCl);

        backMapCl.setOnClickListener(this::handleBackMain);
    }

    private void handleBackMain(View v) {
        finish();
    }
}
