package com.example.maps.common.extensions

import com.example.maps.domain.tracking.model.LocationInfo
import com.google.android.gms.maps.model.LatLng

fun LatLng.toLocationInfo() = LocationInfo(
    longitude = longitude,
    latitude = latitude
)