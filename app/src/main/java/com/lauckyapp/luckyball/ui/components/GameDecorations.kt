package com.lauckyapp.luckyball.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lauckyapp.luckyball.ui.theme.OnSurface
import com.lauckyapp.luckyball.ui.theme.Outline
import com.lauckyapp.luckyball.ui.theme.PrimaryContainer
import com.lauckyapp.luckyball.ui.theme.Secondary
import com.lauckyapp.luckyball.ui.theme.Tertiary

@Composable
fun SkyDecorations(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        repeat(8) { i ->
            Box(
                modifier = Modifier
                    .align(
                        when (i % 4) {
                            0 -> Alignment.TopStart
                            1 -> Alignment.TopEnd
                            2 -> Alignment.CenterStart
                            else -> Alignment.CenterEnd
                        },
                    )
                    .offset(
                        x = (i * 17).dp,
                        y = (i * 23 + 40).dp,
                    )
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(PrimaryContainer.copy(alpha = 0.35f)),
            )
        }
    }
}

@Composable
fun CheeringCrowd(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "crowd")
    val bounce by transition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(tween(750, easing = LinearEasing), RepeatMode.Reverse),
        label = "bounce",
    )

    Row(
        modifier = modifier
            .padding(start = 8.dp, bottom = 8.dp)
            .offset(y = bounce.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        MiniBallFace(color = Tertiary, size = 36.dp, offsetY = (-8).dp)
        MiniBallFace(color = Secondary, size = 44.dp, offsetY = 0.dp)
    }
}

@Composable
private fun MiniBallFace(
    color: androidx.compose.ui.graphics.Color,
    size: Dp,
    offsetY: Dp,
) {
    Box(
        modifier = Modifier
            .offset(y = offsetY)
            .padding(horizontal = 4.dp)
            .size(size)
            .clip(CircleShape)
            .background(color)
            .border(2.dp, Outline, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Row {
            Box(
                Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(OnSurface),
            )
            Box(
                Modifier
                    .padding(start = 4.dp)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(OnSurface),
            )
        }
    }
}
