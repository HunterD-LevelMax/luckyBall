package com.lauckyapp.luckyball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lauckyapp.luckyball.R
import com.lauckyapp.luckyball.data.models.SkinCatalog
import com.lauckyapp.luckyball.data.models.WinRecord
import com.lauckyapp.luckyball.ui.components.NeubrutalBallPreview
import com.lauckyapp.luckyball.ui.components.NeubrutalDefaults
import com.lauckyapp.luckyball.ui.components.NeubrutalSurface
import com.lauckyapp.luckyball.ui.theme.BrightOrange
import com.lauckyapp.luckyball.ui.theme.OnSurfaceVariant
import com.lauckyapp.luckyball.ui.theme.Outline
import com.lauckyapp.luckyball.ui.theme.OutlineVariant
import com.lauckyapp.luckyball.ui.theme.Primary
import com.lauckyapp.luckyball.ui.theme.PrimaryContainer
import com.lauckyapp.luckyball.ui.theme.SecondaryContainer
import com.lauckyapp.luckyball.ui.theme.ShapeDefault
import com.lauckyapp.luckyball.ui.theme.ShapeMd
import com.lauckyapp.luckyball.ui.theme.ShapeSm
import com.lauckyapp.luckyball.ui.theme.Surface
import com.lauckyapp.luckyball.ui.theme.SurfaceContainer
import com.lauckyapp.luckyball.ui.theme.TertiaryContainer
import com.lauckyapp.luckyball.utils.PlinkoEngine
import com.lauckyapp.luckyball.viewmodel.HistoryStats
import com.lauckyapp.luckyball.viewmodel.HistoryViewModel
import com.lauckyapp.luckyball.viewmodel.SharedMainViewModel
import kotlin.math.abs

private val HistoryCardHeight = 92.dp
private val StatCardWidth = 118.dp
private val StatCardHeight = 96.dp
private val BigWinMultiplier = 5f

@Composable
fun HistoryScreen(
    sharedViewModel: SharedMainViewModel,
    historyViewModel: HistoryViewModel,
    modifier: Modifier = Modifier,
) {
    val sharedState by sharedViewModel.state.collectAsStateWithLifecycle()
    val records = sharedState.history
    val stats = remember(records) { historyViewModel.computeStats(records) }
    var showClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
            .padding(16.dp),
    ) {
        HistoryHeader(
            onClearClick = { showClearDialog = true },
            clearEnabled = records.isNotEmpty(),
        )

        if (records.isEmpty()) {
            Text(
                text = stringResource(R.string.stats_empty),
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(top = 32.dp),
            )
        } else {
            StatsSummaryRow(stats = stats)
            LazyColumn(
                modifier = Modifier.padding(top = 16.dp),
                contentPadding = PaddingValues(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(records, key = { it.timestamp }) { record ->
                    HistoryCard(record = record)
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(R.string.stats_clear_title)) },
            text = { Text(stringResource(R.string.stats_clear_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        sharedViewModel.clearHistory()
                        showClearDialog = false
                    },
                ) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(stringResource(R.string.no))
                }
            },
        )
    }
}

@Composable
private fun HistoryHeader(
    onClearClick: () -> Unit,
    clearEnabled: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Primary,
            )
            Text(
                text = stringResource(R.string.stats_subtitle),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceVariant,
            )
        }
        HistoryClearButton(
            enabled = clearEnabled,
            onClick = onClearClick,
        )
    }
}

@Composable
private fun HistoryClearButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val shape = ShapeSm
    Box {
        if (enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 3.dp, y = 3.dp)
                    .clip(shape)
                    .background(Outline),
            )
        }
        Box(
            modifier = Modifier
                .clip(shape)
                .background(if (enabled) BrightOrange else OutlineVariant)
                .border(2.dp, Outline, shape)
                .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(horizontal = 14.dp, vertical = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.stats_clear),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Outline,
            )
        }
    }
}

