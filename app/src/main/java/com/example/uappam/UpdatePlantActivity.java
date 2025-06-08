package com.example.uappam;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
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

public class UpdatePlantActivity extends AppCompatActivity {
    private EditText etPlantName, etPlantPrice, etPlantDescription;
    private MaterialButton btnSave;
    private static final String TAG = "UpdatePlantActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, true);
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(window, window.getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(true);
        windowInsetsController.setAppearanceLightNavigationBars(true);
        window.setStatusBarColor(getResources().getColor(R.color.green));

        setContentView(R.layout.activity_update_plant);

        etPlantName = findViewById(R.id.et_plant_name_update);
        etPlantPrice = findViewById(R.id.et_plant_price_update);
        etPlantDescription = findViewById(R.id.et_plant_description_update);
        btnSave = findViewById(R.id.btn_save);

        String plantName = getIntent().getStringExtra("PLANT_NAME");
        if (plantName != null) {
            Log.d(TAG, "Fetching details for plant: " + plantName);
            fetchPlantDetails(plantName);
        } else {
            Toast.makeText(this, "Invalid plant name", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnSave.setOnClickListener(v -> {
            String newPlantName = etPlantName.getText().toString().trim();
            String price = etPlantPrice.getText().toString().trim();
            String description = etPlantDescription.getText().toString().trim();

            if (newPlantName.isEmpty() || price.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Plant plant = new Plant();
            plant.setPlantName(newPlantName);
            plant.setPrice(price);
            plant.setDescription(description);

            PlantApiService apiService = RetrofitClient.getPlantApiService();
            apiService.updatePlant(plantName, plant).enqueue(new Callback<Plant>() {
                @Override
                public void onResponse(Call<Plant> call, Response<Plant> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(UpdatePlantActivity.this, "Plant berhasil diupdate", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            Log.e(TAG, "Failed to update plant: " + response.code() + ", " + errorBody);
                        } catch (IOException e) {
                            Log.e(TAG, "Error reading response: " + e.getMessage());
                        }
                        Toast.makeText(UpdatePlantActivity.this, "Failed to update plant", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Plant> call, Throwable t) {
                    Log.e(TAG, "Network error: " + t.getMessage(), t);
                    Toast.makeText(UpdatePlantActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void fetchPlantDetails(String plantName) {
        PlantApiService apiService = RetrofitClient.getPlantApiService();
        apiService.getPlantByName(plantName).enqueue(new Callback<PlantDetailResponse>() {
            @Override
            public void onResponse(Call<PlantDetailResponse> call, Response<PlantDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getPlant() != null) {
                    Plant plant = response.body().getPlant();
                    etPlantName.setText(plant.getPlantName() != null ? plant.getPlantName() : "");
                    etPlantPrice.setText(plant.getPrice() != null ? plant.getPrice() : "");
                    etPlantDescription.setText(plant.getDescription() != null ? plant.getDescription() : "");
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Failed to fetch plant details: " + response.code() + ", " + errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading response: " + e.getMessage());
                    }
                    Toast.makeText(UpdatePlantActivity.this, "Failed to fetch plant details", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PlantDetailResponse> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(UpdatePlantActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}