package com.ontime.app.activities

import android.util.Log
import com.ontime.app.logic.Importance
import com.ontime.app.network.AuthTokenProvider
import com.ontime.app.network.FirestoreClient
import com.ontime.app.network.FirestoreDocument
import com.ontime.app.network.FirestoreField
import com.ontime.app.network.documentId
import com.ontime.app.network.getString

private const val TAG = "ActivityRepository"

data class ActivityItem(
    val id: String? = null,
    val title: String,
    val description: String,
    val type: ActivityType,
    val date: String,        // "YYYY-MM-DD"
    val startTime: String,   // "HH:mm"
    val endTime: String,     // "HH:mm"
    val importance: Importance
)

class ActivityRepository {

    private fun collectionPath(uid: String) =
        "${FirestoreClient.documentsRoot}/activities/$uid/items"

    suspend fun add(activity: ActivityItem): String {
        val uid = AuthTokenProvider.currentUid()
        val token = AuthTokenProvider.bearerToken()

        val document = FirestoreDocument(
            fields = mapOf(
                "title" to FirestoreField.of(activity.title),
                "description" to FirestoreField.of(activity.description),
                "type" to FirestoreField.of(activity.type.name),
                "date" to FirestoreField.of(activity.date),
                "startTime" to FirestoreField.of(activity.startTime),
                "endTime" to FirestoreField.of(activity.endTime),
                "importance" to FirestoreField.of(activity.importance.name)
            )
        )

        Log.d(TAG, "Creating activity '${activity.title}' (${activity.type}) on " +
                "${activity.date} ${activity.startTime}-${activity.endTime} (${activity.importance})")

        val created = FirestoreClient.api.createDocument(collectionPath(uid), token, document)
        val id = created.documentId()
        Log.d(TAG, "Activity created with id=$id")
        return id ?: throw IllegalStateException("Firestore did not return a document id")
    }

    suspend fun listAll(): List<ActivityItem> {
        val uid = AuthTokenProvider.currentUid()
        val token = AuthTokenProvider.bearerToken()

        Log.d(TAG, "Fetching all activities for uid=$uid")
        val response = FirestoreClient.api.listDocuments(collectionPath(uid), token)
        val items = response.documents.orEmpty().mapNotNull { doc -> doc.toActivityItem() }
        Log.d(TAG, "Fetched ${items.size} activities")
        return items
    }

    suspend fun listForDate(date: String): List<ActivityItem> =
        listAll().filter { it.date == date }

    suspend fun delete(activityId: String) {
        val uid = AuthTokenProvider.currentUid()
        val token = AuthTokenProvider.bearerToken()
        Log.d(TAG, "Deleting activity id=$activityId")
        FirestoreClient.api.deleteDocument("${collectionPath(uid)}/$activityId", token)
    }

    private fun FirestoreDocument.toActivityItem(): ActivityItem? {
        val title = fields.getString("title") ?: return null
        val description = fields.getString("description") ?: ""
        val date = fields.getString("date") ?: return null
        val startTime = fields.getString("startTime") ?: return null
        val endTime = fields.getString("endTime") ?: return null
        val importanceRaw = fields.getString("importance") ?: return null
        val typeRaw = fields.getString("type") ?: ActivityType.OTHER.name

        val importance = try {
            Importance.valueOf(importanceRaw)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Unknown importance value '$importanceRaw' on document ${this.name} - skipping", e)
            return null
        }

        val type = try {
            ActivityType.valueOf(typeRaw)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Unknown type value '$typeRaw' on document ${this.name} - defaulting to OTHER", e)
            ActivityType.OTHER
        }

        return ActivityItem(
            id = documentId(),
            title = title,
            description = description,
            type = type,
            date = date,
            startTime = startTime,
            endTime = endTime,
            importance = importance
        )
    }
}