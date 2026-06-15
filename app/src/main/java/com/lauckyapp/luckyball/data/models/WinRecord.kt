package com.lauckyapp.luckyball.data.models

import kotlinx.serialization.Serializable

@Serializable
data class WinRecord(
    val timestamp: Long,
    val multiplier: Int,
    val amount: Int,
    val skinId: String,
    val betAmount: Int = 0,
) {
    fun effectiveBet(): Int {
        if (betAmount > 0) return betAmount
        if (multiplier <= 0) return amount
        return (amount * 10 + multiplier / 2) / multiplier
    }

    fun profit(): Int = amount - effectiveBet()

    fun multiplierFloat(): Float = multiplier / 10f
}
