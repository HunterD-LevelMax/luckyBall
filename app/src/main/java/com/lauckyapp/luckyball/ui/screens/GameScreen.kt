package com.lauckyapp.luckyball.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lauckyapp.luckyball.data.models.SkinCatalog
import com.lauckyapp.luckyball.ui.components.CheeringCrowd
import com.lauckyapp.luckyball.ui.components.GameResultBanner
import com.lauckyapp.luckyball.ui.components.LuckyBallPlinkoBoard
import com.lauckyapp.luckyball.ui.components.SkyDecorations
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
        SkyDecorations(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                LuckyBallPlinkoBoard(
                    pegs = gameViewModel.engine.pegs,
                    balls = gameViewModel.engine.balls,
                    pegHighlights = gameViewModel.engine.pegHighlights,
                    frameTick = gameUiState.frameTick,
                    ballDrawableRes = selectedSkin.drawableRes,
                    canDrop = !gameViewModel.isDropping,
                    onTapDrop = { x -> gameViewModel.dropBall(x) },
                    modifier = Modifier.fillMaxWidth(),
                )
                CheeringCrowd(modifier = Modifier.align(Alignment.BottomStart))
            }

            AnimatedVisibility(
                visible = gameViewModel.lastResult != null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                GameResultBanner(result = gameViewModel.lastResult)
            }
        }
    }
}
