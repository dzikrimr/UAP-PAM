package com.example.uappam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class OnboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, true);
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(window, window.getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(true);
        windowInsetsController.setAppearanceLightNavigationBars(true);
        window.setStatusBarColor(getResources().getColor(R.color.green));

        setContentView(R.layout.activity_onboard);

        findViewById(R.id.btn_login).setOnClickListener(v -> {
            startActivity(new Intent(OnboardActivity.this, LoginActivity.class));
        });

        findViewById(R.id.tv_register).setOnClickListener(v -> {
            startActivity(new Intent(OnboardActivity.this, RegisterActivity.class));
        });
    }
}