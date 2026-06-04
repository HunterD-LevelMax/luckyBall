package com.euphoria.ballneonogame.gsde

import android.content.res.Configuration
import android.graphics.Paint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.euphoria.ballneonogame.R
import com.euphoria.ballneonogame.uiasf.theme.BallColor
import com.euphoria.ballneonogame.uiasf.theme.BallGlow
import com.euphoria.ballneonogame.uiasf.theme.DarkCard
import com.euphoria.ballneonogame.uiasf.theme.DarkSurface
import com.euphoria.ballneonogame.uiasf.theme.NeonBlue
import com.euphoria.ballneonogame.uiasf.theme.NeonCyan
import com.euphoria.ballneonogame.uiasf.theme.NeonGreen
import com.euphoria.ballneonogame.uiasf.theme.NeonOrange
import com.euphoria.ballneonogame.uiasf.theme.NeonPurple
import com.euphoria.ballneonogame.uiasf.theme.NeonRed
import com.euphoria.ballneonogame.uiasf.theme.NeonYellow
import com.euphoria.ballneonogame.uiasf.theme.PegColor
import com.euphoria.ballneonogame.uiasf.theme.SlotColors
import com.euphoria.ballneonogame.uiasf.theme.TextPrimary
import com.euphoria.ballneonogame.uiasf.theme.TextSecondary

@Composable
fun PlksafScressa(
    modifier: Modifier = Modifier,
    viewModel: PlidfsGaBiew = viewModel()
) {
    val context = LocalContext.current
    val sourgnMabgfs = remember { SourgnMabgfs(context) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    DisposableEffect(Unit) {
        viewModel.onKnockSound = { sourgnMabgfs.playKnock() }
        viewModel.onGetMoneySound = { sourgnMabgfs.playGetMoney() }
        viewModel.onSelectValueSound = { sourgnMabgfs.playSelectValue() }
        
        onDispose {
            sourgnMabgfs.release()
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF050510),
                        Color(0xFF0A0A1A),
                        Color(0xFF080818)
                    )
                )
            )
    ) {
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0D0D25),
                                    Color(0xFF0A0A1E)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(NeonPurple.copy(alpha = 0.6f), NeonBlue.copy(alpha = 0.3f))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    PlinkoBoard(
                        pegs = viewModel.pegs,
                        balls = viewModel.balls,
                        glowAlpha = glowAlpha,
                        pegHighlights = viewModel.pegHighlights
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(0.45f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PlinkoHeader(
                        balance = viewModel.balance,
                        glowAlpha = glowAlpha
                    )

                    BettingControls(
                        betAmount = viewModel.betAmount,
                        balance = viewModel.balance,
                        isDropping = viewModel.isDropping,
                        onIncreaseBet = { viewModel.increaseBet() },
                        onDecreaseBet = { viewModel.decreaseBet() },
                        onSetBet = { viewModel.setBet(it) },
                        onDropBall = { viewModel.dropBall() },
                        onReset = { viewModel.resetGame() }
                    )

                    AnimatedVisibility(
                        visible = viewModel.lastResult != null,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        viewModel.lastResult?.let { result ->
                            ResultBanner(result = result)
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PlinkoHeader(
                    balance = viewModel.balance,
                    glowAlpha = glowAlpha
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0D0D25),
                                    Color(0xFF0A0A1E)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(NeonPurple.copy(alpha = 0.6f), NeonBlue.copy(alpha = 0.3f))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    PlinkoBoard(
                        pegs = viewModel.pegs,
                        balls = viewModel.balls,
                        glowAlpha = glowAlpha,
                        pegHighlights = viewModel.pegHighlights
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(
                    visible = viewModel.lastResult != null,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    viewModel.lastResult?.let { result ->
                        ResultBanner(result = result)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                BettingControls(
                    betAmount = viewModel.betAmount,
                    balance = viewModel.balance,
                    isDropping = viewModel.isDropping,
                    onIncreaseBet = { viewModel.increaseBet() },
                    onDecreaseBet = { viewModel.decreaseBet() },
                    onSetBet = { viewModel.setBet(it) },
                    onDropBall = { viewModel.dropBall() },
                    onReset = { viewModel.resetGame() }
                )
            }
        }
    }
}

@Composable
fun PlinkoHeader(balance: Float, glowAlpha: Float, isLandscape: Boolean = false) {
    val configuration = LocalConfiguration.current
    val isActuallyLandscape = isLandscape || configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .border(
                width = 1.dp,
                color = NeonPurple.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = if (isActuallyLandscape) 12.dp else 16.dp, vertical = if (isActuallyLandscape) 6.dp else 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.game_title),
            style = TextStyle(
                fontSize = if (isActuallyLandscape) 20.sp else 28.sp,
                fontWeight = FontWeight.Black,
                brush = Brush.horizontalGradient(
                    colors = listOf(NeonPurple, NeonBlue, NeonCyan)
                ),
                shadow = Shadow(
                    color = NeonPurple.copy(alpha = glowAlpha),
                    blurRadius = 20f
                )
            )
        )

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = stringResource(R.string.balance_label),
                style = TextStyle(
                    fontSize = if (isActuallyLandscape) 8.sp else 10.sp,
                    color = TextSecondary,
                    letterSpacing = 2.sp
                )
            )
            Text(
                text = "$${"%.2f".format(balance)}",
                style = TextStyle(
                    fontSize = if (isActuallyLandscape) 16.sp else 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonYellow,
                    shadow = Shadow(
                        color = NeonYellow.copy(alpha = 0.8f),
                        blurRadius = 15f
                    )
                )
            )
        }
    }
}

