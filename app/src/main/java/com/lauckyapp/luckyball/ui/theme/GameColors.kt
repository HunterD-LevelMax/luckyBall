package com.lauckyapp.luckyball.ui.theme

import androidx.compose.ui.graphics.Color

/** Цвета игрового поля Lucky Ball (HTML / DESIGN.md). */
val BoardStone = Color(0xFFD1D5DB)
val BoardSkyTop = Color(0xFF9AE1FF)
val BoardSkyMid = Color(0xFFBAEAFF)
val BoardGrass = Color(0xFF91C788)
val BoardGrassDark = Color(0xFF4CAF50)
val PegToon = Color(0xFF4D4732)

/** 11 slots: .5, .5, 1, 2, 3, 5, 3, 2, 1, .5, .5 */
val ToonSlotColors = listOf(
    BrightOrange,
    BrightOrange,
    Primary,
    Secondary,
    Secondary,
    PrimaryContainer,
    Secondary,
    Secondary,
    Primary,
    BrightOrange,
    BrightOrange,
)
