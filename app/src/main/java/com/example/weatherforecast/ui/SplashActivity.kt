package com.example.weatherforecast.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.WindowManager.BadTokenException
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.weatherforecast.util.Constants
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ActivitySplashBinding
import com.example.weatherforecast.model.LocationModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    var context: Context? = null

    //LOCATION INITIALIZATIONS
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5000
    private val REQUEST_CHECK_SETTINGS = 100

    //Bunch of location related declarations
    var mFusedLocationClient: FusedLocationProviderClient? = null
    var mSettingsClient: SettingsClient? = null
    var mLocationRequest: LocationRequest? = null
    var mLocationSettingsRequest: LocationSettingsRequest? = null
    var mLocationCallback: LocationCallback? = null
    var mCurrentLocation: Location? = null
    var mRequestingLocationUpdates = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        init()
    }

    fun init(){
        context = this

        if (checkPermission()) {
            Constants.LOCATION_PERMISSION_ALLOWED = true
            locationInitializations()
        }
    }

    fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermission()
            } else {
                return true
            }
        } else {
            return true
        }
        return false
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            (context as Activity?)!!,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION), 1)
    }


    fun locationInitializations() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        mSettingsClient = LocationServices.getSettingsClient(context!!)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                mCurrentLocation = locationResult.lastLocation
                updateLocationUI()
            }
        }
        mRequestingLocationUpdates = false
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()
        startLocationButtonClick()
    }

    fun startLocationButtonClick() {
        Dexter.withContext(context)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                    showPermissionGranted(permissionGrantedResponse.permissionName)
                }

                override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    permissionRequest: PermissionRequest,
                    permissionToken: PermissionToken
                ) {
                    permissionToken.continuePermissionRequest()
                }
            })
            .withErrorListener { dexterError ->
                Log.e(
                    "Dexter",
                    "There was an error = $dexterError"
                )
            }
            .check()
    }

    private fun showPermissionGranted(permissionName: String) {
        mRequestingLocationUpdates = true
        if (Constants.LOCATION_PERMISSION_ALLOWED) {
            startLocationUpdates()
        }
    }

    /**
     * ON GRANTED
     */
    private fun startLocationUpdates() {
        if (mSettingsClient != null && mLocationSettingsRequest != null) {
            mSettingsClient!!
                .checkLocationSettings(mLocationSettingsRequest!!)
                .addOnSuccessListener(this) {
                    Log.i("TAG", "All location settings are satisfied.")
                    mFusedLocationClient!!.requestLocationUpdates(
                        mLocationRequest!!,
                        mLocationCallback!!, Looper.myLooper()
                    )
                    updateLocationUI()
                }
                .addOnFailureListener(this) { e ->
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            val rae = e as ResolvableApiException
                            try {
                                rae.startResolutionForResult(
                                    this@SplashActivity,
                                    REQUEST_CHECK_SETTINGS
                                )
                            } catch (sie: SendIntentException) {
                                Log.e("Dexter", "PendingIntent unable to execute request.")
                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage =
                                "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."
                            Log.e("Dexter", errorMessage)
                        }
                    }
                    updateLocationUI()
                }
        }
    }

    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */
    private fun updateLocationUI() {
        if (mCurrentLocation != null) {
            Constants.LATITUDE = mCurrentLocation!!.latitude.toString()
            Constants.LONGITUDE = mCurrentLocation!!.longitude.toString()
            val locationModel = LocationModel()
            locationModel.setLatitude(Constants.LATITUDE)
            locationModel.setLongitude(Constants.LONGITUDE)
            if (mRequestingLocationUpdates && !isFinishing) {
                stopLocationUpdates()
            }
        }
    }

    fun stopLocationUpdates() {
        mRequestingLocationUpdates = false
        mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)
    }

    /**
     * CALLBACK METHODS
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Handle the result of the permission request
        when (requestCode) {
            // Add your permission request code here
            1 -> {
                // Check if the permission is granted
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with accessing location
                    // Your code for accessing location or fetching weather data based on location
                    Constants.LOCATION_PERMISSION_ALLOWED = true
                    locationInitializations()
                    startActivity(Intent(context, MainActivity::class.java))
                    finish()
                } else {
                    // Permission denied
                    // Handle the case when the user denies the location permission
                    Constants.LOCATION_PERMISSION_ALLOWED = false
                    startActivity(Intent(context, MainActivity::class.java))
                    finish()
                }
            }
            // Add more cases for other permission request codes if needed
        }
    }
}