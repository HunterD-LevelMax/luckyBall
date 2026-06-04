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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lauckyapp.luckyball.data.models.SlotResult
import com.lauckyapp.luckyball.ui.theme.GrassGreen
import com.lauckyapp.luckyball.ui.theme.Outline
import com.lauckyapp.luckyball.ui.theme.ShapeDefault
import com.lauckyapp.luckyball.ui.theme.SurfaceContainer
import com.lauckyapp.luckyball.ui.theme.Tertiary
import com.lauckyapp.luckyball.ui.theme.ToonSlotColors

@Composable
fun GameResultBanner(
    result: SlotResult?,
    modifier: Modifier = Modifier,
) {
    if (result == null) return
    val slotColor = ToonSlotColors.getOrElse(result.slot) { SurfaceContainer }
    val isWin = result.multiplier >= 1f
    val multText = if (result.multiplier < 1f) {
        ".${(result.multiplier * 10).toInt()}x"
    } else {
        "${result.multiplier.toInt()}x"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(slotColor.copy(alpha = 0.25f), ShapeDefault)
            .border(3.dp, Outline, ShapeDefault)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (isWin) "Выигрыш!" else "Попробуйте ещё",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Outline,
        )
        Text(
            text = multText,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Outline,
        )
        Text(
            text = if (result.winAmount > 0) "+${result.winAmount} 🪙" else "0 🪙",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (isWin) GrassGreen else Tertiary,
        )
    }
}
