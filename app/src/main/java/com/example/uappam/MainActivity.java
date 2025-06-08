package com.example.uappam;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uappam.adapter.PlantAdapter;
import com.example.uappam.api.PlantApiService;
import com.example.uappam.model.Plant;
import com.example.uappam.model.PlantResponse;
import com.example.uappam.retrofit.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private RecyclerView rvPlants;
    private PlantAdapter plantAdapter;
    private List<Plant> plantList;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, true);
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(window, window.getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(true);
        windowInsetsController.setAppearanceLightNavigationBars(true);
        window.setStatusBarColor(getResources().getColor(R.color.green));

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        rvPlants = findViewById(R.id.rv_plants);
        MaterialButton btnAdd = findViewById(R.id.btn_register);
        ExtendedFloatingActionButton fabLogout = findViewById(R.id.fab_logout);

        plantList = new ArrayList<>();
        plantAdapter = new PlantAdapter(this, plantList);
        rvPlants.setLayoutManager(new LinearLayoutManager(this));
        rvPlants.setAdapter(plantAdapter);

        // Cek autentikasi
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, OnboardActivity.class));
            finish();
            return;
        }

        // Load plants
        fetchPlants();

        // Tambah plant baru
        btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddPlantActivity.class));
        });

        // Logout dan confirm dialog
        fabLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Logout")
                .setMessage("Anda yakin ingin log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    mAuth.signOut();
                    Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, OnboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        });

        dialog.show();
    }

    private void fetchPlants() {
        PlantApiService apiService = RetrofitClient.getPlantApiService();
        apiService.getAllPlants().enqueue(new Callback<PlantResponse>() {
            @Override
            public void onResponse(Call<PlantResponse> call, Response<PlantResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getPlants() != null) {
                    plantList.clear();
                    plantList.addAll(response.body().getPlants());
                    plantAdapter.notifyDataSetChanged();
                } else {
                    try {
                        Log.e(TAG, "Response error: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading response: " + e.getMessage());
                    }
                    Toast.makeText(MainActivity.this, "Failed to fetch plants", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlantResponse> call, Throwable t) {
                Log.e(TAG, "Fetch error: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchPlants();
    }
}