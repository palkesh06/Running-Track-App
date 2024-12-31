package com.example.maps.ui.screens.runstats.utils

import com.example.maps.data.model.Run
import com.example.maps.ui.screens.runstats.RunStatsUiState.AccumulatedRunStatisticsOnDate
import java.util.Date

object RunStatsAccumulator {

    fun accumulateRunByDate(
        list: List<Run>
    ): Map<Date, AccumulatedRunStatisticsOnDate> {
        return buildMap {
            list.forEach { run ->
                val newStats = AccumulatedRunStatisticsOnDate.fromRun(run)
                this[newStats.date] = newStats + this[newStats.date]
            }
        }
    }

}