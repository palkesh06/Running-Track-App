package com.example.maps.ui.screens.currentRun.component

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.maps.R
import com.example.maps.common.extensions.toLatLng
import com.example.maps.domain.tracking.model.LocationInfo
import com.example.maps.domain.tracking.model.PathPoint
import com.example.maps.domain.tracking.model.firstLocationPoint
import com.example.maps.domain.tracking.model.lastLocationPoint
import com.example.maps.ui.common.utils.GoogleMapUtils
import com.example.maps.ui.theme.RTColor
import com.example.maps.ui.theme.md_theme_light_primary
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


@Composable
fun CurrentRunMap(
    modifier: Modifier = Modifier,
    pathPoints: List<PathPoint>,
    isRunningFinished: Boolean,
    onSnapshot: (Bitmap) -> Unit,
) {

    var mapSize by remember { mutableStateOf(Size(0f, 0f)) }
    var mapCenter by remember { mutableStateOf(Offset(0f, 0f)) }
    var isMapLoaded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned {
                val rect = it.boundsInRoot()
                mapSize = rect.size
                mapCenter = rect.center
            }
    ) {
        ShowMapLoadingProgressBar(!isMapLoaded)
        Map(
            pathPoints = pathPoints,
            isRunningFinished = isRunningFinished,
            mapCenter = mapCenter,
            mapSize = mapSize,
            onMapLoaded = { isMapLoaded = true },
            onSnapshot = onSnapshot
        )
    }
}

@Composable
fun Map(
    pathPoints: List<PathPoint>,
    isRunningFinished: Boolean,
    mapCenter: Offset,
    mapSize: Size,
    onMapLoaded: () -> Unit,
    onSnapshot: (Bitmap) -> Unit,
) {
    val mapUiSettings = remember {
        MapUiSettings(
            mapToolbarEnabled = false,
            compassEnabled = true,
            zoomControlsEnabled = false
        )
    }
    val cameraPositionState = rememberCameraPositionState {}
    val lastLocationPoint by remember(pathPoints) {
        derivedStateOf { pathPoints.lastLocationPoint() }
    }
    LaunchedEffect(key1 = lastLocationPoint) {
        lastLocationPoint?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(it.locationInfo.toLatLng(), 18f)
                )
            )
        }
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxSize(),
        uiSettings = mapUiSettings,
        cameraPositionState = cameraPositionState,
        onMapLoaded = onMapLoaded,
    ){
        DrawPathPoints(pathPoints = pathPoints, isRunningFinished = isRunningFinished)

        TakeScreenShot(
            take = isRunningFinished,
            mapSize = mapSize,
            mapCenter = mapCenter,
            pathPoints = pathPoints,
            onSnapshot = onSnapshot
        )
    }
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun TakeScreenShot(
    take: Boolean,
    mapSize: Size,
    mapCenter: Offset,
    pathPoints: List<PathPoint>,
    onSnapshot: (Bitmap) -> Unit
) {
    MapEffect(key1 = take) { map ->
        if (take)
            GoogleMapUtils.takeSnapshot(
                map,
                pathPoints,
                mapCenter,
                onSnapshot,
                snapshotSideLength = mapSize.width / 2f
            )
    }
}


@Composable
fun DrawPathPoints(pathPoints: List<PathPoint>, isRunningFinished: Boolean) {

    val context = LocalContext.current
    val lastMarkerState = rememberMarkerState()
    val largeLastMarkerState = rememberMarkerState()
    val lastLocationPoint by remember(pathPoints) {
        derivedStateOf { pathPoints.lastLocationPoint() }
    }
    val firstLocationPoint by remember(pathPoints) {
        derivedStateOf { pathPoints.firstLocationPoint() }
    }
    val density = LocalDensity.current
    val largeLocationIconSize = remember { with(density) { 32.dp.toPx().toInt() } }
    val smallLocationIconSize = remember { with(density) { 16.dp.toPx().toInt() } }
    val flagSize = remember { with(density) { 32.dp.toPx().toInt() } }
    val flagOffset = remember { Offset(0.5f, 0.8f) }

    LaunchedEffect(key1 = lastLocationPoint) {
        pathPoints.lastLocationPoint()?.let {
            val latLng = it.locationInfo.toLatLng()
            lastMarkerState.position = latLng
            largeLastMarkerState.position = latLng
        }
    }


    val locationInfoList = mutableListOf<LocationInfo>()
    pathPoints.forEach {pathPoint ->
        if(pathPoint is PathPoint.EmptyLocationPoint){
            Polyline(
                points = locationInfoList.map { it.toLatLng() },
                color = MaterialTheme.colorScheme.primary
            )
            locationInfoList.clear()
        } else if (pathPoint is PathPoint.LocationPoint) {
            locationInfoList += pathPoint.locationInfo
        }
    }

    //add the last path points
    if (locationInfoList.isNotEmpty())
        Polyline(
            points = locationInfoList.map { it.toLatLng() },
            color = md_theme_light_primary
        )


    val currentPosIcon = remember(isRunningFinished) {
        if (isRunningFinished.not()) {
            GoogleMapUtils.bitmapDescriptorFromVector(
                context = context,
                vectorResId = R.drawable.ic_circle,
                tint = md_theme_light_primary.toArgb(),
                sizeInPx = smallLocationIconSize
            )
        } else {
            GoogleMapUtils.bitmapDescriptorFromVector(
                context = context,
                vectorResId = R.drawable.ic_location_marker,
                tint = Color.Red.toArgb(),
                sizeInPx = flagSize
            )
        }
    }
    val currentPosLargeIcon = remember(isRunningFinished) {
        if (isRunningFinished) return@remember null

        GoogleMapUtils.bitmapDescriptorFromVector(
            context = context,
            vectorResId = R.drawable.ic_circle,
            tint = md_theme_light_primary.copy(alpha = 0.4f).toArgb(),
            sizeInPx = largeLocationIconSize
        )
    }

    currentPosLargeIcon?.let {
        Marker(
            icon = currentPosLargeIcon,
            state = largeLastMarkerState,
            anchor = Offset(0.5f, 0.5f),
            visible = lastLocationPoint != null
        )
    }

    Marker(
        icon = currentPosIcon,
        state = lastMarkerState,
        anchor = if (isRunningFinished) flagOffset else Offset(0.5f, 0.5f),
        visible = lastLocationPoint != null
    )

    firstLocationPoint?.let {
        val firstLocationIcon = remember(isRunningFinished) {
            GoogleMapUtils.bitmapDescriptorFromVector(
                context = context,
                vectorResId = R.drawable.ic_location_marker,
                tint = RTColor.CHATEAU_GREEN.toArgb(),
                sizeInPx = flagSize
            )
        }
        Marker(
            icon = firstLocationIcon,
            state = rememberMarkerState(position = it.locationInfo.toLatLng()),
            anchor = flagOffset,

            )
    }
}
@Composable
fun ShowMapLoadingProgressBar(
    visible: Boolean = false
) {
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f),
        visible = visible,
        enter = EnterTransition.None,
        exit = fadeOut()
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .wrapContentSize()
        )
    }
}