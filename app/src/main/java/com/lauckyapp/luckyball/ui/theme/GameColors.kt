package com.lauckyapp.luckyball.ui.theme

import androidx.compose.ui.graphics.Color

/** Цвета игрового поля Lucky Ball (HTML / DESIGN.md). */
val BoardStone = Color(0xFFD1D5DB)
val BoardSkyTop = Color(0xFF9AE1FF)
val BoardSkyMid = Color(0xFFBAEAFF)
val BoardGrass = Color(0xFF91C788)
val BoardGrassDark = Color(0xFF4CAF50)
val PegToon = Color(0xFF4D4732)

/** 11 слотов: 0, 0, .2, .5, 1, 2, 1, .5, .2, 0, 0 */
val ToonSlotColors = listOf(
    Color(0xFFBA1A1A),
    Color(0xFFBA1A1A),
    BrightOrange,
    Primary,
    Secondary,
    PrimaryContainer,
    Secondary,
    Primary,
    BrightOrange,
    Color(0xFFBA1A1A),
    Color(0xFFBA1A1A),
)
