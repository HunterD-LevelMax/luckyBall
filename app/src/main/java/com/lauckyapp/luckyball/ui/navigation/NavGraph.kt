package com.lauckyapp.luckyball.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lauckyapp.luckyball.ui.components.GameBettingPanel
import com.lauckyapp.luckyball.ui.components.LuckyBallBottomNav
import com.lauckyapp.luckyball.ui.components.LuckyBallTopBar
import com.lauckyapp.luckyball.ui.screens.GameScreen
import com.lauckyapp.luckyball.ui.screens.HistoryScreen
import com.lauckyapp.luckyball.ui.screens.SplashScreen
import com.lauckyapp.luckyball.ui.screens.StoreScreen
import com.lauckyapp.luckyball.ui.theme.SplashTheme
import com.lauckyapp.luckyball.ui.theme.Surface
import com.lauckyapp.luckyball.viewmodel.GameViewModel
import com.lauckyapp.luckyball.viewmodel.HistoryViewModel
import com.lauckyapp.luckyball.viewmodel.SharedMainViewModel
import com.lauckyapp.luckyball.viewmodel.StoreViewModel

object Routes {
    const val SPLASH = "splash"
    const val GAME = "game"
    const val STORE = "store"
    const val HISTORY = "history"
}

enum class MainTab(val route: String, val label: String) {
    GAME(Routes.GAME, "Play"),
    HISTORY(Routes.HISTORY, "Stats"),
    STORE(Routes.STORE, "Store"),
}

@Composable
fun AppNavGraph(
    sharedViewModel: SharedMainViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val sharedState by sharedViewModel.state.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.GAME) }

    val gameViewModel: GameViewModel = viewModel()
    val storeViewModel: StoreViewModel = viewModel()
    val historyViewModel: HistoryViewModel = viewModel()

    gameViewModel.bindShared(sharedViewModel)

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = modifier,
    ) {
        composable(
            route = Routes.SPLASH,
            exitTransition = { fadeOut(animationSpec = tween(SplashTheme.FADE_OUT_MS.toInt())) },
            popExitTransition = { fadeOut(animationSpec = tween(SplashTheme.FADE_OUT_MS.toInt())) },
        ) {
            SplashScreen(
                isDataLoaded = sharedState.isLoaded,
                onFinished = {
                    navController.navigate(Routes.GAME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = Routes.GAME,
            enterTransition = { fadeIn(animationSpec = tween(SplashTheme.FADE_OUT_MS.toInt())) },
        ) {
            MainScaffold(
                balance = sharedState.balance,
                selectedTab = selectedTab,
                onTabSelected = { tab -> navigateTab(navController, tab) { selectedTab = it } },
                gameBottomBar = {
                    GameBettingPanel(
                        betAmount = gameViewModel.betAmount,
                        balance = sharedState.balance,
                        isDropping = gameViewModel.isDropping,
                        onIncreaseBet = gameViewModel::increaseBet,
                        onDecreaseBet = gameViewModel::decreaseBet,
                        onDropBall = { gameViewModel.dropBall() },
                    )
                },
            ) { innerPadding ->
                GameScreen(
                    sharedViewModel = sharedViewModel,
                    gameViewModel = gameViewModel,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }

        composable(
            route = Routes.STORE,
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(300)) },
        ) {
            MainScaffold(
                balance = sharedState.balance,
                selectedTab = selectedTab,
                onTabSelected = { tab -> navigateTab(navController, tab) { selectedTab = it } },
            ) { innerPadding ->
                StoreScreen(
                    sharedViewModel = sharedViewModel,
                    storeViewModel = storeViewModel,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }

        composable(
            route = Routes.HISTORY,
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(300)) },
        ) {
            MainScaffold(
                balance = sharedState.balance,
                selectedTab = selectedTab,
                onTabSelected = { tab -> navigateTab(navController, tab) { selectedTab = it } },
            ) { innerPadding ->
                HistoryScreen(
                    sharedViewModel = sharedViewModel,
                    historyViewModel = historyViewModel,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

private fun navigateTab(
    navController: NavHostController,
    tab: MainTab,
    onSelected: (MainTab) -> Unit,
) {
    onSelected(tab)
    navController.navigate(tab.route) {
        popUpTo(Routes.GAME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun MainScaffold(
    balance: Int,
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    gameBottomBar: (@Composable () -> Unit)? = null,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Surface,
        topBar = { LuckyBallTopBar(balance = balance) },
        bottomBar = {
            Column {
                gameBottomBar?.invoke()
                LuckyBallBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = onTabSelected,
                )
            }
        },
        content = content,
    )
}
