package com.lauckyapp.luckyball.data.models

data class PegPosition(val col: Int, val row: Int, val x: Float, val y: Float)

data class Ball(
    val id: Int,
    var x: Float,
    var y: Float,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var active: Boolean = true,
    var finalSlot: Int = -1,
    val betAmount: Int,
)

data class PegHighlight(
    val pegIndex: Int,
    val timestamp: Long = System.currentTimeMillis(),
)

data class SlotResult(
    val slot: Int,
    val multiplier: Float,
    val winAmount: Int,
    val timestamp: Long = System.currentTimeMillis(),
)
