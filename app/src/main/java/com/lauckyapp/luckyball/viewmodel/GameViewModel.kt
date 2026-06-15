package com.lauckyapp.luckyball.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lauckyapp.luckyball.data.models.SlotResult
import com.lauckyapp.luckyball.data.models.WinRecord
import com.lauckyapp.luckyball.utils.PlinkoEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameUiState(
    val frameTick: Long = 0L,
)

class GameViewModel : ViewModel() {

    companion object {
        private const val AUTOSPIN_DELAY_MS = 600L
        val QUICK_BETS = listOf(1, 5, 10, 50, 100)
        val AUTOSPIN_OPTIONS = listOf(5, 10, 20, -1)
    }

    val engine = PlinkoEngine()

    var betAmount by mutableIntStateOf(10)
        private set

    var isDropping by mutableStateOf(false)
        private set

    var lastResult by mutableStateOf<SlotResult?>(null)
        private set

    var isAutoSpinActive by mutableStateOf(false)
        private set

    var autoSpinRemaining by mutableIntStateOf(0)
        private set

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var simulationJob: Job? = null
    private var autoSpinJob: Job? = null
    private var sharedMainViewModel: SharedMainViewModel? = null
    private var isScreenActive = true

    fun bindShared(shared: SharedMainViewModel) {
        sharedMainViewModel = shared
    }

    fun setScreenActive(active: Boolean) {
        isScreenActive = active
        if (!active) {
            simulationJob?.cancel()
            stopAutoSpin()
        } else if (isDropping) {
            startSimulationLoop()
        }
    }

    fun syncBetWithBalance(balance: Int) {
        if (betAmount > balance) {
            betAmount = balance.coerceAtLeast(1)
        }
        if (balance < betAmount && isAutoSpinActive) {
            stopAutoSpin()
        }
    }

    fun setBet(amount: Int) {
        if (!isDropping && !isAutoSpinActive) {
            val balance = sharedMainViewModel?.state?.value?.balance ?: 0
            betAmount = amount.coerceIn(1, balance.coerceAtLeast(1))
        }
    }

    fun increaseBet() {
        if (!isDropping && !isAutoSpinActive) {
            val balance = sharedMainViewModel?.state?.value?.balance ?: 0
            betAmount = (betAmount * 2).coerceAtMost(balance.coerceAtLeast(1))
        }
    }

    fun decreaseBet() {
        if (!isDropping && !isAutoSpinActive) {
            betAmount = (betAmount / 2).coerceAtLeast(1)
        }
    }

    fun dropBall(normalizedX: Float? = null) {
        val shared = sharedMainViewModel ?: return
        val balance = shared.state.value.balance
        if (isDropping || balance < betAmount) {
            if (isAutoSpinActive) stopAutoSpin()
            return
        }
        if (!shared.trySpend(betAmount)) {
            stopAutoSpin()
            return
        }

        isDropping = true
        lastResult = null
        engine.dropBall(betAmount, normalizedX) ?: run {
            isDropping = false
            shared.addMoney(betAmount)
            stopAutoSpin()
            return
        }
        startSimulationLoop()
    }

    fun startAutoSpin(count: Int) {
        if (isDropping) return
        isAutoSpinActive = true
        autoSpinRemaining = count
        if (!isDropping) {
            dropBall()
        }
    }

    fun stopAutoSpin() {
        isAutoSpinActive = false
        autoSpinRemaining = 0
        autoSpinJob?.cancel()
    }

    fun resetRound() {
        stopAutoSpin()
        simulationJob?.cancel()
        engine.clearBalls()
        isDropping = false
        lastResult = null
    }

    private fun startSimulationLoop() {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            while (isScreenActive && engine.updatePhysics()) {
                _uiState.update { it.copy(frameTick = it.frameTick + 1) }
                delay(PlinkoEngine.FRAME_DELAY_MS)
            }
            finishRound()
        }
    }

    private fun finishRound() {
        val finishedBalls = engine.balls.filter { !it.active && it.finalSlot >= 0 }
        finishedBalls.forEach { ball ->
            val (slotIndex, multiplier) = engine.resolveWin(ball)
            val winAmount = (ball.betAmount * multiplier).toInt()
            val skinId = sharedMainViewModel?.state?.value?.selectedSkin ?: "default"

            if (winAmount > 0) {
                sharedMainViewModel?.addMoney(winAmount)
            }

            lastResult = SlotResult(
                slot = slotIndex,
                multiplier = multiplier,
                winAmount = winAmount,
            )

            sharedMainViewModel?.addWinRecord(
                WinRecord(
                    timestamp = System.currentTimeMillis(),
                    multiplier = (multiplier * 10).toInt(),
                    amount = winAmount,
                    skinId = skinId,
                    betAmount = ball.betAmount,
                ),
            )
        }

        engine.balls.removeAll(finishedBalls.toSet())
        isDropping = engine.hasActiveBall()
        _uiState.update { it.copy(frameTick = it.frameTick + 1) }

        if (isAutoSpinActive) {
            scheduleNextAutoSpin()
        }
    }

    private fun scheduleNextAutoSpin() {
        autoSpinJob?.cancel()
        autoSpinJob = viewModelScope.launch {
            delay(AUTOSPIN_DELAY_MS)
            if (!isAutoSpinActive || !isScreenActive) return@launch

            if (autoSpinRemaining > 0) {
                autoSpinRemaining -= 1
                if (autoSpinRemaining == 0) {
                    stopAutoSpin()
                    return@launch
                }
            }

            val balance = sharedMainViewModel?.state?.value?.balance ?: 0
            if (balance >= betAmount && !isDropping) {
                dropBall()
            } else {
                stopAutoSpin()
            }
        }
    }
}
