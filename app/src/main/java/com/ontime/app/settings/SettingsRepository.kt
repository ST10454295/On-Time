package com.ontime.app.settings

import com.ontime.app.network.AuthTokenProvider
import com.ontime.app.network.FirestoreClient
import com.ontime.app.network.FirestoreDocument
import com.ontime.app.network.FirestoreField
import com.ontime.app.network.getInt
import com.ontime.app.network.getString

data class UserSettings(
    val defaultDurationMinutes: Int = 30,
    val theme: String = "Light" // "Light" or "Dark" - English only for now, per Part 1 scope
)

/**
 * Reads and writes the current user's settings via the Firestore REST
 * API. One document per user, at settings/{uid}.
 */
class SettingsRepository {

    private fun documentPath(uid: String) = "${FirestoreClient.documentsRoot}/settings/$uid"

    suspend fun save(settings: UserSettings) {
        val uid = AuthTokenProvider.currentUid()
        val token = AuthTokenProvider.bearerToken()

        val document = FirestoreDocument(
            fields = mapOf(
                "defaultDurationMinutes" to FirestoreField.of(settings.defaultDurationMinutes),
                "theme" to FirestoreField.of(settings.theme)
            )
        )

        FirestoreClient.api.setDocument(documentPath(uid), token, document)
    }

    /** Returns the saved settings, or defaults if none have been saved yet. */
    suspend fun load(): UserSettings {
        val uid = AuthTokenProvider.currentUid()
        val token = AuthTokenProvider.bearerToken()

        return try {
            val document = FirestoreClient.api.getDocument(documentPath(uid), token)
            UserSettings(
                defaultDurationMinutes = document.fields.getInt("defaultDurationMinutes") ?: 30,
                theme = document.fields.getString("theme") ?: "Light"
            )
        } catch (notFoundOrError: Exception) {
            // No settings saved yet for this user - fall back to defaults.
            UserSettings()
        }
    }
}
