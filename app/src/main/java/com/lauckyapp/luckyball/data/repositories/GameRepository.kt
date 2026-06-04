package com.lauckyapp.luckyball.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lauckyapp.luckyball.data.models.WinRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.gameDataStore: DataStore<Preferences> by preferencesDataStore(name = "plinko_balls_prefs")

class GameRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    private object Keys {
        val BALANCE = intPreferencesKey("balance")
        val OWNED_SKINS = stringPreferencesKey("owned_skins")
        val SELECTED_SKIN = stringPreferencesKey("selected_skin")
        val HISTORY = stringPreferencesKey("history")
    }

    val gameState: Flow<GamePersistedState> = context.gameDataStore.data.map { prefs ->
        val owned = prefs[Keys.OWNED_SKINS]?.split(",")?.filter { it.isNotBlank() }?.toSet()
            ?: setOf("default")
        val historyRaw = prefs[Keys.HISTORY].orEmpty()
        val history = if (historyRaw.isBlank()) {
            emptyList()
        } else {
            runCatching { json.decodeFromString<List<WinRecord>>(historyRaw) }.getOrDefault(emptyList())
        }
        GamePersistedState(
            balance = prefs[Keys.BALANCE] ?: 500,
            ownedSkins = owned.ifEmpty { setOf("default") },
            selectedSkin = prefs[Keys.SELECTED_SKIN] ?: "default",
            history = history,
        )
    }

    suspend fun save(
        balance: Int,
        ownedSkins: Set<String>,
        selectedSkin: String,
        history: List<WinRecord>,
    ) {
        context.gameDataStore.edit { prefs ->
            prefs[Keys.BALANCE] = balance
            prefs[Keys.OWNED_SKINS] = ownedSkins.joinToString(",")
            prefs[Keys.SELECTED_SKIN] = selectedSkin
            prefs[Keys.HISTORY] = json.encodeToString(history.take(50))
        }
    }
}

data class GamePersistedState(
    val balance: Int,
    val ownedSkins: Set<String>,
    val selectedSkin: String,
    val history: List<WinRecord>,
)
