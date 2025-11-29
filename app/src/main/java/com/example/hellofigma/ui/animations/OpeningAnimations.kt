package com.example.hellofigma.ui.animations

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun animateOpeningGradient(): State<Float> {
    return rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing), // faster than 5000ms
            repeatMode = RepeatMode.Reverse
        )
    )
}

@Composable
fun animatedWordAlpha(isVisible: Boolean): Float {
    return animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy
        )
    ).value
}

@Composable
fun animatedWordScale(isFirst: Boolean, alpha: Float): Float {
    return animateFloatAsState(
        targetValue = if (isFirst && alpha > 0f) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy
        )
    ).value
}


