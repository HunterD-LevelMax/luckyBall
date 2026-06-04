package com.lauckyapp.luckyball.ui.components

import android.graphics.Paint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.lauckyapp.luckyball.data.models.Ball
import com.lauckyapp.luckyball.data.models.PegHighlight
import com.lauckyapp.luckyball.data.models.PegPosition
import com.lauckyapp.luckyball.ui.theme.BoardGrass
import com.lauckyapp.luckyball.ui.theme.BoardGrassDark
import com.lauckyapp.luckyball.ui.theme.BoardSkyMid
import com.lauckyapp.luckyball.ui.theme.BoardSkyTop
import com.lauckyapp.luckyball.ui.theme.BoardStone
import com.lauckyapp.luckyball.ui.theme.OnSurfaceVariant
import com.lauckyapp.luckyball.ui.theme.Outline
import com.lauckyapp.luckyball.ui.theme.PegToon
import com.lauckyapp.luckyball.ui.theme.PrimaryContainer
import com.lauckyapp.luckyball.ui.theme.ShapeLg
import com.lauckyapp.luckyball.ui.theme.ToonSlotColors
import com.lauckyapp.luckyball.utils.PlinkoEngine

@Composable
fun LuckyBallPlinkoBoard(
    pegs: List<PegPosition>,
    balls: List<Ball>,
    pegHighlights: List<PegHighlight>,
    frameTick: Long,
    @DrawableRes ballDrawableRes: Int,
    canDrop: Boolean,
    onTapDrop: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val redraw = frameTick
    val context = LocalContext.current
    val ballBitmap = remember(ballDrawableRes) {
        ContextCompat.getDrawable(context, ballDrawableRes)
            ?.toBitmap(96, 96)
            ?.asImageBitmap()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .border(6.dp, Outline, ShapeLg)
            .background(BoardStone, ShapeLg),
    ) {
        val boardWidthPx = constraints.maxWidth.toFloat()
        val boardHeightPx = constraints.maxHeight.toFloat()

        Canvas(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(canDrop) {
                    if (!canDrop) return@pointerInput
                    detectTapGestures { offset ->
                        if (offset.y <= boardHeightPx * 0.25f) {
                            val normalizedX = offset.x / boardWidthPx
                            onTapDrop(normalizedX)
                        }
                    }
                },
        ) {
            val tick = redraw
            val w = size.width
            val h = size.height

            drawRect(
                brush = Brush.verticalGradient(
                    0f to BoardSkyTop,
                    0.65f to BoardSkyMid,
                    0.7f to BoardGrass,
                    1f to BoardGrassDark,
                    startY = 0f,
                    endY = h,
                ),
                size = size,
            )

            var dy = 8f
            while (dy < h * 0.7f) {
                var dx = 8f
                while (dx < w) {
                    drawCircle(OnSurfaceVariant.copy(alpha = 0.15f), 2f, Offset(dx, dy))
                    dx += 20f
                }
                dy += 20f
            }

            val slotCount = PlinkoEngine.MULTIPLIERS.size
            val slotWidth = w * (PlinkoEngine.BOARD_RIGHT - PlinkoEngine.BOARD_LEFT) / slotCount
            val slotsStartX = w * PlinkoEngine.BOARD_LEFT
            val slotsY = h * PlinkoEngine.BOARD_BOTTOM
            val centerSlot = 5

            for (i in 0 until slotCount) {
                val slotX = slotsStartX + i * slotWidth
                val slotColor = ToonSlotColors.getOrElse(i) { Color.Gray }
                val isCenter = i == centerSlot
                val slotHeight = if (isCenter) h * 0.12f else h * 0.1f
                val top = slotsY - slotHeight + if (isCenter) -h * 0.02f else 0f

                drawRect(
                    color = slotColor.copy(alpha = 0.9f),
                    topLeft = Offset(slotX + 1f, top),
                    size = Size(slotWidth - 2f, slotHeight),
                )
                drawRect(
                    color = Outline,
                    topLeft = Offset(slotX + 1f, top),
                    size = Size(slotWidth - 2f, slotHeight),
                    style = Stroke(3f),
                )
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        color = Color.White.toArgb()
                        textSize = slotWidth * (if (isCenter) 0.32f else 0.28f)
                        textAlign = Paint.Align.CENTER
                        isFakeBoldText = true
                        isAntiAlias = true
                    }
                    val mult = PlinkoEngine.MULTIPLIERS[i]
                    val text = if (mult < 1f) ".${(mult * 10).toInt()}x" else "${mult.toInt()}x"
                    canvas.nativeCanvas.drawText(
                        text,
                        slotX + slotWidth / 2f,
                        top + slotHeight * 0.62f,
                        paint,
                    )
                }
            }

            pegs.forEachIndexed { pegIndex, peg ->
                val pegX = peg.x * w
                val pegY = peg.y * h
                val pegRadius = PlinkoEngine.PEG_RADIUS * w

                val highlight = pegHighlights.find { it.pegIndex == pegIndex }
                val highlightIntensity = if (highlight != null) {
                    val elapsed = System.currentTimeMillis() - highlight.timestamp
                    val progress = elapsed.toFloat() / PlinkoEngine.HIGHLIGHT_DURATION_MS
                    (1f - progress).coerceIn(0f, 1f)
                } else 0f

                if (highlightIntensity > 0f) {
                    drawCircle(
                        PrimaryContainer.copy(alpha = highlightIntensity * 0.6f),
                        pegRadius * 3f,
                        Offset(pegX, pegY),
                    )
                }
                drawCircle(Color.White, pegRadius + 2f, Offset(pegX, pegY))
                drawCircle(
                    if (highlightIntensity > 0.3f) PrimaryContainer else PegToon,
                    pegRadius,
                    Offset(pegX, pegY),
                )
                drawCircle(Color.Transparent, pegRadius, Offset(pegX, pegY), style = Stroke(2f))
            }

            if (tick >= 0) {
                for (ball in balls) {
                    if (!ball.active) continue
                    val ballX = ball.x * w
                    val ballY = ball.y * h
                    val ballRadius = PlinkoEngine.BALL_RADIUS * w
                    val topLeft = Offset(ballX - ballRadius, ballY - ballRadius)
                    val sizePx = ballRadius * 2f
                    if (ballBitmap != null) {
                        drawImage(
                            image = ballBitmap,
                            topLeft = topLeft,
                        )
                    } else {
                        drawCircle(PrimaryContainer, ballRadius, Offset(ballX, ballY))
                    }
                }
            }
        }
    }
}
