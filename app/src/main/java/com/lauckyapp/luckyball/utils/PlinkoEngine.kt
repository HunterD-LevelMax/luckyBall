package com.lauckyapp.luckyball.utils

import com.lauckyapp.luckyball.data.models.Ball
import com.lauckyapp.luckyball.data.models.PegHighlight
import com.lauckyapp.luckyball.data.models.PegPosition
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Физика Plinko из PlidfsGaBiew: нормализованные координаты 0..1,
 * пирамида пинов, 11 слотов с множителями.
 */
class PlinkoEngine {

    companion object {
        const val ROWS = 12
        const val COLS_BASE = 3
        /** Symmetrical payouts — no zero slots, higher center jackpot. */
        val MULTIPLIERS = listOf(0.5f, 0.5f, 1f, 2f, 3f, 5f, 3f, 2f, 1f, 0.5f, 0.5f)
        const val GRAVITY = 0.0006f
        const val BALL_RADIUS = 0.019f
        const val PEG_RADIUS = 0.012f
        const val BOUNCE_DAMPING = 0.55f
        const val HORIZONTAL_SPREAD = 0.012f
        const val FRAME_DELAY_MS = 16L
        const val BOARD_TOP = 0.05f
        /** Win line — ball resolves when entering the slot row. */
        const val BOARD_BOTTOM = 0.918f
        /** Gap between last peg row and basket tops. */
        const val PEG_GAP_ABOVE_SLOTS = 0.022f
        /** Basket height as fraction of board height. */
        const val SLOT_HEIGHT_FRAC = 0.08f
        /** Gap between basket bottom and board edge. */
        const val SLOT_BOTTOM_INSET = 0.005f
        const val BOARD_LEFT = 0.05f
        const val BOARD_RIGHT = 0.95f
        const val HIGHLIGHT_DURATION_MS = 400L
        const val KNOCK_SOUND_DEBOUNCE_MS = 50L

        val SLOT_TOP_NORM: Float
            get() = 1f - SLOT_BOTTOM_INSET - SLOT_HEIGHT_FRAC

        val PEG_AREA_BOTTOM: Float
            get() = SLOT_TOP_NORM - PEG_GAP_ABOVE_SLOTS

        fun formatMultiplier(mult: Float): String = when {
            mult <= 0f -> "0x"
            mult < 1f -> ".${(mult * 10).toInt()}x"
            kotlin.math.abs(mult - mult.toInt()) < 0.05f -> "${mult.toInt()}x"
            else -> String.format(java.util.Locale.US, "%.1fx", mult)
        }
    }

    val pegs = mutableListOf<PegPosition>()
    val balls = mutableListOf<Ball>()
    val pegHighlights = mutableListOf<PegHighlight>()

    private var ballIdCounter = 0
    private var lastKnockSoundTime = 0L

    var onKnockSound: (() -> Unit)? = null

    init {
        generatePegs()
    }

    fun generatePegs() {
        pegs.clear()
        val boardWidth = BOARD_RIGHT - BOARD_LEFT
        val pegBottom = PEG_AREA_BOTTOM
        val boardHeight = pegBottom - BOARD_TOP

        for (row in 0 until ROWS) {
            val pegsInRow = COLS_BASE + row
            val rowY = BOARD_TOP + (row + 1) * boardHeight / ROWS
            val rowWidth = pegsInRow * boardWidth / (ROWS + COLS_BASE)
            val startX = 0.5f - rowWidth / 2f

            for (col in 0 until pegsInRow) {
                val pegX = startX + col * rowWidth / (pegsInRow - 1).coerceAtLeast(1)
                pegs.add(PegPosition(col, row, pegX, rowY))
            }
        }
    }

    fun dropBall(betAmount: Int, normalizedX: Float? = null): Ball? {
        if (balls.any { it.active }) return null
        val x = normalizedX?.coerceIn(
            BOARD_LEFT + BALL_RADIUS,
            BOARD_RIGHT - BALL_RADIUS,
        ) ?: (0.5f + Random.nextFloat() * 0.02f - 0.01f)

        val ball = Ball(
            id = ballIdCounter++,
            x = x,
            y = BOARD_TOP - BALL_RADIUS,
            vx = Random.nextFloat() * 0.004f - 0.002f,
            vy = 0.005f,
            betAmount = betAmount,
        )
        balls.add(ball)
        return ball
    }

    fun hasActiveBall(): Boolean = balls.any { it.active }

    fun clearBalls() {
        balls.clear()
        pegHighlights.clear()
    }

    /** @return true если остались активные шарики */
    fun updatePhysics(): Boolean {
        for (ball in balls) {
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
                val slotCount = MULTIPLIERS.size
                val boardWidth = BOARD_RIGHT - BOARD_LEFT
                val slotWidth = boardWidth / slotCount
                val slotIndex = ((ball.x - BOARD_LEFT) / slotWidth).toInt()
                    .coerceIn(0, slotCount - 1)

                ball.active = false
                ball.finalSlot = slotIndex
            }
        }

        val now = System.currentTimeMillis()
        pegHighlights.removeAll { now - it.timestamp > HIGHLIGHT_DURATION_MS }

        return balls.any { it.active }
    }

    fun resolveWin(ball: Ball): Pair<Int, Float> {
        val slotIndex = ball.finalSlot.coerceIn(0, MULTIPLIERS.lastIndex)
        val multiplier = MULTIPLIERS[slotIndex]
        return slotIndex to multiplier
    }
}
