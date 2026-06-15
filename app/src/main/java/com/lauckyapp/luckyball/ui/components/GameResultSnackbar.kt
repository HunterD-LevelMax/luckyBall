package com.lauckyapp.luckyball.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.lauckyapp.luckyball.ui.theme.OnSurface
import com.lauckyapp.luckyball.ui.theme.OnSurfaceVariant
import com.lauckyapp.luckyball.ui.theme.Outline
import com.lauckyapp.luckyball.ui.theme.PrimaryContainer
import com.lauckyapp.luckyball.ui.theme.Surface
import com.lauckyapp.luckyball.ui.theme.ToonSlotColors
import com.lauckyapp.luckyball.utils.PlinkoEngine

@Composable
fun GameResultSnackbar(
    result: SlotResult?,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = result != null,
        modifier = modifier.fillMaxWidth(),
        enter = slideInVertically(animationSpec = tween(220)) { it / 2 } + fadeIn(tween(220)),
        exit = slideOutVertically(animationSpec = tween(180)) { it / 2 } + fadeOut(tween(180)),
    ) {
        if (result != null) {
            val slotColor = ToonSlotColors.getOrElse(result.slot) { PrimaryContainer }
            val isWin = result.multiplier >= 1f
            val multText = PlinkoEngine.formatMultiplier(result.multiplier)
            val message = if (isWin) {
                stringResource(R.string.win_message)
            } else {
                stringResource(R.string.try_again_message)
            }
            val coins = if (result.winAmount > 0) {
                stringResource(R.string.win_coins_format, result.winAmount)
            } else {
                stringResource(R.string.zero_coins)
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                color = OnSurface,
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(2.dp, Outline),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f),
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = slotColor,
                        ) {
                            Text(
                                text = multText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Surface,
                            )
                        }
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Surface,
                        )
                    }
                    Text(
                        text = coins,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isWin -> GrassGreen
                            result.winAmount > 0 -> PrimaryContainer
                            else -> OnSurfaceVariant
                        },
                    )
                }
            }
        }
    }
}
