package com.example.weatherforecast.repository;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weatherforecast.R;
import com.example.weatherforecast.model.WeatherModel;
import com.example.weatherforecast.network.ApiRequest;
import com.example.weatherforecast.network.RetrofitRequest;
import com.example.weatherforecast.util.ConnectionManager;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.QueryMap;

public class WeatherRepository {
    
    private static final String TAG = WeatherRepository.class.getSimpleName();
    private ApiRequest apiRequest;
    Context context;
    Call<WeatherModel> modelCall;
    private boolean isCanceled = false;

    public WeatherRepository(Context context) {
        this.context = context;
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
    }

    public LiveData<WeatherModel> getWeatherData(@QueryMap Map<String, String> params)
    {
        isCanceled = false;
        final MutableLiveData<WeatherModel> data = new MutableLiveData<>();
        if (ConnectionManager.Connection(context))
        {
            modelCall = apiRequest.getWeatherData(params);
            modelCall.enqueue(new Callback<WeatherModel>()
            {
                @Override
                public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {

                    WeatherModel weatherModel = response.body();
                    if (weatherModel != null)
                    {
                        Toast.makeText(context, "model = "+ weatherModel, Toast.LENGTH_SHORT).show();
                        data.setValue(weatherModel);
                    }
                    else
                    {
                        Toast.makeText(context, "null", Toast.LENGTH_SHORT).show();
                        data.setValue(null);
                    }
                }
                @Override
                public void onFailure(Call<WeatherModel> call, Throwable t) {
                    Toast.makeText(context, "null", Toast.LENGTH_SHORT).show();
                    data.setValue(null);
                }
            });
        }
        else
        {
            Toast.makeText(context, context.getResources().getString(R.string.error_check_internet), Toast.LENGTH_SHORT).show();
            data.setValue(null);
        }
        return data;
    }
}
