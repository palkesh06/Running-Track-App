package com.example.maps.common.utils

import android.location.Location
import com.example.maps.domain.tracking.model.PathPoint
import kotlin.math.roundToInt

object LocationUtils {

    fun getDistanceBetweenPathPoints(
        pathPoint1: PathPoint,
        pathPoint2: PathPoint
    ): Int {
        return if (pathPoint1 is PathPoint.LocationPoint && pathPoint2 is PathPoint.LocationPoint) {
            val result = FloatArray(1)
            Location.distanceBetween(
                pathPoint1.locationInfo.latitude,
                pathPoint1.locationInfo.longitude,
                pathPoint2.locationInfo.latitude,
                pathPoint2.locationInfo.longitude,
                result
            )
            result[0].roundToInt()
        } else 0
    }
}