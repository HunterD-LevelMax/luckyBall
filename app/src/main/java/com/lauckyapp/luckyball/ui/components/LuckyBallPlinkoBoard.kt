package com.lauckyapp.luckyball.ui.components

import android.graphics.Paint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
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

/** Only affects how big the ball looks — physics uses [PlinkoEngine.BALL_RADIUS]. */
private const val VISUAL_BALL_SCALE = 1.35f

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
            .fillMaxSize()
            .border(4.dp, Outline, ShapeLg)
            .background(BoardStone, ShapeLg),
    ) {
        val boardWidthPx = constraints.maxWidth.toFloat()
        val boardHeightPx = constraints.maxHeight.toFloat()

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(canDrop) {
                    if (!canDrop) return@pointerInput
                    detectTapGestures { offset ->
                        if (offset.y <= boardHeightPx * 0.22f) {
                            onTapDrop(offset.x / boardWidthPx)
                        }
                    }
                },
        ) {
            val w = size.width
            val h = size.height
            val slotBottom = h * (1f - PlinkoEngine.SLOT_BOTTOM_INSET)
            val baseSlotHeight = h * PlinkoEngine.SLOT_HEIGHT_FRAC
            val slotZoneTop = slotBottom - baseSlotHeight
            val pegZoneBottom = h * PlinkoEngine.PEG_AREA_BOTTOM

            drawRect(
                brush = Brush.verticalGradient(
                    0f to BoardSkyTop,
                    0.55f to BoardSkyMid,
                    (pegZoneBottom / h - 0.04f).coerceAtLeast(0.4f) to BoardGrass,
                    slotZoneTop / h to BoardGrassDark,
                    1f to BoardGrassDark,
                    startY = 0f,
                    endY = h,
                ),
                size = size,
            )

            var dy = 8f
            while (dy < pegZoneBottom) {
                var dx = 8f
                while (dx < w) {
                    drawCircle(OnSurfaceVariant.copy(alpha = 0.12f), 2f, Offset(dx, dy))
                    dx += 20f
                }
                dy += 20f
            }

            val slotCount = PlinkoEngine.MULTIPLIERS.size
            val slotWidth = w * (PlinkoEngine.BOARD_RIGHT - PlinkoEngine.BOARD_LEFT) / slotCount
            val slotsStartX = w * PlinkoEngine.BOARD_LEFT
            val centerSlot = 5

            for (i in 0 until slotCount) {
                val slotX = slotsStartX + i * slotWidth
                val slotColor = ToonSlotColors.getOrElse(i) { Color.Gray }
                val isCenter = i == centerSlot
                val slotHeight = baseSlotHeight
                val top = slotBottom - slotHeight

                drawRect(
                    color = slotColor.copy(alpha = 0.95f),
                    topLeft = Offset(slotX + 2f, top),
                    size = Size(slotWidth - 4f, slotHeight),
                )
                drawRect(
                    color = Outline,
                    topLeft = Offset(slotX + 2f, top),
                    size = Size(slotWidth - 4f, slotHeight),
                    style = Stroke(if (isCenter) 3f else 2.5f),
                )
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        color = Color.White.toArgb()
                        textSize = slotWidth * (if (isCenter) 0.38f else 0.36f)
                        textAlign = Paint.Align.CENTER
                        isFakeBoldText = true
                        isAntiAlias = true
                    }
                    val mult = PlinkoEngine.MULTIPLIERS[i]
                    val text = PlinkoEngine.formatMultiplier(mult)
                    canvas.nativeCanvas.drawText(
                        text,
                        slotX + slotWidth / 2f,
                        top + slotHeight * 0.68f,
                        paint,
                    )
                }
            }

            pegs.forEachIndexed { pegIndex, peg ->
                val pegX = peg.x * w
                val pegY = peg.y * h
                if (pegY > pegZoneBottom) return@forEachIndexed

                val pegRadius = PlinkoEngine.PEG_RADIUS * w
                val highlight = pegHighlights.find { it.pegIndex == pegIndex }
                val highlightIntensity = if (highlight != null) {
                    val elapsed = System.currentTimeMillis() - highlight.timestamp
                    (1f - elapsed.toFloat() / PlinkoEngine.HIGHLIGHT_DURATION_MS).coerceIn(0f, 1f)
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

            if (redraw >= 0) {
                for (ball in balls) {
                    if (!ball.active) continue
                    val ballX = ball.x * w
                    val ballY = ball.y * h
                    val ballDiameter = (
                        PlinkoEngine.BALL_RADIUS * w * 2f * VISUAL_BALL_SCALE
                        ).toInt().coerceAtLeast(22)
                    val topLeft = Offset(ballX - ballDiameter / 2f, ballY - ballDiameter / 2f)
                    if (ballBitmap != null) {
                        drawImage(
                            image = ballBitmap,
                            dstOffset = IntOffset(topLeft.x.toInt(), topLeft.y.toInt()),
                            dstSize = IntSize(ballDiameter, ballDiameter),
                        )
                    } else {
                        drawCircle(PrimaryContainer, ballDiameter / 2f, Offset(ballX, ballY))
                    }
                }
            }
        }
    }
}
