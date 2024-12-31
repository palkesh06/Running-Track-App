package com.example.maps.ui.common.compose.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object ComposeUtil {
    const val slideDownInDuration = 250
    const val slideDownOutDuration = 250

@Composable
fun SlideUpAnimationVertically(
    modifier: Modifier = Modifier,
    visible: Boolean,
    inDurationInMillis: Int = slideDownInDuration,
    outDurationInMillis: Int = slideDownOutDuration,
    content: @Composable AnimatedVisibilityScope.() -> Unit) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = inDurationInMillis,
                easing = LinearOutSlowInEasing
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = outDurationInMillis,
                easing = FastOutLinearInEasing
            )
        ),
        content = content
    )
}
}