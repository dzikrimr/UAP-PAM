package com.example.uappam;

import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.uappam.api.PlantApiService;
import com.example.uappam.model.Plant;
import com.example.uappam.retrofit.RetrofitClient;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPlantActivity extends AppCompatActivity {
    private EditText etPlantName, etPlantPrice, etPlantDescription;
    private MaterialButton btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, true);
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(window, window.getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(true);
        windowInsetsController.setAppearanceLightNavigationBars(true);
        window.setStatusBarColor(getResources().getColor(R.color.green));

        setContentView(R.layout.activity_add_plant);

        etPlantName = findViewById(R.id.et_plant_name);
        etPlantPrice = findViewById(R.id.et_plant_price);
        etPlantDescription = findViewById(R.id.et_plant_description);
        btnAdd = findViewById(R.id.btn_add);

        btnAdd.setOnClickListener(v -> {
            String plantName = etPlantName.getText().toString().trim();
            String price = etPlantPrice.getText().toString().trim();
            String description = etPlantDescription.getText().toString().trim();

            if (plantName.isEmpty() || price.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Plant plant = new Plant();
            plant.setPlantName(plantName);
            plant.setPrice(price);
            plant.setDescription(description);

            PlantApiService apiService = RetrofitClient.getPlantApiService();
            apiService.addPlant(plant).enqueue(new Callback<Plant>() {
                @Override
                public void onResponse(Call<Plant> call, Response<Plant> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddPlantActivity.this, "Plant berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddPlantActivity.this, "Gagal menambahkan plant", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Plant> call, Throwable t) {
                    Toast.makeText(AddPlantActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}