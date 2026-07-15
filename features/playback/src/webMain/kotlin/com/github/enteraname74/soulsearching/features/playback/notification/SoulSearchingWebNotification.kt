@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.github.enteraname74.soulsearching.features.playback.notification

import com.github.enteraname74.soulsearching.features.playback.model.UpdateData
import kotlinx.coroutines.await
import kotlin.js.JsAny
import kotlin.js.JsName
import kotlin.js.JsString
import kotlin.js.Promise
import kotlin.js.definedExternally
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class SoulSearchingWebNotification : SoulSearchingNotification {
    private var notification: BrowserNotification? = null
    private var currentMusicId: String? = null

    override suspend fun update(updateData: UpdateData) {
        updateMediaSession(updateData)

        val updatedMusicId = updateData.music.musicId.toString()
        if (currentMusicId == updatedMusicId) return

        currentMusicId = updatedMusicId
        showNotification(updateData)
    }

    override fun dismiss() {
        closeNotification()
        clearMediaSession()
        currentMusicId = null
    }

    private suspend fun showNotification(updateData: UpdateData) {
        if (!areNotificationsSupported()) return

        val permission = when (BrowserNotification.permission) {
            NOTIFICATION_PERMISSION_GRANTED -> NOTIFICATION_PERMISSION_GRANTED
            NOTIFICATION_PERMISSION_DEFAULT -> BrowserNotification.requestPermission()
                .await<JsString>()
            else -> return
        }

        if (permission != NOTIFICATION_PERMISSION_GRANTED) return

        closeNotification()
        notification = BrowserNotification(
            title = updateData.music.name,
            options = notificationOptions(
                body = updateData.music.artistsNames,
            ),
        )
    }

    private fun closeNotification() {
        notification?.close()
        notification = null
    }

    private fun updateMediaSession(updateData: UpdateData) {
        if (!isMediaSessionSupported()) return

        webNavigator.mediaSession?.metadata = BrowserMediaMetadata(
            mediaMetadataOptions(
                title = updateData.music.name,
                artist = updateData.music.artistsNames,
                album = updateData.music.album.albumName,
            ),
        )
        webNavigator.mediaSession?.playbackState = if (updateData.isPlaying) {
            MEDIA_SESSION_PLAYING
        } else {
            MEDIA_SESSION_PAUSED
        }
    }

    private fun clearMediaSession() {
        if (!isMediaSessionSupported()) return

        webNavigator.mediaSession?.metadata = null
        webNavigator.mediaSession?.playbackState = MEDIA_SESSION_NONE
    }

    private fun notificationOptions(
        body: String,
    ): BrowserNotificationOptions =
        newJsObject()
            .unsafeCast<BrowserNotificationOptions>()
            .apply {
                this.body = body
                tag = NOTIFICATION_TAG
                renotify = false
                silent = true
            }

    private fun mediaMetadataOptions(
        title: String,
        artist: String,
        album: String,
    ): BrowserMediaMetadataOptions =
        newJsObject()
            .unsafeCast<BrowserMediaMetadataOptions>()
            .apply {
                this.title = title
                this.artist = artist
                this.album = album
            }

    private companion object {
        private const val NOTIFICATION_PERMISSION_GRANTED: String = "granted"
        private const val NOTIFICATION_PERMISSION_DEFAULT: String = "default"
        private const val NOTIFICATION_TAG: String = "soul-searching-current-music"

        private const val MEDIA_SESSION_NONE: String = "none"
        private const val MEDIA_SESSION_PAUSED: String = "paused"
        private const val MEDIA_SESSION_PLAYING: String = "playing"
    }
}

private fun newJsObject(): JsAny =
    js("({})")

private fun areNotificationsSupported(): Boolean =
    js("typeof Notification !== 'undefined'")

private fun isMediaSessionSupported(): Boolean =
    js("typeof navigator !== 'undefined' && 'mediaSession' in navigator && typeof MediaMetadata !== 'undefined'")

@JsName("Notification")
private external class BrowserNotification(
    title: String,
    options: BrowserNotificationOptions = definedExternally,
) {
    fun close()

    companion object {
        val permission: String
        fun requestPermission(): Promise<JsString>
    }
}

private external interface BrowserNotificationOptions : JsAny {
    var body: String?
    var tag: String?
    var renotify: Boolean?
    var silent: Boolean?
}

@JsName("navigator")
private external val webNavigator: BrowserNavigator

private external interface BrowserNavigator : JsAny {
    val mediaSession: BrowserMediaSession?
}

private external interface BrowserMediaSession : JsAny {
    var metadata: BrowserMediaMetadata?
    var playbackState: String
}

@JsName("MediaMetadata")
private external class BrowserMediaMetadata(
    options: BrowserMediaMetadataOptions,
)

private external interface BrowserMediaMetadataOptions : JsAny {
    var title: String?
    var artist: String?
    var album: String?
}
