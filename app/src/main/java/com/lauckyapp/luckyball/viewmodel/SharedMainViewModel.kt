package com.lauckyapp.luckyball.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lauckyapp.luckyball.data.models.SkinCatalog
import com.lauckyapp.luckyball.data.models.WinRecord
import com.lauckyapp.luckyball.data.repositories.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SharedUiState(
    val balance: Int = 500,
    val ownedSkins: Set<String> = setOf("default"),
    val selectedSkin: String = "default",
    val history: List<WinRecord> = emptyList(),
    val isLoaded: Boolean = false,
)

class SharedMainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application)

    private val _state = MutableStateFlow(SharedUiState())
    val state: StateFlow<SharedUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.gameState.collect { persisted ->
                _state.update {
                    it.copy(
                        balance = persisted.balance,
                        ownedSkins = persisted.ownedSkins,
                        selectedSkin = persisted.selectedSkin,
                        history = persisted.history,
                        isLoaded = true,
                    )
                }
            }
        }
    }

    private fun persist() {
        val current = _state.value
        viewModelScope.launch {
            repository.save(
                balance = current.balance,
                ownedSkins = current.ownedSkins,
                selectedSkin = current.selectedSkin,
                history = current.history,
            )
        }
    }

    fun addMoney(amount: Int) {
        if (amount <= 0) return
        _state.update { it.copy(balance = it.balance + amount) }
        persist()
    }

    fun trySpend(amount: Int): Boolean {
        if (amount <= 0 || _state.value.balance < amount) return false
        _state.update { it.copy(balance = it.balance - amount) }
        persist()
        return true
    }

    fun buySkin(skinId: String): Boolean {
        val skin = SkinCatalog.byId(skinId)
        if (skinId == "default" || skinId in _state.value.ownedSkins) return false
        val current = _state.value
        if (current.balance < skin.price) return false
        _state.update {
            it.copy(
                balance = it.balance - skin.price,
                ownedSkins = it.ownedSkins + skinId,
            )
        }
        persist()
        return true
    }

    fun selectSkin(skinId: String) {
        if (skinId !in _state.value.ownedSkins) return
        _state.update { it.copy(selectedSkin = skinId) }
        persist()
    }

    fun addWinRecord(record: WinRecord) {
        _state.update {
            it.copy(history = listOf(record) + it.history.take(49))
        }
        persist()
    }

    fun clearHistory() {
        _state.update { it.copy(history = emptyList()) }
        persist()
    }
}