@Composable
fun PlinkoBoard(
    pegs: List<PegPosition>,
    balls: List<Ball>,
    glowAlpha: Float,
    pegHighlights: List<PegHighlight> = emptyList()
) {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val width = size.width
        val height = size.height

        val slotCount = PlidfsGaBiew.MULTIPLIERS.size
        val slotWidth = width * (PlidfsGaBiew.BOARD_RIGHT - PlidfsGaBiew.Companion.BOARD_LEFT) / slotCount
        val slotsStartX = width * PlidfsGaBiew.Companion.BOARD_LEFT
        val slotsY = height * PlidfsGaBiew.Companion.BOARD_BOTTOM

        for (i in 0 until slotCount) {
            val slotX = slotsStartX + i * slotWidth
            val slotColor = SlotColors[i]

            drawRect(
                color = slotColor.copy(alpha = 0.15f),
                topLeft = Offset(slotX, slotsY - height * 0.04f),
                size = Size(slotWidth - 2f, height * 0.1f)
            )

            drawRect(
                color = slotColor.copy(alpha = 0.6f),
                topLeft = Offset(slotX, slotsY - height * 0.04f),
                size = Size(slotWidth - 2f, height * 0.1f),
                style = Stroke(width = 1.5f)
            )

            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    color = slotColor.copy(alpha = 0.9f).toArgb()
                    textSize = slotWidth * 0.28f
                    textAlign = Paint.Align.CENTER
                    isFakeBoldText = true
                    isAntiAlias = true
                }
                val multiplierText = if (PlidfsGaBiew.Companion.MULTIPLIERS[i] < 1f) {
                    ".${(PlidfsGaBiew.Companion.MULTIPLIERS[i] * 10).toInt()}x"
                } else {
                    "${PlidfsGaBiew.Companion.MULTIPLIERS[i].toInt()}x"
                }
                canvas.nativeCanvas.drawText(
                    multiplierText,
                    slotX + slotWidth / 2f,
                    slotsY + height * 0.04f,
                    paint
                )
            }
        }

        for ((pegIndex, peg) in pegs.withIndex()) {
            val pegX = peg.x * width
            val pegY = peg.y * height
            val pegRadius = PlidfsGaBiew.Companion.PEG_RADIUS * width

            val highlight = pegHighlights.find { it.pegIndex == pegIndex }
            val highlightIntensity = if (highlight != null) {
                val elapsed = System.currentTimeMillis() - highlight.timestamp
                val progress = elapsed.toFloat() / PlidfsGaBiew.Companion.HIGHLIGHT_DURATION_MS
                (1f - progress).coerceIn(0f, 1f)
            } else 0f

            if (highlightIntensity > 0f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            NeonCyan.copy(alpha = highlightIntensity * 0.8f),
                            NeonBlue.copy(alpha = highlightIntensity * 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(pegX, pegY),
                        radius = pegRadius * 4f
                    ),
                    radius = pegRadius * 4f,
                    center = Offset(pegX, pegY)
                )
                
                drawCircle(
                    color = Color.White.copy(alpha = highlightIntensity * 0.9f),
                    radius = pegRadius * 2f,
                    center = Offset(pegX, pegY)
                )
            }

            drawCircle(
                color = PegColor.copy(alpha = 0.15f * glowAlpha + highlightIntensity * 0.3f),
                radius = pegRadius * 2.5f,
                center = Offset(pegX, pegY)
            )

            val pegBodyColors = if (highlightIntensity > 0f) {
                listOf(
                    Color.White.copy(alpha = 1f),
                    NeonCyan.copy(alpha = highlightIntensity * 0.8f + 0.2f),
                    NeonBlue.copy(alpha = highlightIntensity * 0.6f + 0.2f)
                )
            } else {
                listOf(Color.White, PegColor)
            }
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = pegBodyColors,
                    center = Offset(pegX - pegRadius * 0.3f, pegY - pegRadius * 0.3f),
                    radius = pegRadius
                ),
                radius = pegRadius,
                center = Offset(pegX, pegY)
            )
        }

        for (ball in balls) {
            if (!ball.active) continue
            val ballX = ball.x * width
            val ballY = ball.y * height
            val ballRadius = PlidfsGaBiew.Companion.BALL_RADIUS * width

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        BallGlow.copy(alpha = 0.6f),
                        BallGlow.copy(alpha = 0f)
                    ),
                    center = Offset(ballX, ballY),
                    radius = ballRadius * 3f
                ),
                radius = ballRadius * 3f,
                center = Offset(ballX, ballY)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, BallColor, BallGlow),
                    center = Offset(ballX - ballRadius * 0.3f, ballY - ballRadius * 0.3f),
                    radius = ballRadius
                ),
                radius = ballRadius,
                center = Offset(ballX, ballY)
            )
        }
    }
}

