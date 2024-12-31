package com.example.maps.domain

import android.util.Log
import com.example.maps.common.utils.LocationUtils
import com.example.maps.domain.tracking.background.BackgroundTrackingManager
import com.example.maps.domain.tracking.location.LocationTrackingManager
import com.example.maps.domain.tracking.model.CurrentRunState
import com.example.maps.domain.tracking.model.LocationTrackingInfo
import com.example.maps.domain.tracking.model.PathPoint
import com.example.maps.domain.tracking.timer.TimeTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingManager @Inject constructor(
    private val locationTrackingManager: LocationTrackingManager,
    private val timeTracker: TimeTracker,
    private val backgroundTrackingManager: BackgroundTrackingManager
) {
    private var isTracking = false
        set(value) {
            _currentRunState.update {it.copy(isTracking = value)}
            field = value
        }

    private val _currentRunState = MutableStateFlow(CurrentRunState())
    val currentRunState: StateFlow<CurrentRunState> = _currentRunState

    private val _trackingDurationInMs = MutableStateFlow(0L)
    val trackingDurationInMs: StateFlow<Long> = _trackingDurationInMs

    private val timeTrackerCallback = { timeElapsed: Long ->
        _trackingDurationInMs.update { timeElapsed }
    }

    private var isFirst = true


    private val locationCallback = object : LocationTrackingManager.LocationCallback {
        override fun onLocationChanged(result: List<LocationTrackingInfo>) {
            result.forEach { info ->
                addPathPoints(info)
            }
        }
    }

    private fun postInitialValue() {
        _currentRunState.update {
            CurrentRunState()
        }
        _trackingDurationInMs.update { 0 }
    }


    private fun addPathPoints(info: LocationTrackingInfo) {
        _currentRunState.update { state ->
            val pathPoints = state.pathPoints + PathPoint.LocationPoint(info.locationInfo)
            state.copy(
                pathPoints = pathPoints,
                distanceInMeters = state.distanceInMeters.run {
                    var distance = this
                    if (pathPoints.size > 1)
                        distance += LocationUtils.getDistanceBetweenPathPoints(
                            pathPoint1 = pathPoints[pathPoints.size - 1],
                            pathPoint2 = pathPoints[pathPoints.size - 2]
                        )
                    distance
                },
                speedInKMH = (info.speedInMS * 3.6f).toBigDecimal()
                    .setScale(2, RoundingMode.HALF_UP).toFloat()
            )
        }
    }

    fun startResumeTracking() {
        if (isTracking)
            return
        if (isFirst) {
            postInitialValue()
            backgroundTrackingManager.startBackgroundTracking()
            isFirst = false
        }
        isTracking = true
        timeTracker.startResumeTimer(timeTrackerCallback)
        locationTrackingManager.setCallback(locationCallback)
    }

    private fun addEmptyPolyLine() {
        _currentRunState.update {
            it.copy(
                pathPoints = it.pathPoints + PathPoint.EmptyLocationPoint
            )
        }
    }

    fun pauseTracking() {
        isTracking = false
        locationTrackingManager.removeCallback()
        timeTracker.pauseTimer()
        addEmptyPolyLine()
    }

    fun stop() {
        pauseTracking()
        backgroundTrackingManager.stopBackgroundTracking()
        timeTracker.stopTimer()
        postInitialValue()
        isFirst = true
    }
}

