package com.lauckyapp.luckyball.viewmodel

import androidx.lifecycle.ViewModel
import com.lauckyapp.luckyball.data.models.WinRecord
import com.lauckyapp.luckyball.utils.PlinkoEngine

data class HistoryStats(
    val totalGames: Int,
    val bestMultiplierLabel: String,
    val totalWon: Int,
)

class HistoryViewModel : ViewModel() {

    fun computeStats(records: List<WinRecord>): HistoryStats {
        if (records.isEmpty()) {
            return HistoryStats(
                totalGames = 0,
                bestMultiplierLabel = PlinkoEngine.formatMultiplier(0f),
                totalWon = 0,
            )
        }
        val bestMultiplier = records.maxOf { it.multiplierFloat() }
        return HistoryStats(
            totalGames = records.size,
            bestMultiplierLabel = PlinkoEngine.formatMultiplier(bestMultiplier),
            totalWon = records.sumOf { it.amount },
        )
    }
}
