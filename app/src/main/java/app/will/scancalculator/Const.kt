package app.will.scancalculator

import androidx.datastore.preferences.core.stringPreferencesKey

object Const {

    const val LOCAL_DATABASE_NAME = "local.db"
    const val DATA_STORE_NAME = "data-store"

    val SECURED_DATA = stringPreferencesKey("secured_data")

    enum class BuildTheme {
        RED, GREEN
    }

    enum class MediaSource {
        FILE, CAMERA
    }
}