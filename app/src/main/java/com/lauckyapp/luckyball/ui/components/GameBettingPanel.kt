package com.lauckyapp.luckyball.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.lauckyapp.luckyball.ui.theme.BrightOrange
import com.lauckyapp.luckyball.ui.theme.GrassGreen
import com.lauckyapp.luckyball.ui.theme.OnPrimary
import com.lauckyapp.luckyball.ui.theme.OnSurfaceVariant
import com.lauckyapp.luckyball.ui.theme.Outline
import com.lauckyapp.luckyball.ui.theme.Primary
import com.lauckyapp.luckyball.ui.theme.ShapePill
import com.lauckyapp.luckyball.ui.theme.SurfaceContainer

/** Панель ставки + DROP! как в макете (на зелёной «траве»). */
@Composable
fun GameBettingPanel(
    betAmount: Int,
    balance: Int,
    isDropping: Boolean,
    onIncreaseBet: () -> Unit,
    onDecreaseBet: () -> Unit,
    onDropBall: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(GrassGreen)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier
                .clip(ShapePill)
                .background(SurfaceContainer)
                .border(4.dp, Outline, ShapePill)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            BetCircleButton(
                text = "−",
                background = Color(0xFFBA1A1A),
                enabled = !isDropping && betAmount > 1,
                onClick = onDecreaseBet,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "CURRENT BET",
                    style = MaterialTheme.typography.labelLarge,
                    color = OnSurfaceVariant,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "$betAmount",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Primary,
                    fontWeight = FontWeight.Bold,
                )
            }
            BetCircleButton(
                text = "+",
                background = GrassGreen,
                enabled = !isDropping && betAmount < balance,
                onClick = onIncreaseBet,
            )
        }

        DropButton(
            enabled = !isDropping && balance >= betAmount,
            isDropping = isDropping,
            onClick = onDropBall,
        )
    }
}

@Composable
private fun DropButton(
    enabled: Boolean,
    isDropping: Boolean,
    onClick: () -> Unit,
) {
    Box(contentAlignment = Alignment.Center) {
        listOf(
            Modifier.offset((-16).dp, (-16).dp) to 45f,
            Modifier.offset(16.dp, (-16).dp) to -45f,
            Modifier.offset((-16).dp, 16.dp) to -45f,
            Modifier.offset(16.dp, 16.dp) to 45f,
        ).forEach { (offsetMod, rotation) ->
            Box(
                modifier = Modifier
                    .then(offsetMod)
                    .size(width = 16.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Outline),
            )
        }

        Box(
            modifier = Modifier
                .offset(y = 6.dp)
                .fillMaxWidth(0.75f)
                .height(56.dp)
                .clip(ShapePill)
                .background(Outline),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(56.dp)
                .clip(ShapePill)
                .background(if (enabled) BrightOrange else OnSurfaceVariant.copy(alpha = 0.5f))
                .border(4.dp, Outline, ShapePill)
                .clickable(enabled = enabled, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (isDropping) "…" else "DROP!",
                style = MaterialTheme.typography.headlineMedium,
                color = OnPrimary,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun BetCircleButton(
    text: String,
    background: androidx.compose.ui.graphics.Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(background.copy(alpha = if (enabled) 1f else 0.4f))
            .border(4.dp, Outline, CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = OnPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp)
    }
}
