package com.lauckyapp.luckyball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lauckyapp.luckyball.data.models.SkinCatalog
import com.lauckyapp.luckyball.ui.components.LuckyBallPlinkoBoard
import com.lauckyapp.luckyball.ui.theme.BoardGrass
import com.lauckyapp.luckyball.ui.theme.BoardGrassDark
import com.lauckyapp.luckyball.ui.theme.BoardSkyMid
import com.lauckyapp.luckyball.ui.theme.BoardSkyTop
import com.lauckyapp.luckyball.viewmodel.GameViewModel
import com.lauckyapp.luckyball.viewmodel.SharedMainViewModel

@Composable
fun GameScreen(
    sharedViewModel: SharedMainViewModel,
    gameViewModel: GameViewModel,
    modifier: Modifier = Modifier,
) {
    val sharedState by sharedViewModel.state.collectAsStateWithLifecycle()
    val gameUiState by gameViewModel.uiState.collectAsStateWithLifecycle()
    val selectedSkin = SkinCatalog.byId(sharedState.selectedSkin)

    DisposableEffect(Unit) {
        gameViewModel.setScreenActive(true)
        onDispose { gameViewModel.setScreenActive(false) }
    }

    LaunchedEffect(sharedState.balance) {
        gameViewModel.syncBetWithBalance(sharedState.balance)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(BoardSkyTop, BoardSkyMid, BoardGrass, BoardGrassDark),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            LuckyBallPlinkoBoard(
                pegs = gameViewModel.engine.pegs,
                balls = gameViewModel.engine.balls,
                pegHighlights = gameViewModel.engine.pegHighlights,
                frameTick = gameUiState.frameTick,
                ballDrawableRes = selectedSkin.drawableRes,
                canDrop = !gameViewModel.isDropping && !gameViewModel.isAutoSpinActive,
                onTapDrop = { x -> gameViewModel.dropBall(x) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }
    }
}
