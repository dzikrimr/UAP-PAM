package com.example.uappam.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PlantResponse {
    @SerializedName("data")
    private List<Plant> plants;

    @SerializedName("status")
    private String status;

    public List<Plant> getPlants() {
        return plants;
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}