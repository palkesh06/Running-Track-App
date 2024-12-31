package com.example.maps.data.tracking.timer

import com.example.maps.di.ApplicationScope
import com.example.maps.di.DefaultDispatcher
import com.example.maps.domain.tracking.timer.TimeTracker
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class DefaultTimeTracker @Inject constructor(
    @ApplicationScope private val applicationScope: CoroutineScope,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : TimeTracker {

    private var callback: ((timeInMillis: Long) -> Unit)? = null
    private var job: Job? = null
    private var timeElapsedInMillis = 0L
    private var isRunning = false

    private fun start() {
        if (job != null)
            return
        System.currentTimeMillis()
        this.job = applicationScope.launch(defaultDispatcher) {
            while (isRunning && isActive) {
                callback?.invoke(timeElapsedInMillis)
                delay(1000)
                timeElapsedInMillis += 1000
            }
        }
    }

    override fun startResumeTimer(callback: (Long) -> Unit) {
        if (isRunning)
            return
        this.callback = callback
        isRunning = true
        start()
    }

    override fun stopTimer() {
        pauseTimer()
        timeElapsedInMillis = 0
    }

    override fun pauseTimer() {
        isRunning = false
        job?.cancel()
        job = null
        callback = null
    }
}