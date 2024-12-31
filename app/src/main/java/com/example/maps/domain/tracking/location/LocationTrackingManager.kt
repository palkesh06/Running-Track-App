package com.example.maps.domain.tracking.location

import com.example.maps.domain.tracking.model.LocationTrackingInfo

interface LocationTrackingManager {
    fun setCallback(locationCallback : LocationCallback)

    fun removeCallback()

    interface LocationCallback {
        fun onLocationChanged(result: List<LocationTrackingInfo>)
    }
}