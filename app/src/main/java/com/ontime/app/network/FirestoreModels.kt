package com.ontime.app.network

/**
 * Firestore's REST API represents every field as a typed wrapper object,
 * e.g. {"stringValue": "High"} rather than a plain JSON value. These
 * classes mirror that shape so Retrofit/Gson can (de)serialise it.
 */
data class FirestoreValue(
    val stringValue: String? = null,
    val integerValue: String? = null,
    val doubleValue: Double? = null,
    val booleanValue: Boolean? = null
)

data class FirestoreDocument(
    val name: String? = null,
    val fields: Map<String, FirestoreValue>? = null
)

data class FirestoreListResponse(
    val documents: List<FirestoreDocument>? = null
)

/** Small helpers so call sites can write plain Kotlin values. */
object FirestoreField {
    fun of(value: String) = FirestoreValue(stringValue = value)
    fun of(value: Int) = FirestoreValue(integerValue = value.toString())
    fun of(value: Boolean) = FirestoreValue(booleanValue = value)
}

fun Map<String, FirestoreValue>?.getString(key: String): String? = this?.get(key)?.stringValue
fun Map<String, FirestoreValue>?.getInt(key: String): Int? = this?.get(key)?.integerValue?.toIntOrNull()
fun Map<String, FirestoreValue>?.getBool(key: String): Boolean? = this?.get(key)?.booleanValue

/** Pulls the last path segment out of a returned document's "name" (its full resource path) - used as the document/activity id. */
fun FirestoreDocument.documentId(): String? = name?.substringAfterLast('/')
