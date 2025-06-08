package com.example.uappam.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uappam.DetailActivity;
import com.example.uappam.R;
import com.example.uappam.api.PlantApiService;
import com.example.uappam.model.Plant;
import com.example.uappam.retrofit.RetrofitClient;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {
    private List<Plant> plantList;
    private Context context;

    public PlantAdapter(Context context, List<Plant> plantList) {
        this.context = context;
        this.plantList = plantList;
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant plant = plantList.get(position);
        holder.tvPlantName.setText(plant.getPlantName());
        holder.tvPlantPrice.setText("Rp " + plant.getPrice());

        // Handle Detail button
        holder.btnDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("PLANT_NAME", plant.getPlantName());
            context.startActivity(intent);
        });

        // Handle Delete button
        holder.btnDelete.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) {
                return;
            }
            String plantName = plantList.get(currentPosition).getPlantName();
            PlantApiService apiService = RetrofitClient.getPlantApiService();
            apiService.deletePlant(plantName).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        int updatedPosition = holder.getAdapterPosition();
                        if (updatedPosition != RecyclerView.NO_POSITION) {
                            plantList.remove(updatedPosition);
                            notifyItemRemoved(updatedPosition);
                            notifyItemRangeChanged(updatedPosition, plantList.size());
                            Toast.makeText(context, "Plant deleted", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Failed to delete plant", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    public static class PlantViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlant;
        TextView tvPlantName, tvPlantPrice;
        MaterialButton btnDetail, btnDelete;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlant = itemView.findViewById(R.id.iv_plant);
            tvPlantName = itemView.findViewById(R.id.tv_plant_name);
            tvPlantPrice = itemView.findViewById(R.id.tv_plant_price);
            btnDetail = itemView.findViewById(R.id.btn_detail);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}