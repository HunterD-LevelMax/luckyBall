package com.euphoria.ballneonogame.gsde

import android.content.Context
import android.media.SoundPool
import com.euphoria.ballneonogame.R

class SourgnMabgfs(context: Context) {
    
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(10)
        .build()
    
    private val sdfsed = mutableMapOf<SugmTysa, Int>()
    
    init {
        sdfsed[SugmTysa.KNOCK] = soundPool.load(context, R.raw.knock, 1)
        sdfsed[SugmTysa.GET_MONEY] = soundPool.load(context, R.raw.get_money, 1)
        sdfsed[SugmTysa.SELECT_VALUE] = soundPool.load(context, R.raw.select_value, 1)
    }
    
    fun dsfffPlaw(sugmTysa: SugmTysa, volume: Float = 1f) {
        val soundId = sdfsed[sugmTysa] ?: return
        soundPool.play(soundId, volume, volume, 1, 0, 1f)
    }
    
    fun playKnock() {
        dsfffPlaw(SugmTysa.KNOCK, 0.5f)
    }
    
    fun playGetMoney() {
        dsfffPlaw(SugmTysa.GET_MONEY, 0.8f)
    }
    
    fun playSelectValue() {
        dsfffPlaw(SugmTysa.SELECT_VALUE, 0.6f)
    }
    
    fun release() {
        soundPool.release()
    }
}

enum class SugmTysa {
    KNOCK,
    GET_MONEY,
    SELECT_VALUE
}
