package com.example.maps.ui.screens.currentRun.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.maps.R
import com.example.maps.common.utils.DateTimeUtils
import com.example.maps.domain.model.CurrentRunStateWithCalories
import com.example.maps.ui.common.compose.component.RunningStatsItem

@Composable
fun CurrentRunStatsCard(
    modifier: Modifier = Modifier,
    durationInMillis: Long = 0L,
    runState: CurrentRunStateWithCalories,
    onPlayPauseButtonClick: () -> Unit = {},
    onFinish: () -> Unit
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 24.dp, bottom = 16.dp)
                .padding(horizontal = 20.dp)
        ) {
           RunningCardTime (
               modifier = Modifier
                   .weight(1f),
               durationInMillis = durationInMillis
           )
           TrackingControlButton(
               isRunning = runState.currentRunState.isTracking,
               durationInMillis = durationInMillis,
               onFinish = onFinish,
               onPlayPauseButtonClick = onPlayPauseButtonClick
           )
        }
        RunningStats(runState)
    }

}

@Composable
fun TrackingControlButton(
    modifier: Modifier = Modifier,
    isRunning: Boolean,
    durationInMillis: Long,
    onFinish: () -> Unit,
    onPlayPauseButtonClick: () -> Unit
) {

    Row(modifier = modifier) {
        if (!isRunning && durationInMillis > 0) {
            IconButton(
                onClick = onFinish,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.error,
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        id = R.drawable.ic_finish
                    ),
                    contentDescription = "",
                    modifier = Modifier
                        .size(16.dp),
                    tint = MaterialTheme.colorScheme.onError
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
        }
        IconButton(
            onClick = onPlayPauseButtonClick,
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(
                    id = if (isRunning) R.drawable.ic_pause else R.drawable.ic_play
                ),
                contentDescription = "",
                modifier = Modifier
                    .size(16.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

    }
}

@Composable
private fun RunningStats(
    runState: CurrentRunStateWithCalories
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp)
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)

    ) {
        RunningStatsItem(
            modifier = Modifier,
            painter = painterResource(id = R.drawable.running_boy),
            unit = "km",
            value = (runState.currentRunState.distanceInMeters / 1000f).toString()
        )
        VerticalDivider(
            thickness = 1.dp,
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
        RunningStatsItem(
            modifier = Modifier,
            painter = painterResource(id = R.drawable.fire),
            unit = "kcal",
            value = runState.caloriesBurnt.toString()
        )

        VerticalDivider(
            thickness = 1.dp,
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
        RunningStatsItem(
            modifier = Modifier,
            painter = painterResource(id = R.drawable.bolt),
            unit = "km/hr",
            value = runState.currentRunState.speedInKMH.toString()
        )
    }
}

@Composable
fun RunningCardTime(
    modifier: Modifier = Modifier,
    durationInMillis: Long
) {
    Column(modifier = modifier) {
        Text(
            text = "Running Time",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = DateTimeUtils.getFormattedStopwatchTime(durationInMillis),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}