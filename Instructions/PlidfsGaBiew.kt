package com.euphoria.ballneonogame.gsde

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

const val KNOCK_SOUND_DEBOUNCE_MS = 50L

data class PegPosition(val col: Int, val row: Int, val x: Float, val y: Float)

data class Ball(
    val id: Int,
    var x: Float,
    var y: Float,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var active: Boolean = true,
    var finalSlot: Int = -1
)

data class PegHighlight(
    val pegIndex: Int,
    var intensity: Float = 1f,
    val timestamp: Long = System.currentTimeMillis()
)

data class SlotResult(
    val slot: Int,
    val multiplier: Float,
    val winAmount: Float,
    val timestamp: Long = System.currentTimeMillis()
)

class PlidfsGaBiew : ViewModel() {

    companion object {
        const val ROWS = 12
        const val COLS_BASE = 3
        val MULTIPLIERS = listOf(0f, 0f, 0.2f, 0.5f, 1f, 2f, 1f, 0.5f, 0.2f, 0f, 0f)
        const val GRAVITY = 0.0006f
        const val BALL_RADIUS = 0.018f
        const val PEG_RADIUS = 0.012f
        const val BOUNCE_DAMPING = 0.55f
        const val HORIZONTAL_SPREAD = 0.012f
        const val FRAME_DELAY_MS = 16L
        const val BOARD_TOP = 0.05f
        const val BOARD_BOTTOM = 0.88f
        const val BOARD_LEFT = 0.05f
        const val BOARD_RIGHT = 0.95f
        const val HIGHLIGHT_DURATION_MS = 400L
    }

    // Game state
    var balance by mutableStateOf(1000f)
        private set
    var betAmount by mutableStateOf(10f)
        private set
    var isDropping by mutableStateOf(false)
        private set
    var lastResult by mutableStateOf<SlotResult?>(null)
        private set

    val balls = mutableStateListOf<Ball>()
    val pegs = mutableStateListOf<PegPosition>()
    val recentResults = mutableStateListOf<SlotResult>()
    val pegHighlights = mutableStateListOf<PegHighlight>()

    private var ballIdCounter = 0
    private var simulationJob: Job? = null
    
    var onKnockSound: (() -> Unit)? = null
    var onGetMoneySound: (() -> Unit)? = null
    var onSelectValueSound: (() -> Unit)? = null
    
    private var lastKnockSoundTime = 0L

    init {
        generatePegs()
    }

    private fun generatePegs() {
        pegs.clear()
        val boardWidth = BOARD_RIGHT - BOARD_LEFT
        val boardHeight = BOARD_BOTTOM - BOARD_TOP

        for (row in 0 until ROWS) {
            val pegsInRow = COLS_BASE + row
            val rowY = BOARD_TOP + (row + 1) * boardHeight / (ROWS + 2)
            val rowWidth = pegsInRow * boardWidth / (ROWS + COLS_BASE)
            val startX = 0.5f - rowWidth / 2f

            for (col in 0 until pegsInRow) {
                val pegX = startX + col * rowWidth / (pegsInRow - 1).coerceAtLeast(1)
                pegs.add(PegPosition(col, row, pegX, rowY))
            }
        }
    }

    fun setBet(amount: Float) {
        if (!isDropping) {
            betAmount = amount.coerceIn(1f, balance)
            onSelectValueSound?.invoke()
        }
    }

    fun increaseBet() {
        if (!isDropping) {
            betAmount = (betAmount * 2f).coerceAtMost(balance)
            onSelectValueSound?.invoke()
        }
    }

    fun decreaseBet() {
        if (!isDropping) {
            betAmount = (betAmount / 2f).coerceAtLeast(1f)
            onSelectValueSound?.invoke()
        }
    }

