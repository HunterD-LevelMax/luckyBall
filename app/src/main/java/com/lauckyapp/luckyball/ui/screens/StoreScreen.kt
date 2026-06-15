package com.lauckyapp.luckyball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lauckyapp.luckyball.R
import com.lauckyapp.luckyball.ui.components.NeubrutalBallPreview
import com.lauckyapp.luckyball.ui.components.NeubrutalDefaults
import com.lauckyapp.luckyball.ui.components.NeubrutalSurface
import com.lauckyapp.luckyball.ui.theme.GrassGreen
import com.lauckyapp.luckyball.ui.theme.OnSurfaceVariant
import com.lauckyapp.luckyball.ui.theme.Outline
import com.lauckyapp.luckyball.ui.theme.Primary
import com.lauckyapp.luckyball.ui.theme.PrimaryContainer
import com.lauckyapp.luckyball.ui.theme.SecondaryContainer
import com.lauckyapp.luckyball.ui.theme.ShapeDefault
import com.lauckyapp.luckyball.ui.theme.ShapeMd
import com.lauckyapp.luckyball.ui.theme.ShapeSm
import com.lauckyapp.luckyball.ui.theme.SkyBlue
import com.lauckyapp.luckyball.ui.theme.Surface
import com.lauckyapp.luckyball.ui.theme.SurfaceContainer
import com.lauckyapp.luckyball.ui.theme.TertiaryContainer
import com.lauckyapp.luckyball.viewmodel.SharedMainViewModel
import com.lauckyapp.luckyball.viewmodel.SkinItemUi
import com.lauckyapp.luckyball.viewmodel.SkinStatus
import com.lauckyapp.luckyball.viewmodel.StoreViewModel

private val SkinCardHeight = 196.dp
private val SkinActionHeight = 40.dp
private val SkinBallSize = 72.dp

@Composable
fun StoreScreen(
    sharedViewModel: SharedMainViewModel,
    storeViewModel: StoreViewModel,
    modifier: Modifier = Modifier,
) {
    val sharedState by sharedViewModel.state.collectAsStateWithLifecycle()
    val items = remember(sharedState) { storeViewModel.skinItems(sharedState) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.store_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Primary,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        BalanceBadge(balance = sharedState.balance)

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .padding(top = 16.dp),
            contentPadding = PaddingValues(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(items, key = { it.skin.id }) { item ->
                SkinCard(
                    item = item,
                    onBuy = { sharedViewModel.buySkin(item.skin.id) },
                    onSelect = { sharedViewModel.selectSkin(item.skin.id) },
                )
            }
        }
    }
}

@Composable
private fun BalanceBadge(balance: Int) {
    NeubrutalSurface(
        backgroundColor = PrimaryContainer,
        shape = ShapeSm,
        shadowOffset = 3.dp,
        borderWidth = 2.dp,
    ) {
        Text(
            text = stringResource(R.string.store_balance, balance),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Outline,
        )
    }
}

@Composable
private fun SkinCard(
    item: SkinItemUi,
    onBuy: () -> Unit,
    onSelect: () -> Unit,
) {
    val skin = item.skin
    val skinName = stringResource(skin.nameRes)
    val isSelected = item.status == SkinStatus.SELECTED
    val accentColor = skinAccentColor(skin.id)

    NeubrutalSurface(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = accentColor,
        shape = ShapeMd,
        borderWidth = if (isSelected) 4.dp else NeubrutalDefaults.Border,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(SkinCardHeight)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            NeubrutalBallPreview(
                drawableRes = skin.drawableRes,
                contentDescription = skinName,
                size = SkinBallSize,
            )
            Text(
                text = skinName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Outline,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            SkinCardAction(
                item = item,
                onBuy = onBuy,
                onSelect = onSelect,
            )
        }
    }
}

@Composable
private fun SkinCardAction(
    item: SkinItemUi,
    onBuy: () -> Unit,
    onSelect: () -> Unit,
) {
    val skin = item.skin
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(SkinActionHeight),
        contentAlignment = Alignment.Center,
    ) {
        when (item.status) {
            SkinStatus.SELECTED -> SkinActionButton(
                text = stringResource(R.string.skin_status_selected),
                backgroundColor = GrassGreen,
                enabled = false,
            )
            SkinStatus.OWNED -> SkinActionButton(
                text = stringResource(R.string.skin_select),
                backgroundColor = Surface,
                onClick = onSelect,
            )
            SkinStatus.AVAILABLE -> {
                if (skin.price == 0) {
                    SkinActionButton(
                        text = stringResource(R.string.skin_free),
                        backgroundColor = PrimaryContainer,
                        onClick = onSelect,
                    )
                } else {
                    SkinActionButton(
                        text = stringResource(R.string.skin_buy_format, skin.price),
                        backgroundColor = Primary,
                        textColor = Color.White,
                        onClick = onBuy,
                    )
                }
            }
            SkinStatus.LOCKED -> SkinActionButton(
                text = stringResource(R.string.skin_buy_format, skin.price),
                backgroundColor = OnSurfaceVariant.copy(alpha = 0.25f),
                enabled = false,
            )
        }
    }
}

@Composable
private fun SkinActionButton(
    text: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    textColor: Color = Outline,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    val shape = ShapeDefault
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(SkinActionHeight),
    ) {
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
                .fillMaxWidth()
                .height(SkinActionHeight)
                .clip(shape)
                .background(backgroundColor)
                .border(2.dp, Outline, shape)
                .then(
                    if (enabled) {
                        Modifier.clickable(onClick = onClick)
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun skinAccentColor(skinId: String): Color = when (skinId) {
    "default" -> SurfaceContainer
    "red" -> Color(0xFFFFE8CC)
    "blue" -> SkyBlue
    "gold" -> PrimaryContainer
    "rainbow" -> SecondaryContainer
    "pink" -> TertiaryContainer
    else -> SurfaceContainer
}
