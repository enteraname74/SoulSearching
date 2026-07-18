@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.github.enteraname74.soulsearching.features.playback.notification

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.repository.CloudPreferencesRepository
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.model.UpdateData
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.JsAny
import kotlin.js.JsName
import kotlin.js.JsString
import kotlin.js.Promise
import kotlin.js.definedExternally
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class SoulSearchingWebNotification(
    private val cloudPreferencesRepository: CloudPreferencesRepository,
    workDispatcher: WorkDispatcher,
) : SoulSearchingNotification, KoinComponent {
    private val playbackManager: PlaybackManager by inject()
    private val workScope = CoroutineScope(workDispatcher.dispatcher)

    private var notification: BrowserNotification? = null
    private var currentMusicId: String? = null

    override suspend fun update(updateData: UpdateData) {
        val artworkUrl = updateData.toArtworkUrl()

        updateMediaSession(
            updateData = updateData,
            artworkUrl = artworkUrl,
        )
        updateMediaSessionActionHandlers(canControl = updateData.playedListScope.isAdmin)

        val updatedMusicId = updateData.music.musicId.toString()
        if (currentMusicId == updatedMusicId) return

        currentMusicId = updatedMusicId
        showNotification(
            updateData = updateData,
            artworkUrl = artworkUrl,
        )
    }

    override fun dismiss() {
        closeNotification()
        clearMediaSession()
        updateMediaSessionActionHandlers(canControl = false)
        currentMusicId = null
    }

    private suspend fun showNotification(
        updateData: UpdateData,
        artworkUrl: String?,
    ) {
        if (!areNotificationsSupported()) return

        val permission: String = when (BrowserNotification.permission) {
            NOTIFICATION_PERMISSION_GRANTED -> {
                NOTIFICATION_PERMISSION_GRANTED
            }

            NOTIFICATION_PERMISSION_DEFAULT -> {
                val jsPermission: JsString =
                    BrowserNotification.requestPermission().await()

                jsPermission.toString()
            }

            else -> return
        }

        if (permission != NOTIFICATION_PERMISSION_GRANTED) return

        closeNotification()

        notification = BrowserNotification(
            title = updateData.music.name,
            options = notificationOptions(
                body = updateData.music.artistsNames,
                artworkUrl = artworkUrl,
            ),
        )
    }

    private fun closeNotification() {
        notification?.close()
        notification = null
    }

    private fun updateMediaSession(
        updateData: UpdateData,
        artworkUrl: String?,
    ) {
        if (!isMediaSessionSupported()) return

        webNavigator.mediaSession?.metadata = BrowserMediaMetadata(
            mediaMetadataOptions(
                title = updateData.music.name,
                artist = updateData.music.artistsNames,
                album = updateData.music.album.albumName,
                artworkUrl = artworkUrl,
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

    private fun updateMediaSessionActionHandlers(canControl: Boolean) {
        if (!isMediaSessionSupported()) return

        if (canControl) {
            webNavigator.mediaSession?.setActionHandler(MEDIA_ACTION_PLAY) {
                playbackManager.play()
            }
            webNavigator.mediaSession?.setActionHandler(MEDIA_ACTION_PAUSE) {
                playbackManager.pause()
            }
            webNavigator.mediaSession?.setActionHandler(MEDIA_ACTION_PREVIOUS) {
                workScope.launch {
                    playbackManager.previous()
                }
            }
            webNavigator.mediaSession?.setActionHandler(MEDIA_ACTION_NEXT) {
                workScope.launch {
                    playbackManager.next()
                }
            }
            webNavigator.mediaSession?.setActionHandler(MEDIA_ACTION_SEEK_TO) { details ->
                getSeekTime(details)?.let { seekTime ->
                    workScope.launch {
                        playbackManager.seekToPosition((seekTime * MILLIS_IN_SECOND).toInt())
                    }
                }
            }
        } else {
            webNavigator.mediaSession?.setActionHandler(MEDIA_ACTION_PLAY, null)
            webNavigator.mediaSession?.setActionHandler(MEDIA_ACTION_PAUSE, null)
            webNavigator.mediaSession?.setActionHandler(MEDIA_ACTION_PREVIOUS, null)
            webNavigator.mediaSession?.setActionHandler(MEDIA_ACTION_NEXT, null)
            webNavigator.mediaSession?.setActionHandler(MEDIA_ACTION_SEEK_TO, null)
        }
    }

    private fun notificationOptions(
        body: String,
        artworkUrl: String?,
    ): BrowserNotificationOptions =
        newJsObject()
            .unsafeCast<BrowserNotificationOptions>()
            .apply {
                this.body = body
                tag = NOTIFICATION_TAG
                renotify = false
                silent = true
                icon = artworkUrl
                image = artworkUrl.takeIf { areNotificationImagesSupported() }
            }

    private fun mediaMetadataOptions(
        title: String,
        artist: String,
        album: String,
        artworkUrl: String?,
    ): BrowserMediaMetadataOptions =
        newJsObject()
            .unsafeCast<BrowserMediaMetadataOptions>()
            .apply {
                this.title = title
                this.artist = artist
                this.album = album
                if (artworkUrl != null) {
                    artwork = mediaImageArray(
                        src = artworkUrl,
                        sizes = MEDIA_ARTWORK_SIZE,
                        type = artworkUrl.toArtworkMimeType(),
                    )
                }
            }

    private suspend fun UpdateData.toArtworkUrl(): String? =
        cover?.toPngDataUrl()
            ?: music.cover.toDirectArtworkUrl()

    private suspend fun Cover.toDirectArtworkUrl(): String? =
        (this as? Cover.Url)
            ?.url
            ?.takeIf { it.isNotBlank() }
            ?.let { url ->
                when {
                    url.startsWith("http://") || url.startsWith("https://") -> url
                    else -> buildAbsoluteArtworkUrl(
                        baseUrl = cloudPreferencesRepository.observeUrl().firstOrNull().orEmpty(),
                        path = url,
                    )
                }
            }

    private fun String.toArtworkMimeType(): String =
        substringBefore(';')
            .removePrefix("data:")
            .takeIf { startsWith("data:") && it.startsWith("image/") }
            ?: when (substringBefore('?').substringAfterLast('.').lowercase()) {
                "png" -> "image/png"
                "webp" -> "image/webp"
                "gif" -> "image/gif"
                else -> "image/jpeg"
            }

    private fun ImageBitmap.toPngDataUrl(): String? =
        runCatching {
            val pixelMap = toPixelMap()
            val canvas = document.createElement("canvas").unsafeCast<HTMLCanvasElement>()
            canvas.width = width
            canvas.height = height

            val context = canvas
                .getContext("2d")
                    as CanvasRenderingContext2D
            val imageData = context.createImageData(
                sw = width.toDouble(),
                sh = height.toDouble(),
            )
            val data: Uint8ClampedArray = imageData.data

            var dataIndex = 0
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val argb = pixelMap[x, y].toArgb()
                    setClampedArrayValue(
                        array = data,
                        index = dataIndex++,
                        value = (argb shr 16) and COLOR_CHANNEL_MASK,
                    )
                    setClampedArrayValue(
                        array = data,
                        index = dataIndex++,
                        value = (argb shr 8) and COLOR_CHANNEL_MASK,
                    )
                    setClampedArrayValue(
                        array = data,
                        index = dataIndex++,
                        value = argb and COLOR_CHANNEL_MASK,
                    )
                    setClampedArrayValue(
                        array = data,
                        index = dataIndex++,
                        value = (argb shr 24) and COLOR_CHANNEL_MASK,
                    )
                }
            }

            context.putImageData(
                imagedata = imageData,
                dx = 0.0,
                dy = 0.0,
            )
            canvas.toDataURL("image/png")
        }.getOrNull()

    private companion object {
        private const val NOTIFICATION_PERMISSION_GRANTED: String = "granted"
        private const val NOTIFICATION_PERMISSION_DEFAULT: String = "default"
        private const val NOTIFICATION_TAG: String = "soul-searching-current-music"

        private const val MEDIA_SESSION_NONE: String = "none"
        private const val MEDIA_SESSION_PAUSED: String = "paused"
        private const val MEDIA_SESSION_PLAYING: String = "playing"

        private const val MEDIA_ACTION_PLAY: String = "play"
        private const val MEDIA_ACTION_PAUSE: String = "pause"
        private const val MEDIA_ACTION_PREVIOUS: String = "previoustrack"
        private const val MEDIA_ACTION_NEXT: String = "nexttrack"
        private const val MEDIA_ACTION_SEEK_TO: String = "seekto"

        private const val MEDIA_ARTWORK_SIZE: String = "512x512"
        private const val MILLIS_IN_SECOND: Int = 1000
        private const val COLOR_CHANNEL_MASK: Int = 0xFF
    }
}

private fun newJsObject(): JsAny =
    js("({})")

private fun mediaImageArray(
    src: String,
    sizes: String,
    type: String,
): JsAny =
    js("[{ src: src, sizes: sizes, type: type }]")

private fun buildAbsoluteArtworkUrl(
    baseUrl: String,
    path: String,
): String =
    js("new URL(path, baseUrl.endsWith('/') ? baseUrl : baseUrl + '/').toString()")

private fun areNotificationsSupported(): Boolean =
    js("typeof Notification !== 'undefined'")

private fun areNotificationImagesSupported(): Boolean =
    js("typeof Notification !== 'undefined' && 'image' in Notification.prototype && !navigator.userAgent.includes('Firefox')")

private fun isMediaSessionSupported(): Boolean =
    js("typeof navigator !== 'undefined' && 'mediaSession' in navigator && typeof MediaMetadata !== 'undefined'")

private fun getSeekTime(details: JsAny?): Double? =
    js("details && typeof details.seekTime === 'number' ? details.seekTime : null")

private fun setClampedArrayValue(
    array: Uint8ClampedArray,
    index: Int,
    value: Int,
) {
    js("array[index] = value")
}

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
    var icon: String?
    var image: String?
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
    fun setActionHandler(
        action: String,
        handler: ((JsAny?) -> Unit)?,
    )
}

@JsName("MediaMetadata")
private external class BrowserMediaMetadata(
    options: BrowserMediaMetadataOptions,
)

private external interface BrowserMediaMetadataOptions : JsAny {
    var title: String?
    var artist: String?
    var album: String?
    var artwork: JsAny?
}
