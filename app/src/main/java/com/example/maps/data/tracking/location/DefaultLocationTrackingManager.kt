package com.example.maps.data.tracking.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.example.maps.common.extensions.hasLocationPermission
import com.example.maps.domain.tracking.location.LocationTrackingManager
import com.example.maps.domain.tracking.model.LocationInfo
import com.example.maps.domain.tracking.model.LocationTrackingInfo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

@SuppressLint("MissingPermission")
class DefaultLocationTrackingManager(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val context: Context,
    private val locationRequest: LocationRequest
) : LocationTrackingManager {

    private var locationCallback: LocationTrackingManager.LocationCallback? = null
    private val gLocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            locationCallback?.onLocationChanged(
                p0.locations.mapNotNull {
                    it?.let {
                        LocationTrackingInfo(
                            locationInfo = LocationInfo(
                                latitude = it.latitude,
                                longitude = it.longitude
                            ),
                            speedInMS = it.speed
                        )
                    }
                }
            )
        }
    }


    override fun setCallback(locationCallback: LocationTrackingManager.LocationCallback) {
        if (context.hasLocationPermission()) {
            this.locationCallback = locationCallback
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                gLocationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun removeCallback() {
        this.locationCallback = null
        fusedLocationProviderClient.removeLocationUpdates(gLocationCallback)
    }
}