@Composable
fun ResultBanner(result: SlotResult, isLandscape: Boolean = false) {
    val configuration = LocalConfiguration.current
    val isActuallyLandscape = isLandscape || configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    val slotColor = SlotColors[result.slot]
    val isWin = result.multiplier >= 1f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        slotColor.copy(alpha = 0.2f),
                        slotColor.copy(alpha = 0.1f),
                        slotColor.copy(alpha = 0.2f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = slotColor.copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(vertical = if (isActuallyLandscape) 4.dp else 8.dp, horizontal = if (isActuallyLandscape) 10.dp else 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isWin) stringResource(R.string.win_message) else stringResource(R.string.try_again_message),
                style = TextStyle(
                    fontSize = if (isActuallyLandscape) 12.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = slotColor
                )
            )
            Text(
                text = "${result.multiplier}x",
                style = TextStyle(
                    fontSize = if (isActuallyLandscape) 14.sp else 20.sp,
                    fontWeight = FontWeight.Black,
                    color = slotColor,
                    shadow = Shadow(color = slotColor, blurRadius = 10f)
                )
            )
            Text(
                text = "+$${"%.2f".format(result.winAmount)}",
                style = TextStyle(
                    fontSize = if (isActuallyLandscape) 12.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isWin) NeonGreen else NeonRed
                )
            )
        }
    }
}

