package com.ontime.app.network

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Firestore's REST API accepts a Firebase Auth ID token as a Bearer
 * token, so the same login from Firebase Auth authorises these REST
 * calls too - no separate backend login step needed.
 */
object AuthTokenProvider {

    suspend fun bearerToken(): String {
        val user = FirebaseAuth.getInstance().currentUser
            ?: throw IllegalStateException("No user is logged in")
        val result = user.getIdToken(false).await()
        val token = result.token ?: throw IllegalStateException("Could not get an ID token")
        return "Bearer $token"
    }

    fun currentUid(): String =
        FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("No user is logged in")
}
