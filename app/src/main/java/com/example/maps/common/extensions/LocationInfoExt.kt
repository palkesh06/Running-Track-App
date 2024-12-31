package com.example.maps.common.extensions

import com.example.maps.domain.tracking.model.LocationInfo
import com.google.android.gms.maps.model.LatLng

fun LocationInfo.toLatLng() = LatLng(
    latitude,
    longitude
)