package com.github.enteraname74.soulsearching.remote.utils

import com.github.enteraname74.domain.repository.SharedPlayedListListener
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

class PlayerUserCommunication(
    private val client: HttpClient,
    private val cloudPreferencesDataSource: CloudPreferencesDataSource,
) {
    private var currentUser: PlayerSocketUser? = null
    private var webSocketJob: Job? = null


    suspend fun register(
        listId: Uuid,
        userId: Uuid,
        deviceId: String,
        listener: SharedPlayedListListener
    ) {
        currentUser?.session?.close()
        webSocketJob?.cancel()
        webSocketJob = CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                client.webSocket(
                    urlString = buildUrl(
                        listId = listId,
                        userId = userId,
                        deviceId = deviceId,
                    )
                ) {
                    while (true) {
                        val frame = incoming.receiveCatching().getOrNull() as? Frame.Text
                        val event = runCatching { Json.decodeFromString<Event>(frame?.readText().orEmpty()) }.getOrNull()
                        println("CLUELESS -- PlayerUserCommunication -- got event: $event")
                        when (event) {
                            Event.SyncMusics -> listener.onSyncMusics()
                            Event.SyncPlayedList -> listener.onSyncPlayedList()
                            Event.PlayedListDeleted, null -> break
                        }
                    }
                }
            }
            listener.onClose()
            currentUser = null
        }
    }

    suspend fun unregister() {
        currentUser?.session?.close()
        currentUser = null
    }

    private suspend fun buildUrl(
        listId: Uuid,
        userId: Uuid,
        deviceId: String,
    ): String {
        val baseUrl = cloudPreferencesDataSource.getUrl().replace(
            regex = """http(s?)://""".toRegex(),
            replacement = "",
        )
        val path = "/player/$listId"

        val queryParameters = "?userId=$userId&deviceId=$deviceId"

        return "ws://$baseUrl$path$queryParameters"
    }

    private enum class Event {
        SyncMusics,
        SyncPlayedList,
        PlayedListDeleted,
    }
}

data class PlayerSocketUser(
    val listId: Uuid,
    val userId: Uuid,
    val deviceId: String,
    val session: DefaultClientWebSocketSession,
)