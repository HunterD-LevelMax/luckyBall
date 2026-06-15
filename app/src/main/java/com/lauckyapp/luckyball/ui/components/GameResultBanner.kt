package com.lauckyapp.luckyball.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lauckyapp.luckyball.R
import com.lauckyapp.luckyball.data.models.SlotResult
import com.lauckyapp.luckyball.ui.theme.GrassGreen
import com.lauckyapp.luckyball.ui.theme.OnPrimary
import com.lauckyapp.luckyball.ui.theme.Outline
import com.lauckyapp.luckyball.ui.theme.ShapePill
import com.lauckyapp.luckyball.ui.theme.SurfaceContainer
import com.lauckyapp.luckyball.ui.theme.Tertiary
import com.lauckyapp.luckyball.ui.theme.ToonSlotColors
import com.lauckyapp.luckyball.utils.PlinkoEngine

@Composable
fun GameResultBanner(
    result: SlotResult?,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    if (result == null) return
    val slotColor = ToonSlotColors.getOrElse(result.slot) { SurfaceContainer }
    val isWin = result.multiplier >= 1f
    val multText = PlinkoEngine.formatMultiplier(result.multiplier)

    val message = if (isWin) stringResource(R.string.win_message) else stringResource(R.string.try_again_message)
    val coins = if (result.winAmount > 0) {
        stringResource(R.string.win_coins_format, result.winAmount)
    } else {
        stringResource(R.string.zero_coins)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(slotColor.copy(alpha = if (compact) 0.92f else 0.25f), ShapePill)
            .border(if (compact) 2.dp else 3.dp, Outline, ShapePill)
            .padding(
                horizontal = if (compact) 14.dp else 12.dp,
                vertical = if (compact) 6.dp else 12.dp,
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!compact) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Outline,
            )
        }
        Text(
            text = if (compact) "$message · $multText" else multText,
            style = if (compact) MaterialTheme.typography.labelLarge else MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = if (compact) OnPrimary else Outline,
        )
        Text(
            text = coins,
            style = if (compact) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (isWin) {
                if (compact) OnPrimary else GrassGreen
            } else {
                if (compact) OnPrimary.copy(alpha = 0.85f) else Tertiary
            },
        )
    }
}
