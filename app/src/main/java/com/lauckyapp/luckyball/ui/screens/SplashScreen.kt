package com.lauckyapp.luckyball.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lauckyapp.luckyball.R
import com.lauckyapp.luckyball.ui.theme.Outline
import com.lauckyapp.luckyball.ui.theme.PrimaryContainer
import com.lauckyapp.luckyball.ui.theme.SplashTheme
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private data class SplashFloatie(
    val xFraction: Float,
    val yFraction: Float,
    val size: Float,
    val color: Color,
    val isStar: Boolean,
    val phase: Int,
)

private val SplashFloatieDecorations = listOf(
    SplashFloatie(0.12f, 0.34f, 14f, SplashTheme.DotRed, false, 0),
    SplashFloatie(0.82f, 0.30f, 12f, SplashTheme.DotPurple, false, 1),
    SplashFloatie(0.18f, 0.58f, 10f, SplashTheme.DotTeal, false, 2),
    SplashFloatie(0.78f, 0.52f, 16f, SplashTheme.DotRed, false, 3),
    SplashFloatie(0.28f, 0.26f, 18f, SplashTheme.StarYellow, true, 4),
    SplashFloatie(0.68f, 0.38f, 14f, SplashTheme.StarYellow, true, 5),
    SplashFloatie(0.52f, 0.22f, 12f, SplashTheme.StarYellow, true, 6),
)

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
            .background(SplashTheme.SkyBlue),
    ) {
        SplashCloud(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 28.dp),
            scale = 1f,
        )
        SplashCloud(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 24.dp, top = 52.dp),
            scale = 0.85f,
        )

        SplashTitle(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 72.dp),
        )

        CrossedOutMiniBall(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 18.dp, top = 78.dp),
        )

        SplashFloaties()

        SplashHeroBall(
            modifier = Modifier.align(Alignment.Center),
        )

        SplashHills(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(170.dp),
        )

        SplashLoadingLabel(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 148.dp),
        )
    }
}

@Composable
private fun SplashTitle(modifier: Modifier = Modifier) {
    val gameTitle = stringResource(R.string.game_title)
    val titleLines = gameTitle.split(" ")
    Column(modifier = modifier) {
        titleLines.forEach { line ->
            StickerTitleLine(text = line)
        }
    }
}

@Composable
private fun StickerTitleLine(text: String) {
    val style = MaterialTheme.typography.displaySmall.copy(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 34.sp,
        letterSpacing = 1.sp,
        lineHeight = 36.sp,
    )
    Box {
        listOf(
            -2f to -2f,
            2f to -2f,
            -2f to 2f,
            2f to 2f,
            -2f to 0f,
            2f to 0f,
            0f to -2f,
            0f to 2f,
        ).forEach { (dx, dy) ->
            Text(
                text = text,
                style = style,
                color = SplashTheme.TitleOutline,
                modifier = Modifier.offset(dx.dp, dy.dp),
            )
        }
        Text(
            text = text,
            style = style,
            color = SplashTheme.TitleYellow,
        )
    }
}

