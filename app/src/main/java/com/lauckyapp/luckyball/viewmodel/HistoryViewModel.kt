package com.lauckyapp.luckyball.viewmodel

import androidx.lifecycle.ViewModel
import com.lauckyapp.luckyball.data.models.WinRecord
import kotlinx.coroutines.flow.StateFlow

class HistoryViewModel : ViewModel() {

    fun records(sharedState: StateFlow<SharedUiState>): List<WinRecord> =
        sharedState.value.history
}