@Composable
fun BettingControls(
    betAmount: Float,
    balance: Float,
    isDropping: Boolean,
    onIncreaseBet: () -> Unit,
    onDecreaseBet: () -> Unit,
    onSetBet: (Float) -> Unit,
    onDropBall: () -> Unit,
    onReset: () -> Unit,
    isLandscape: Boolean = false
) {
    val configuration = LocalConfiguration.current
    val isActuallyLandscape = isLandscape || configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard)
            .border(
                width = 1.dp,
                color = NeonPurple.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(if (isActuallyLandscape) 10.dp else 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.bet_amount_label),
            style = TextStyle(
                fontSize = if (isActuallyLandscape) 9.sp else 11.sp,
                color = TextSecondary,
                letterSpacing = 2.sp
            )
        )

        Spacer(modifier = Modifier.height(if (isActuallyLandscape) 4.dp else 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BetButton(
                text = "½",
                color = NeonOrange,
                onClick = onDecreaseBet,
                enabled = !isDropping && betAmount > 1f,
                isLandscape = isActuallyLandscape
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = if (isActuallyLandscape) 6.dp else 12.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurface)
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(NeonPurple.copy(alpha = 0.5f), NeonBlue.copy(alpha = 0.5f))
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(vertical = if (isActuallyLandscape) 6.dp else 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$${"%.2f".format(betAmount)}",
                    style = TextStyle(
                        fontSize = if (isActuallyLandscape) 16.sp else 22.sp,
                        fontWeight = FontWeight.Bold,
                        brush = Brush.horizontalGradient(
                            colors = listOf(NeonPurple, NeonBlue)
                        )
                    )
                )
            }

            BetButton(
                text = "2×",
                color = NeonGreen,
                onClick = onIncreaseBet,
                enabled = !isDropping && betAmount < balance,
                isLandscape = isActuallyLandscape
            )
        }

        Spacer(modifier = Modifier.height(if (isActuallyLandscape) 6.dp else 12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(1f, 5f, 10f, 50f, 100f).forEach { amount ->
                QuickBetButton(
                    amount = amount,
                    isSelected = betAmount == amount,
                    enabled = !isDropping && amount <= balance,
                    onClick = { onSetBet(amount) },
                    isLandscape = isActuallyLandscape
                )
            }
        }

        Spacer(modifier = Modifier.height(if (isActuallyLandscape) 6.dp else 12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(if (isActuallyLandscape) 4.dp else 8.dp)
        ) {
            Button(
                onClick = onReset,
                modifier = Modifier
                    .weight(0.4f)
                    .height(if (isActuallyLandscape) 40.dp else 52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A1A35),
                    contentColor = TextSecondary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.reset_button),
                    style = TextStyle(
                        fontSize = if (isActuallyLandscape) 11.sp else 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            Button(
                onClick = onDropBall,
                modifier = Modifier
                    .weight(0.6f)
                    .height(if (isActuallyLandscape) 40.dp else 52.dp),
                enabled = !isDropping && balance >= betAmount,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = TextPrimary,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = TextSecondary.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (!isDropping && balance >= betAmount) {
                                Brush.horizontalGradient(
                                    colors = listOf(NeonPurple, NeonBlue)
                                )
                            } else {
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF333355),
                                        Color(0xFF222244)
                                    )
                                )
                            },
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isDropping) stringResource(R.string.dropping_status) else stringResource(
                            R.string.drop_ball_button),
                        style = TextStyle(
                            fontSize = if (isActuallyLandscape) 12.sp else 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = if (!isDropping && balance >= betAmount) TextPrimary
                            else TextSecondary.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun BetButton(
    text: String,
    color: Color,
    onClick: () -> Unit,
    enabled: Boolean,
    isLandscape: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(if (isLandscape) 36.dp else 44.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = if (enabled) 0.15f else 0.05f))
            .border(
                width = 1.dp,
                color = color.copy(alpha = if (enabled) 0.7f else 0.2f),
                shape = CircleShape
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = if (isLandscape) 13.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                color = color.copy(alpha = if (enabled) 1f else 0.3f)
            )
        )
    }
}

@Composable
fun QuickBetButton(
    amount: Float,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    isLandscape: Boolean = false
) {
    val color = if (isSelected) NeonPurple else TextSecondary

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) NeonPurple.copy(alpha = 0.2f)
                else Color.Transparent
            )
            .border(
                width = 1.dp,
                color = color.copy(alpha = if (enabled) 0.6f else 0.2f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = if (isLandscape) 6.dp else 8.dp, vertical = if (isLandscape) 4.dp else 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$${amount.toInt()}",
            style = TextStyle(
                fontSize = if (isLandscape) 10.sp else 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = color.copy(alpha = if (enabled) 1f else 0.3f)
            )
        )
    }
}
