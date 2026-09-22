package com.ontime.app.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Single Retrofit instance for talking to the Firestore REST API.
 *
 * PROJECT_ID must match the "project_id" in google-services.json.
 */
object FirestoreClient {

    const val PROJECT_ID = "ontime-9455a"
    private const val BASE_URL = "https://firestore.googleapis.com/"

    /** e.g. "projects/ontime-9455a/databases/(default)/documents" */
    val documentsRoot = "projects/$PROJECT_ID/databases/(default)/documents"

    val api: FirestoreApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FirestoreApi::class.java)
    }
}
