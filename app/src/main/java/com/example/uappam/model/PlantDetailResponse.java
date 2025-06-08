package com.example.uappam.model;

import com.example.uappam.model.Plant;
import com.google.gson.annotations.SerializedName;

public class PlantDetailResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Plant plant;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }
}