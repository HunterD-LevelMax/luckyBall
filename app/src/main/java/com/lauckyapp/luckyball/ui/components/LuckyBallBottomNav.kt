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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lauckyapp.luckyball.ui.navigation.MainTab
import com.lauckyapp.luckyball.ui.theme.OnPrimaryContainer
import com.lauckyapp.luckyball.ui.theme.OnSurfaceVariant
import com.lauckyapp.luckyball.ui.theme.Outline
import com.lauckyapp.luckyball.ui.theme.PrimaryContainer
import com.lauckyapp.luckyball.ui.theme.SurfaceContainer

@Composable
fun LuckyBallBottomNav(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Outline),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceContainer)
                .border(width = 4.dp, color = Outline)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
        ) {
            MainTab.entries.forEach { tab ->
                LuckyNavItem(
                    tab = tab,
                    selected = tab == selectedTab,
                    onClick = { onTabSelected(tab) },
                )
            }
        }
    }
}

@Composable
private fun LuckyNavItem(
    tab: MainTab,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val icon = when (tab) {
        MainTab.GAME -> Icons.Filled.SportsEsports
        MainTab.HISTORY -> Icons.Filled.Leaderboard
        MainTab.STORE -> Icons.Filled.ShoppingBag
    }
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = Modifier.offset(y = if (selected) (-4).dp else 0.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = 4.dp)
                    .clip(shape)
                    .background(Outline),
            )
        }
        Column(
            modifier = Modifier
                .clip(shape)
                .background(if (selected) PrimaryContainer else SurfaceContainer)
                .then(if (selected) Modifier.border(2.dp, Outline, shape) else Modifier)
                .clickable(onClick = onClick)
                .padding(horizontal = 22.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = tab.label,
                tint = if (selected) OnPrimaryContainer else OnSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = tab.label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (selected) OnPrimaryContainer else OnSurfaceVariant,
            )
        }
    }
}
