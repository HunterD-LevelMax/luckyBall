package com.lauckyapp.luckyball.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lauckyapp.luckyball.R
import com.lauckyapp.luckyball.ui.theme.BrightOrange
import com.lauckyapp.luckyball.ui.theme.GrassGreen
import com.lauckyapp.luckyball.ui.theme.OnPrimary
import com.lauckyapp.luckyball.ui.theme.OnPrimaryContainer
import com.lauckyapp.luckyball.ui.theme.OnSurface
import com.lauckyapp.luckyball.ui.theme.OnSurfaceVariant
import com.lauckyapp.luckyball.ui.theme.Outline
import com.lauckyapp.luckyball.ui.theme.Primary
import com.lauckyapp.luckyball.ui.theme.PrimaryContainer
import com.lauckyapp.luckyball.ui.theme.Secondary
import com.lauckyapp.luckyball.ui.theme.ShapePill
import com.lauckyapp.luckyball.ui.theme.Surface
import com.lauckyapp.luckyball.ui.theme.SurfaceContainer
import com.lauckyapp.luckyball.ui.theme.Tertiary
import com.lauckyapp.luckyball.viewmodel.GameViewModel

@Composable
fun GameBettingPanel(
    betAmount: Int,
    balance: Int,
    isDropping: Boolean,
    isAutoSpinActive: Boolean,
    autoSpinRemaining: Int,
    onIncreaseBet: () -> Unit,
    onDecreaseBet: () -> Unit,
    onSetBet: (Int) -> Unit,
    onDropBall: () -> Unit,
    onStartAutoSpin: (Int) -> Unit,
    onStopAutoSpin: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val controlsEnabled = !isDropping && !isAutoSpinActive
    val dropEnabled = !isDropping && balance >= betAmount && !isAutoSpinActive
    val dropLabel = when {
        isAutoSpinActive && autoSpinRemaining < 0 -> stringResource(R.string.autospin_infinite)
        isAutoSpinActive -> stringResource(R.string.autospin_count, autoSpinRemaining)
        isDropping -> stringResource(R.string.dropping)
        else -> stringResource(R.string.drop_ball)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface)
            .border(width = 4.dp, color = Outline)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            GameViewModel.QUICK_BETS.forEach { amount ->
                QuickBetChip(
                    amount = amount,
                    selected = betAmount == amount,
                    enabled = controlsEnabled && amount <= balance,
                    onClick = { onSetBet(amount) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ShapePill)
                .background(SurfaceContainer)
                .border(4.dp, Outline, ShapePill)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            RoundBetButton(
                text = stringResource(R.string.bet_half),
                containerColor = BrightOrange,
                enabled = controlsEnabled && betAmount > 1,
                onClick = onDecreaseBet,
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(R.string.current_bet).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = stringResource(R.string.balance_format, betAmount),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Primary,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            RoundBetButton(
                text = stringResource(R.string.bet_double),
                containerColor = GrassGreen,
                enabled = controlsEnabled && betAmount < balance,
                onClick = onIncreaseBet,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(ShapePill)
                .background(
                    if (dropEnabled) BrightOrange else OnSurfaceVariant.copy(alpha = 0.35f),
                )
                .border(4.dp, Outline, ShapePill)
                .clickable(enabled = dropEnabled, onClick = onDropBall),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = dropLabel,
                style = MaterialTheme.typography.headlineSmall,
                color = OnPrimary,
                fontWeight = FontWeight.ExtraBold,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(R.string.autospin),
                style = MaterialTheme.typography.labelMedium,
                color = OnSurfaceVariant,
                fontWeight = FontWeight.Bold,
            )
            GameViewModel.AUTOSPIN_OPTIONS.forEach { count ->
                val label = if (count < 0) "∞" else count.toString()
                AutoSpinChip(
                    label = label,
                    enabled = !isDropping && !isAutoSpinActive && balance >= betAmount,
                    onClick = { onStartAutoSpin(count) },
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = onReset,
                enabled = controlsEnabled,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface),
            ) {
                Text(
                    text = stringResource(R.string.reset_board),
                    fontWeight = FontWeight.Bold,
                )
            }
            OutlinedButton(
                onClick = onStopAutoSpin,
                enabled = isAutoSpinActive,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Tertiary),
            ) {
                Text(
                    text = stringResource(R.string.autospin_stop),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun QuickBetChip(
    amount: Int,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) PrimaryContainer else SurfaceContainer)
            .border(2.dp, if (selected) Primary else Outline.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.quick_bet_format, amount),
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) OnPrimaryContainer else OnSurfaceVariant,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun RowScope.AutoSpinChip(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (enabled) Secondary.copy(alpha = 0.18f) else SurfaceContainer)
            .border(2.dp, Outline.copy(alpha = if (enabled) 0.7f else 0.3f), RoundedCornerShape(10.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (enabled) OnSurface else OnSurfaceVariant.copy(alpha = 0.5f),
        )
    }
}

@Composable
private fun RoundBetButton(
    text: String,
    containerColor: Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(containerColor.copy(alpha = if (enabled) 1f else 0.4f))
            .border(3.dp, Outline, CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = OnPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
    }
}
