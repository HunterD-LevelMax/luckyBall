package com.lauckyapp.luckyball.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lauckyapp.luckyball.ui.theme.Outline
import com.lauckyapp.luckyball.ui.theme.ShapeMd
import com.lauckyapp.luckyball.ui.theme.Surface
import com.lauckyapp.luckyball.ui.theme.SurfaceContainer

object NeubrutalDefaults {
    val Shadow = 4.dp
    val Border = 3.dp
}

@Composable
fun NeubrutalSurface(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceContainer,
    shape: Shape = ShapeMd,
    shadowOffset: Dp = NeubrutalDefaults.Shadow,
    borderWidth: Dp = NeubrutalDefaults.Border,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .clip(shape)
                .background(Outline),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(backgroundColor)
                .border(borderWidth, Outline, shape),
        ) {
            content()
        }
    }
}

@Composable
fun NeubrutalBallPreview(
    drawableRes: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    enabled: Boolean = true,
) {
    val shadow = 3.dp
    Box(
        modifier = modifier.size(size + shadow),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .alpha(if (enabled) 1f else 0.35f)
                .clip(CircleShape)
                .background(Outline)
                .align(Alignment.BottomEnd),
        )
        Box(
            modifier = Modifier
                .size(size)
                .alpha(if (enabled) 1f else 0.5f)
                .clip(CircleShape)
                .background(Surface)
                .border(2.dp, Outline, CircleShape)
                .align(Alignment.TopStart),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(drawableRes),
                contentDescription = contentDescription,
                modifier = Modifier.size(size - 8.dp),
            )
        }
    }
}
