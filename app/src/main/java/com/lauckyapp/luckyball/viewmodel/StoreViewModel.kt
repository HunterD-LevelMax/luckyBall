package com.lauckyapp.luckyball.viewmodel

import androidx.lifecycle.ViewModel
import com.lauckyapp.luckyball.data.models.Skin
import com.lauckyapp.luckyball.data.models.SkinCatalog
enum class SkinStatus {
    OWNED,
    SELECTED,
    AVAILABLE,
    LOCKED,
}

data class SkinItemUi(
    val skin: Skin,
    val status: SkinStatus,
)

class StoreViewModel : ViewModel() {

    fun skinItems(state: SharedUiState): List<SkinItemUi> {
        return SkinCatalog.all.map { skin ->
            val status = when {
                skin.id == state.selectedSkin -> SkinStatus.SELECTED
                skin.id in state.ownedSkins -> SkinStatus.OWNED
                state.balance >= skin.price -> SkinStatus.AVAILABLE
                else -> SkinStatus.LOCKED
            }
            SkinItemUi(skin = skin, status = status)
        }
    }
}
