package com.example.weatherforecast.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.example.weatherforecast.model.WeatherModel
import com.example.weatherforecast.viewmodel.WeatherViewModel
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    var context: Context? = null
    private lateinit var binding:ActivityMainBinding
    var progressBarDialog: ProgressBarDialog? = null
    var weatherViewModel: WeatherViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        init()




        // Load the last searched city from local storage
//        lastSearchedCity = loadLastSearchedCity()
//
//        // Auto-load the last searched city upon app launch
//        if (lastSearchedCity != null && !lastSearchedCity.isEmpty()) {
//            cityEditText.setText(lastSearchedCity)
//            fetchWeatherData(lastSearchedCity)
//        }
//
//        // Set up search button click listener
//
    }

    fun init(){
        context = this

        setListener()
    }

    private fun setListener() {
        binding.searchButton.setOnClickListener {
            getWeatherData()
        }
    }


    fun getWeatherData(){
        val url = "https://api.openweathermap.org/data/2.5/weather?q=" + binding.cityEditText.text.toString() + "&appid=" + getString(R.string.weather_api_key)
        val queue: RequestQueue = Volley.newRequestQueue(this@MainActivity)
        val jsonObjectRequest =
            JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    try {
                        val jsonObject = response.getJSONObject("main")
                        val tempMin = jsonObject.getString("temp_min")
                        val tempMax = jsonObject.getString("temp_max")
                        val city = response.getString("name")

                        binding.cityTextView.text = "City = $city"
                        binding.temperatureTextViewMin.text = "Min Temp = $tempMin"
                        binding.temperatureTextViewMax.text = "Max Temp = $tempMax"


                    } catch (e: JSONException) {
                        // if we do not extract data from json object properly.
                        // below line of code is use to handle json exception
                        e.printStackTrace()
                    }
                }, object : Response.ErrorListener {
                // this is the error listener method which
                // we will call if we get any error from API.
                override fun onErrorResponse(error: VolleyError?) {
                    // below line is use to display a toast message along with our error.
                    Toast.makeText(this@MainActivity, "Fail to get data..", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        queue.add(jsonObjectRequest)
    }






















    fun getWeatherDataAPI() {
        progressBarDialog?.show()
//        val param = JSONObject()
//        try {
//            param.put("q", binding.cityEditText.getText().toString())
//            param.put("api_key", getString(R.string.weather_api_key))
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }


        val params: MutableMap<String, String> = HashMap()
        params["q"] = "binding.cityEditText.getText().toString()"
        params["api_key"] = getString(R.string.weather_api_key)


        weatherViewModel?.getWeatherData(params)
            ?.observe((context as LifecycleOwner?)!!) { weatherModel ->
                progressBarDialog?.dismiss()
                if (weatherModel != null) {
                    val weatherModel: WeatherModel = weatherModel
                    if (weatherModel != null) {
                        //binding.temperatureTextView.text = weatherModel.main?.temp.toString()
                    }
                } else {
                    progressBarDialog?.dismiss()
                }
            }
    }


//    private fun fetchWeatherData(city: String) {
//
//        // Process the API response and extract relevant weather information
//
//        // Display the temperature and weather icon
//        temperatureTextView.setText()
//        weatherIconImageView.setImageResource()
//
//        // Save the searched city to local storage
//        //saveLastSearchedCity(city)
//    }

//    private fun loadLastSearchedCity(): String? {
//        // Load the last searched city from local storage
//
//        // Return the last searched city
//        return  /* Last searched city */
//    }
//
//    private fun saveLastSearchedCity(city: String) {
//        // Save the last searched city to local storage
//    }
}