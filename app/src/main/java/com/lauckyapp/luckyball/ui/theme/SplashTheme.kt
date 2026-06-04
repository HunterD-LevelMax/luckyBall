package com.lauckyapp.luckyball.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object SplashTheme {
    const val MIN_DISPLAY_MS = 1500L
    const val FADE_OUT_MS = 450

    val backgroundGradient: Brush
        get() = Brush.verticalGradient(
            colors = listOf(PrimaryDark, Primary),
        )

    val titleColor: Color = OnPrimary
    val pegColor: Color = OnPrimary.copy(alpha = 0.4f)
    val pegHighlightColor: Color = PrimaryContainer.copy(alpha = 0.55f)
}
