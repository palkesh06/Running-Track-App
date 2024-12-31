package com.example.maps.common.utils

object RunUtils {

    fun calculateCaloriesBurnt(distanceInMeters: Int, weightInKg: Float) =
        //from chat gpt
        (0.75f * weightInKg) * (distanceInMeters / 1000f)

}