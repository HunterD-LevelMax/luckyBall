package com.lauckyapp.luckyball.data.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.lauckyapp.luckyball.R

data class Skin(
    val id: String,
    @StringRes val nameRes: Int,
    val price: Int,
    @DrawableRes val drawableRes: Int,
)

object SkinCatalog {
    val all: List<Skin> = listOf(
        Skin("default", R.string.skin_cheerful, 0, R.drawable.cheerful),
        Skin("red", R.string.skin_fire, 150, R.drawable.fire),
        Skin("blue", R.string.skin_cool, 300, R.drawable.cool),
        Skin("gold", R.string.skin_pirate, 600, R.drawable.pirate),
        Skin("rainbow", R.string.skin_cosmo, 1200, R.drawable.cosmo),
        Skin("pink", R.string.skin_pinky, 450, R.drawable.pinky),
    )

    fun byId(id: String): Skin = all.firstOrNull { it.id == id } ?: all.first()
}
