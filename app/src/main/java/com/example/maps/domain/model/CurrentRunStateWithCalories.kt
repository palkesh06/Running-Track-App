package com.example.maps.domain.model

import com.example.maps.domain.tracking.model.CurrentRunState


data class CurrentRunStateWithCalories(
    val currentRunState: CurrentRunState = CurrentRunState(),
    val caloriesBurnt: Int = 0
)
