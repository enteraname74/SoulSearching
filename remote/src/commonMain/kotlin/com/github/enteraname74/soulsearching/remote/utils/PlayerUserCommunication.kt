package com.github.enteraname74.soulsearching.remote.utils

import com.github.enteraname74.domain.repository.SharedPlayedListListener
import com.github.enteraname74.domain.util.LocaleUtils
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

class PlayerUserCommunication(
    private val client: HttpClient,
    private val cloudPreferencesDataSource: CloudPreferencesDataSource,
    workDispatcher: WorkDispatcher,
) {
    private val workScope = CoroutineScope(workDispatcher.dispatcher)
    private var webSocketJob: Job? = null
    private var session: DefaultClientWebSocketSession? = null
    private var listener: SharedPlayedListListener? = null

    fun register(
        listId: Uuid,
        userId: Uuid,
        deviceId: String,
        newListener: SharedPlayedListListener,
    ) {
        unregister()
        listener = newListener
        webSocketJob = workScope.launch {
            runCatching {
                client.webSocket(
                    request = {
                        header(HttpHeaders.AcceptLanguage, LocaleUtils.currentLanguage())
                    },
                    urlString = buildUrl(
                        listId = listId,
                        userId = userId,
                        deviceId = deviceId,
                    )
                ) {
                    session = this
                    listener?.onConnected()

                    while (true) {
                        val frame = incoming.receiveCatching().getOrNull() as? Frame.Text
                        val event = runCatching { Json.decodeFromString<Event>(frame?.readText().orEmpty()) }.getOrNull()
                        when (event) {
                            Event.SyncMusics -> listener?.onSyncMusics()
                            Event.SyncPlayedList -> listener?.onSyncPlayedList()
                            Event.PlayedListDeleted, null -> break
                        }
                    }
                }
            }
            listener?.onClose()
            listener = null
        }
    }

    fun unregister() {
        listener = null
        webSocketJob?.cancel()
        session?.cancel()
        webSocketJob = null
        session = null
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
