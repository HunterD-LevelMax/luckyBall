package com.lauckyapp.luckyball

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lauckyapp.luckyball.ui.navigation.AppNavGraph
import com.lauckyapp.luckyball.ui.theme.PlinkoBallsTheme
import com.lauckyapp.luckyball.viewmodel.SharedMainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedViewModel: SharedMainViewModel = viewModel()
            PlinkoBallsTheme {
                AppNavGraph(
                    sharedViewModel = sharedViewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                )
            }
        }
    }
}