    fun dropBall() {
        if (isDropping || balance < betAmount) return

        balance -= betAmount
        isDropping = true

        val newBall = Ball(
            id = ballIdCounter++,
            x = 0.5f + Random.nextFloat() * 0.02f - 0.01f,
            y = BOARD_TOP - BALL_RADIUS,
            vx = Random.nextFloat() * 0.004f - 0.002f,
            vy = 0.005f
        )
        balls.add(newBall)

        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            simulateBalls()
        }
    }

    private suspend fun simulateBalls() {
        while (balls.any { it.active }) {
            updateBalls()
            delay(FRAME_DELAY_MS)
        }
        isDropping = false
    }

    private fun updateBalls() {
        val toRemove = mutableListOf<Ball>()

        for (i in balls.indices) {
            val ball = balls[i]
            if (!ball.active) continue

            ball.vy += GRAVITY

            ball.x += ball.vx
            ball.y += ball.vy

            if (ball.x - BALL_RADIUS < BOARD_LEFT) {
                ball.x = BOARD_LEFT + BALL_RADIUS
                ball.vx = abs(ball.vx) * BOUNCE_DAMPING
            }
            if (ball.x + BALL_RADIUS > BOARD_RIGHT) {
                ball.x = BOARD_RIGHT - BALL_RADIUS
                ball.vx = -abs(ball.vx) * BOUNCE_DAMPING
            }

            for ((pegIndex, peg) in pegs.withIndex()) {
                val dx = ball.x - peg.x
                val dy = ball.y - peg.y
                val dist = sqrt(dx * dx + dy * dy)
                val minDist = BALL_RADIUS + PEG_RADIUS

                if (dist < minDist && dist > 0.0001f) {
                    val nx = dx / dist
                    val ny = dy / dist

                    ball.x = peg.x + nx * minDist
                    ball.y = peg.y + ny * minDist

                    val dot = ball.vx * nx + ball.vy * ny
                    ball.vx = (ball.vx - 2 * dot * nx) * BOUNCE_DAMPING
                    ball.vy = (ball.vy - 2 * dot * ny) * BOUNCE_DAMPING

                    ball.vx += Random.nextFloat() * HORIZONTAL_SPREAD * 2 - HORIZONTAL_SPREAD

                    if (ball.vy < 0.002f) ball.vy = 0.002f

                    pegHighlights.add(PegHighlight(pegIndex))
                    
                    val now = System.currentTimeMillis()
                    if (now - lastKnockSoundTime > KNOCK_SOUND_DEBOUNCE_MS) {
                        onKnockSound?.invoke()
                        lastKnockSoundTime = now
                    }
                }
            }

            if (ball.y > BOARD_BOTTOM) {
                ball.active = false
                val slotCount = MULTIPLIERS.size
                val boardWidth = BOARD_RIGHT - BOARD_LEFT
                val slotWidth = boardWidth / slotCount
                val slotIndex = ((ball.x - BOARD_LEFT) / slotWidth).toInt()
                    .coerceIn(0, slotCount - 1)

                ball.finalSlot = slotIndex
                val multiplier = MULTIPLIERS[slotIndex]
                val winAmount = betAmount * multiplier

                balance += winAmount

                val result = SlotResult(
                    slot = slotIndex,
                    multiplier = multiplier,
                    winAmount = winAmount
                )
                lastResult = result
                recentResults.add(0, result)
                if (recentResults.size > 10) {
                    recentResults.removeAt(recentResults.size - 1)
                }
                
                onGetMoneySound?.invoke()

                toRemove.add(ball)
            }
        }

        for (ball in toRemove) {
            balls.remove(ball)
        }

        val now = System.currentTimeMillis()
        val expiredHighlights = pegHighlights.filter { 
            now - it.timestamp > HIGHLIGHT_DURATION_MS 
        }
        for (highlight in expiredHighlights) {
            pegHighlights.remove(highlight)
        }
    }

    fun resetGame() {
        balance = 1000f
        betAmount = 10f
        balls.clear()
        recentResults.clear()
        pegHighlights.clear()
        lastResult = null
        isDropping = false
        simulationJob?.cancel()
    }
}
