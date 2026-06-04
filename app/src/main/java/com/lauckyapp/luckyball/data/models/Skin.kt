package com.lauckyapp.luckyball.data.models

import androidx.annotation.DrawableRes
import com.lauckyapp.luckyball.R

data class Skin(
    val id: String,
    val name: String,
    val price: Int,
    @DrawableRes val drawableRes: Int,
)

object SkinCatalog {
    val all: List<Skin> = listOf(
        Skin("default", "Весёлый", 0, R.drawable.cheerful),
        Skin("red", "Огненный", 150, R.drawable.fire),
        Skin("blue", "Крутой", 300, R.drawable.cool),
        Skin("gold", "Пиратский", 600, R.drawable.pirate),
        Skin("rainbow", "Космос", 1200, R.drawable.cosmo),
        Skin("pink", "Розовый", 450, R.drawable.pinky),
    )

    fun byId(id: String): Skin = all.firstOrNull { it.id == id } ?: all.first()
}
