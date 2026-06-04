package com.lauckyapp.luckyball.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lauckyapp.luckyball.R
import com.lauckyapp.luckyball.ui.theme.SplashTheme
import kotlinx.coroutines.delay

/**
 * Splash-экран: градиент primaryDark → primary, анимированный шарик Plinko,
 * минимум 1.5 с (или дольше, пока не загружены данные), затем [onFinished].
 * Затухание при переходе — в NavGraph (fadeOut / fadeIn).
 */
@Composable
fun SplashScreen(
    isDataLoaded: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(isDataLoaded) {
        val startedAt = System.currentTimeMillis()
        while (!isDataLoaded) {
            delay(32L)
        }
        val elapsed = System.currentTimeMillis() - startedAt
        val remaining = SplashTheme.MIN_DISPLAY_MS - elapsed
        if (remaining > 0) delay(remaining)
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SplashTheme.backgroundGradient),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            SplashBallLogo(
                ballDrawableRes = R.drawable.cheerful,
                modifier = Modifier.size(160.dp),
            )

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayLarge,
                color = SplashTheme.titleColor,
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** Мини-поле Plinko: пины + падающий/пульсирующий шарик из drawables. */
@Composable
private fun SplashBallLogo(
    ballDrawableRes: Int,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splashBall")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "wobble",
    )

    val dropOffset by infiniteTransition.animateFloat(
        initialValue = -36f,
        targetValue = 36f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "drop",
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        repeat(4) { row ->
            val pinsInRow = row + 3
            val rowOffsetY = 12 + row * 22
            repeat(pinsInRow) { col ->
                val xOffset = (-((pinsInRow - 1) * 14) / 2) + col * 14
                Box(
                    modifier = Modifier
                        .offset(x = xOffset.dp, y = rowOffsetY.dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(SplashTheme.pegColor),
                )
            }
        }

        Image(
            painter = painterResource(ballDrawableRes),
            contentDescription = stringResource(R.string.splash_ball_content_description),
            modifier = Modifier
                .offset(y = dropOffset.dp)
                .size(88.dp)
                .scale(pulseScale)
                .graphicsLayer { rotationZ = rotation }
                .clip(CircleShape),
        )
    }
}
