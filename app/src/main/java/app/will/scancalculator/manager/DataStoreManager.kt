package app.will.scancalculator.manager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import app.will.scancalculator.Const.SECURED_DATA
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>, private val security: EncryptionManager
) {

    private val bytesToStringSeparator = "|"

    fun getSecuredData(): Flow<String> = dataStore.data.map { preferences ->
        preferences[SECURED_DATA]?.let { data ->
            if (data.isNotBlank()) {
                val decryptedValue =
                    security.decryptData(data.split(bytesToStringSeparator).map { it.toByte() }
                        .toByteArray())
                val json = Json { encodeDefaults = true }
                json.decodeFromString(decryptedValue)
            } else {
                ""
            }
        } ?: run {
            ""
        }
    }

    suspend fun setSecuredData(value: String) {
        dataStore.edit {
            val encryptedValue = security.encryptData(Json.encodeToString(value))
            it[SECURED_DATA] = encryptedValue.joinToString(bytesToStringSeparator)
        }
    }
}