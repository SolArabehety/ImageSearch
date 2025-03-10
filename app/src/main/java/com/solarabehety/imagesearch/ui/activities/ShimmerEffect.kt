package com.solarabehety.imagesearch.ui.activities

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color


@Composable
fun Modifier.shimmerEffect(): Modifier {
    val shimmerColors = listOf(
        Color.Gray.copy(alpha = 0.6f),
        Color.Gray.copy(alpha = 0.3f),
        Color.Gray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateX by transition.animateFloat(
        initialValue = -300f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerAnimation"
    )

    return this.drawWithCache {
        val gradient = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateX, 0f),
            end = Offset(translateX + 300f, 0f)
        )
        onDrawWithContent {
            drawContent()
            drawRect(brush = gradient)
        }
    }
}
