package com.lauckyapp.luckyball.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lauckyapp.luckyball.ui.theme.GrassGreen
import com.lauckyapp.luckyball.ui.theme.OnSurfaceVariant
import com.lauckyapp.luckyball.ui.theme.Outline
import com.lauckyapp.luckyball.ui.theme.Primary
import com.lauckyapp.luckyball.ui.theme.PrimaryContainer
import com.lauckyapp.luckyball.ui.theme.ShapeDefault
import com.lauckyapp.luckyball.ui.theme.Surface
import com.lauckyapp.luckyball.ui.theme.SurfaceContainer
import com.lauckyapp.luckyball.ui.theme.Tertiary
import com.lauckyapp.luckyball.viewmodel.SharedMainViewModel
import com.lauckyapp.luckyball.viewmodel.SkinItemUi
import com.lauckyapp.luckyball.viewmodel.SkinStatus
import com.lauckyapp.luckyball.viewmodel.StoreViewModel

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
            text = "Store",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Primary,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Text(
            text = "Balance: ${sharedState.balance}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(items, key = { it.skin.id }) { item ->
                SkinCard(
                    item = item,
                    balance = sharedState.balance,
                    onBuy = { sharedViewModel.buySkin(item.skin.id) },
                    onSelect = { sharedViewModel.selectSkin(item.skin.id) },
                )
            }
        }
    }
}

@Composable
private fun SkinCard(
    item: SkinItemUi,
    balance: Int,
    onBuy: () -> Unit,
    onSelect: () -> Unit,
) {
    val skin = item.skin
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(3.dp, Outline, ShapeDefault),
        shape = ShapeDefault,
        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Image(
                painter = painterResource(skin.drawableRes),
                contentDescription = skin.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, Outline, CircleShape),
            )
            Text(text = skin.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            when (item.status) {
                SkinStatus.SELECTED -> StatusChip("ВЫБРАН", PrimaryContainer)
                SkinStatus.OWNED -> {
                    StatusChip("КУПЛЕН", Color(0xFFBAEAFF))
                    Button(
                        onClick = onSelect,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GrassGreen),
                        shape = ShapeDefault,
                    ) {
                        Text("Выбрать", color = Outline)
                    }
                }
                SkinStatus.AVAILABLE -> {
                    if (skin.price == 0) {
                        Button(onClick = onSelect, modifier = Modifier.fillMaxWidth()) {
                            Text("Бесплатно")
                        }
                    } else {
                        Button(
                            onClick = onBuy,
                            enabled = balance >= skin.price,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            shape = ShapeDefault,
                        ) {
                            Text("КУПИТЬ ${skin.price} монет", color = Color.White)
                        }
                    }
                }
                SkinStatus.LOCKED -> {
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = OnSurfaceVariant.copy(alpha = 0.3f),
                        ),
                        shape = ShapeDefault,
                    ) {
                        Text("КУПИТЬ ${skin.price} монет")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(text: String, color: Color) {
    Text(
        text = text,
        modifier = Modifier
            .background(color, ShapeDefault)
            .border(2.dp, Outline, ShapeDefault)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelLarge,
        color = Outline,
    )
}
