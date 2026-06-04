package com.lauckyapp.luckyball.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lauckyapp.luckyball.data.models.SkinCatalog
import com.lauckyapp.luckyball.data.models.WinRecord
import com.lauckyapp.luckyball.ui.theme.BrightOrange
import com.lauckyapp.luckyball.ui.theme.Primary
import com.lauckyapp.luckyball.ui.theme.Outline
import com.lauckyapp.luckyball.ui.theme.ShapeDefault
import com.lauckyapp.luckyball.ui.theme.Surface
import com.lauckyapp.luckyball.ui.theme.SurfaceContainer
import com.lauckyapp.luckyball.viewmodel.HistoryViewModel
import com.lauckyapp.luckyball.viewmodel.SharedMainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    sharedViewModel: SharedMainViewModel,
    @Suppress("UNUSED_PARAMETER") historyViewModel: HistoryViewModel,
    modifier: Modifier = Modifier,
) {
    val sharedState by sharedViewModel.state.collectAsStateWithLifecycle()
    val records = sharedState.history
    var showClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Stats",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Primary,
            )
            Button(
                onClick = { showClearDialog = true },
                enabled = records.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = BrightOrange),
                shape = ShapeDefault,
            ) {
                Text("Очистить", color = Outline)
            }
        }

        if (records.isEmpty()) {
            Text(
                text = "Пока нет выигрышей. Кидайте шарики!",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 32.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(records, key = { it.timestamp }) { record ->
                    HistoryItem(record = record)
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Очистить историю?") },
            text = { Text("Все записи будут удалены без возможности восстановления.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        sharedViewModel.clearHistory()
                        showClearDialog = false
                    },
                ) {
                    Text("Да")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Отмена")
                }
            },
        )
    }
}

@Composable
private fun HistoryItem(record: WinRecord) {
    val skin = SkinCatalog.byId(record.skinId)
    val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(record.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainer, ShapeDefault)
            .border(2.dp, Outline, ShapeDefault)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(
            painter = painterResource(skin.drawableRes),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = time, style = MaterialTheme.typography.bodyMedium)
            val multValue = record.multiplier / 10f
            val multLabel = if (multValue < 1f) {
                if (multValue == 0f) ".0x" else ".${record.multiplier}x"
            } else {
                "x${multValue.toInt()}"
            }
            Text(
                text = "$multLabel · +${record.amount} монет",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
