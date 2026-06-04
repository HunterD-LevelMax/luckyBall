package com.lauckyapp.luckyball.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lauckyapp.luckyball.ui.theme.OnSurface
import com.lauckyapp.luckyball.ui.theme.Outline
import com.lauckyapp.luckyball.ui.theme.Primary
import com.lauckyapp.luckyball.ui.theme.Surface

/**
 * Верхняя панель как в lucky_ball_plinko_game_screen:
 * кошелёк + баланс слева, «LUCKY BALL» справа, кремовый фон, толстая обводка снизу.
 */
@Composable
fun LuckyBallTopBar(
    balance: Int,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .align(Alignment.BottomCenter)
                .offset(y = 4.dp)
                .background(Outline),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .border(width = 4.dp, color = Outline)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountBalanceWallet,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(26.dp),
                )
                Text(
                    text = "$balance",
                    style = MaterialTheme.typography.labelLarge,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            Text(
                text = "LUCKY BALL",
                style = MaterialTheme.typography.headlineMedium,
                color = Primary,
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Italic,
                letterSpacing = (-0.5).sp,
            )
        }
    }
}
