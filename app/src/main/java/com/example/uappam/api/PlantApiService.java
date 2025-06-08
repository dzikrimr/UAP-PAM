package com.example.uappam.api;

import com.example.uappam.model.Plant;
import com.example.uappam.model.PlantDetailResponse;
import com.example.uappam.model.PlantResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PlantApiService {
    @GET("plant/all")
    Call<PlantResponse> getAllPlants();

    @POST("plant/new")
    Call<Plant> addPlant(@Body Plant plant);

    @GET("plant/{plantName}")
    Call<PlantDetailResponse> getPlantByName(@Path("plantName") String plantName);

    @PUT("plant/{plantName}")
    Call<Plant> updatePlant(@Path("plantName") String plantName, @Body Plant plant);

    @DELETE("plant/{plantName}")
    Call<Void> deletePlant(@Path("plantName") String plantName);
}