@Composable
private fun CrossedOutMiniBall(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(52.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(PrimaryContainer)
                .border(3.dp, Outline, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.cheerful),
                contentDescription = null,
                modifier = Modifier.size(38.dp),
            )
        }
        Canvas(modifier = Modifier.size(52.dp)) {
            val stroke = 4.dp.toPx()
            drawLine(
                color = Outline,
                start = Offset(size.width * 0.15f, size.height * 0.15f),
                end = Offset(size.width * 0.85f, size.height * 0.85f),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = Outline,
                start = Offset(size.width * 0.85f, size.height * 0.15f),
                end = Offset(size.width * 0.15f, size.height * 0.85f),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun SplashCloud(
    modifier: Modifier = Modifier,
    scale: Float = 1f,
) {
    Row(
        modifier = modifier.scale(scale),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CloudPuff(size = 28.dp, offsetY = 6.dp)
        CloudPuff(size = 36.dp, offsetY = 0.dp)
        CloudPuff(size = 30.dp, offsetY = 4.dp)
        CloudPuff(size = 22.dp, offsetY = 8.dp)
    }
}

@Composable
private fun CloudPuff(
    size: androidx.compose.ui.unit.Dp,
    offsetY: androidx.compose.ui.unit.Dp,
) {
    Box(
        modifier = Modifier
            .offset(y = offsetY)
            .size(size)
            .clip(CircleShape)
            .background(SplashTheme.CloudWhite)
            .border(3.dp, Outline, CircleShape),
    )
}

@Composable
private fun SplashFloaties() {
    val transition = rememberInfiniteTransition(label = "splashFloaties")
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        SplashFloatieDecorations.forEach { floatie ->
            val drift by transition.animateFloat(
                initialValue = -6f,
                targetValue = 6f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200 + floatie.phase * 180, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "float_${floatie.phase}",
            )
            Box(
                modifier = Modifier.offset(
                    x = screenWidth * floatie.xFraction + drift.dp,
                    y = screenHeight * floatie.yFraction,
                ),
            ) {
                if (floatie.isStar) {
                    SplashStar(size = floatie.size.dp)
                } else {
                    Box(
                        modifier = Modifier
                            .size(floatie.size.dp)
                            .clip(CircleShape)
                            .background(floatie.color)
                            .border(2.dp, Outline, CircleShape),
                    )
                }
            }
        }
    }
}

@Composable
private fun SplashStar(size: androidx.compose.ui.unit.Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val radius = this.size.minDimension / 2f
        val path = starPath(radius)
        drawPath(path, color = SplashTheme.StarYellow)
        drawPath(path, color = Outline, style = Stroke(width = 2.dp.toPx()))
    }
}

private fun starPath(radius: Float): Path {
    val path = Path()
    val points = 5
    val innerRadius = radius * 0.42f
    for (i in 0 until points * 2) {
        val angle = PI / 2 + i * PI / points
        val r = if (i % 2 == 0) radius else innerRadius
        val x = radius + (cos(angle) * r).toFloat()
        val y = radius + (sin(angle) * r).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

@Composable
private fun SplashHeroBall(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "heroBall")
    val bounce by transition.animateFloat(
        initialValue = -18f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bounce",
    )
    val pulse by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )
    val wobble by transition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "wobble",
    )

    Box(
        modifier = modifier
            .offset(y = bounce.dp)
            .scale(pulse),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(188.dp)
                .offset(x = 5.dp, y = 6.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.55f)),
        )
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(PrimaryContainer)
                .border(4.dp, Outline, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.cheerful),
                contentDescription = stringResource(R.string.splash_ball_content_description),
                modifier = Modifier
                    .size(168.dp)
                    .offset(x = wobble.dp),
            )
        }
    }
}

@Composable
private fun SplashHills(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val stroke = 4.dp.toPx()

            val backHill = Path().apply {
                moveTo(0f, h * 0.58f)
                quadraticTo(w * 0.45f, h * 0.08f, w, h * 0.52f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(backHill, color = SplashTheme.HillBack)
            drawPath(backHill, color = Outline, style = Stroke(width = stroke))

            val frontHill = Path().apply {
                moveTo(0f, h * 0.72f)
                quadraticTo(w * 0.55f, h * 0.28f, w, h * 0.68f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(frontHill, color = SplashTheme.HillFront)
            drawPath(frontHill, color = Outline, style = Stroke(width = stroke))
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 34.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp),
        ) {
            HillDot(color = SplashTheme.DotRed)
            HillDot(color = SplashTheme.TitleYellow)
            HillDot(color = SplashTheme.DotTeal)
        }
    }
}

@Composable
private fun HillDot(color: Color) {
    Box(
        modifier = Modifier
            .size(14.dp)
            .clip(CircleShape)
            .background(color)
            .border(2.dp, Outline, CircleShape),
    )
}

@Composable
private fun SplashLoadingLabel(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "loading")
    val dotPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "dots",
    )
    val dots = ".".repeat(dotPhase.toInt().coerceIn(0, 3))
    Text(
        text = stringResource(R.string.splash_loading) + dots,
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
        ),
        color = Outline,
    )
}
