package com.lauckyapp.luckyball.data.models

import kotlinx.serialization.Serializable

@Serializable
data class WinRecord(
    val timestamp: Long,
    val multiplier: Int,
    val amount: Int,
    val skinId: String,
)
