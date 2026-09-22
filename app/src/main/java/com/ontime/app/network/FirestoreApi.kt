package com.ontime.app.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * This is the app's RESTful API layer: plain HTTPS calls (via Retrofit,
 * the external library) to Google's hosted Firestore REST API, carrying
 * a Firebase Auth ID token for authorisation. No Firebase SDK is used
 * here for reading/writing data - only for authentication.
 *
 * Firestore REST reference: https://firestore.googleapis.com/v1/{path}
 * where {path} is "projects/{projectId}/databases/(default)/documents/..."
 */
interface FirestoreApi {

    @GET("v1/{path}")
    suspend fun getDocument(
        @Path("path", encoded = true) path: String,
        @Header("Authorization") authorization: String
    ): FirestoreDocument

    @GET("v1/{path}")
    suspend fun listDocuments(
        @Path("path", encoded = true) path: String,
        @Header("Authorization") authorization: String
    ): FirestoreListResponse

    /** Creates a document with a specific, known id (used for one-per-user Settings). */
    @PATCH("v1/{path}")
    suspend fun setDocument(
        @Path("path", encoded = true) path: String,
        @Header("Authorization") authorization: String,
        @Body document: FirestoreDocument
    ): FirestoreDocument

    /** Creates a document with an auto-generated id inside a collection (used for Activities). */
    @POST("v1/{collectionPath}")
    suspend fun createDocument(
        @Path("collectionPath", encoded = true) collectionPath: String,
        @Header("Authorization") authorization: String,
        @Body document: FirestoreDocument
    ): FirestoreDocument

    @DELETE("v1/{path}")
    suspend fun deleteDocument(
        @Path("path", encoded = true) path: String,
        @Header("Authorization") authorization: String
    )
}
