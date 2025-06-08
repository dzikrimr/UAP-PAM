package com.example.uappam;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.uappam.api.PlantApiService;
import com.example.uappam.model.Plant;
import com.example.uappam.model.PlantDetailResponse;
import com.example.uappam.retrofit.RetrofitClient;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private TextView tvPlantName, tvPlantPrice, tvPlantDescription;
    private ImageView ivPlantDetail;
    private MaterialButton btnUpdate;
    private static final String TAG = "DetailActivity";
    private String plantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, true);
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(window, window.getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(true);
        windowInsetsController.setAppearanceLightNavigationBars(true);
        window.setStatusBarColor(getResources().getColor(R.color.green));

        setContentView(R.layout.activity_detail);

        tvPlantName = findViewById(R.id.tv_plant_name_detail);
        tvPlantPrice = findViewById(R.id.tv_plant_price_detail);
        tvPlantDescription = findViewById(R.id.tv_plant_description);
        ivPlantDetail = findViewById(R.id.iv_plant_detail);
        btnUpdate = findViewById(R.id.btn_update);

        plantName = getIntent().getStringExtra("PLANT_NAME");
        if (plantName != null) {
            fetchPlantDetails(plantName);
        } else {
            Toast.makeText(this, "Invalid plant name", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, UpdatePlantActivity.class);
            intent.putExtra("PLANT_NAME", plantName);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (plantName != null) {
            fetchPlantDetails(plantName);
        }
    }

    private void fetchPlantDetails(String plantName) {
        PlantApiService apiService = RetrofitClient.getPlantApiService();
        apiService.getPlantByName(plantName).enqueue(new Callback<PlantDetailResponse>() {
            @Override
            public void onResponse(Call<PlantDetailResponse> call, Response<PlantDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getPlant() != null) {
                    Plant plant = response.body().getPlant();
                    tvPlantName.setText(plant.getPlantName() != null ? plant.getPlantName() : "N/A");
                    tvPlantPrice.setText(plant.getPrice() != null ? "Rp " + plant.getPrice() : "Rp N/A");
                    tvPlantDescription.setText(plant.getDescription() != null ? plant.getDescription() : "No description available");
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Failed to fetch plant details: " + response.code() + ", " + errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading response: " + e.getMessage());
                    }
                    Toast.makeText(DetailActivity.this, "Failed to fetch plant details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlantDetailResponse> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(DetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}