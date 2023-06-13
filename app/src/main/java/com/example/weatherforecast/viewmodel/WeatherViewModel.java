package com.example.weatherforecast.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.weatherforecast.model.WeatherModel;
import com.example.weatherforecast.repository.WeatherRepository;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.http.QueryMap;

public class WeatherViewModel extends AndroidViewModel {

    WeatherRepository weatherRepository;

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        weatherRepository = new WeatherRepository(application);
    }

    public LiveData<WeatherModel> getWeatherData(@QueryMap Map<String, String> params) {
        //RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), strRequest);
        LiveData<WeatherModel> weatherModelLiveData = weatherRepository.getWeatherData(params);
        return weatherModelLiveData;
    }
}
