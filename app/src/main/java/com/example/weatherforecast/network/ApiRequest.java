package com.example.weatherforecast.network;

import com.example.weatherforecast.model.WeatherModel;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ApiRequest {

    @GET("data/2.5/weather")
    Call<WeatherModel> getWeatherData(@QueryMap Map<String, String> params);
}