@Composable
private fun StatsSummaryRow(
    stats: HistoryStats,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(end = 4.dp),
    ) {
        item {
            StatSummaryCard(
                label = stringResource(R.string.stats_total_label),
                value = stats.totalGames.toString(),
                sublabel = stringResource(R.string.stats_total_sub),
                backgroundColor = PrimaryContainer,
            )
        }
        item {
            StatSummaryCard(
                label = stringResource(R.string.stats_best_label),
                value = stats.bestMultiplierLabel,
                sublabel = stringResource(R.string.stats_best_sub),
                backgroundColor = TertiaryContainer,
            )
        }
        item {
            StatSummaryCard(
                label = stringResource(R.string.stats_won_label),
                value = stringResource(R.string.balance_format, stats.totalWon),
                sublabel = stringResource(R.string.stats_won_sub),
                backgroundColor = SecondaryContainer,
            )
        }
    }
}

@Composable
private fun StatSummaryCard(
    label: String,
    value: String,
    sublabel: String,
    backgroundColor: Color,
) {
    NeubrutalSurface(
        modifier = Modifier
            .width(StatCardWidth)
            .height(StatCardHeight + NeubrutalDefaults.Shadow),
        backgroundColor = backgroundColor,
        shape = ShapeMd,
    ) {
        Column(
            modifier = Modifier
                .width(StatCardWidth)
                .height(StatCardHeight)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Outline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = sublabel,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Primary,
            )
        }
    }
}

@Composable
private fun HistoryCard(record: WinRecord) {
    val skin = SkinCatalog.byId(record.skinId)
    val profit = record.profit()
    val isLoss = profit < 0
    val isBigWin = !isLoss && record.multiplierFloat() >= BigWinMultiplier
    val multLabel = PlinkoEngine.formatMultiplier(record.multiplierFloat())
    val timeAgo = formatTimeAgo(record.timestamp)
    val title = if (isBigWin) {
        stringResource(R.string.stats_big_win)
    } else {
        stringResource(R.string.history_game_title)
    }
    val cardColor = when {
        isLoss -> Color(0xFFE8E4DC)
        isBigWin -> PrimaryContainer
        else -> SurfaceContainer
    }
    val textColor = if (isLoss) OnSurfaceVariant.copy(alpha = 0.55f) else Outline
    val profitColor = when {
        isLoss -> OnSurfaceVariant.copy(alpha = 0.55f)
        profit > 0 -> Primary
        else -> OnSurfaceVariant
    }
    val profitText = if (profit >= 0) {
        stringResource(R.string.stats_profit_positive, profit)
    } else {
        stringResource(R.string.stats_profit_negative, abs(profit))
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        NeubrutalSurface(
            backgroundColor = cardColor,
            shape = ShapeMd,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HistoryCardHeight)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                NeubrutalBallPreview(
                    drawableRes = skin.drawableRes,
                    contentDescription = null,
                    size = 52.dp,
                    enabled = !isLoss,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = timeAgo,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isLoss) OnSurfaceVariant.copy(alpha = 0.45f) else OnSurfaceVariant,
                    )
                    Text(
                        text = stringResource(R.string.stats_bet_format, record.effectiveBet()),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = multLabel,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor,
                    )
                    Text(
                        text = profitText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = profitColor,
                    )
                }
            }
        }
        if (isBigWin) {
            HotBadge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = 8.dp),
            )
        }
    }
}

@Composable
private fun HotBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .rotate(12f)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFE53935))
            .border(2.dp, Outline, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(
            text = stringResource(R.string.stats_hot),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 10.sp,
            ),
            color = Color.White,
        )
    }
}

@Composable
private fun formatTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = (now - timestamp).coerceAtLeast(0L)
    val minutes = diff / 60_000L
    val hours = diff / 3_600_000L
    val days = diff / 86_400_000L
    return when {
        minutes < 1 -> stringResource(R.string.time_just_now)
        minutes < 60 -> stringResource(R.string.time_minutes_ago, minutes.toInt())
        hours < 24 -> stringResource(R.string.time_hours_ago, hours.toInt())
        else -> stringResource(R.string.time_days_ago, days.toInt())
    }
}
