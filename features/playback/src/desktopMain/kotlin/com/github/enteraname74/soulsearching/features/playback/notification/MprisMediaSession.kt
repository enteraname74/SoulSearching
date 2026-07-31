package com.github.enteraname74.soulsearching.features.playback.notification

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.model.UpdateData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.freedesktop.dbus.DBusPath
import org.freedesktop.dbus.annotations.DBusInterfaceName
import org.freedesktop.dbus.connections.impl.DBusConnection
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder
import org.freedesktop.dbus.errors.NotSupported
import org.freedesktop.dbus.errors.PropertyReadOnly
import org.freedesktop.dbus.errors.UnknownInterface
import org.freedesktop.dbus.errors.UnknownProperty
import org.freedesktop.dbus.interfaces.DBusInterface
import org.freedesktop.dbus.interfaces.Properties
import org.freedesktop.dbus.types.Variant
import kotlin.math.max

internal class MprisMediaSession(
    private val playbackManager: PlaybackManager,
    workDispatcher: WorkDispatcher,
) : MprisRoot, MprisPlayer, Properties {
    private val workScope = CoroutineScope(workDispatcher.dispatcher)

    private var connection: DBusConnection? = null
    private var currentData: UpdateData? = null
    private var currentArtUrl: String? = null

    fun update(
        updateData: UpdateData,
        artUrl: String?,
    ) {
        ensureStarted()

        currentData = updateData
        currentArtUrl = artUrl

        emitPlayerPropertiesChanged(playerProperties())
    }

    fun dismiss() {
        currentData = null
        currentArtUrl = null

        emitPlayerPropertiesChanged(
            mapOf(
                PLAYER_PROPERTY_PLAYBACK_STATUS to Variant(PLAYBACK_STATUS_STOPPED),
                PLAYER_PROPERTY_METADATA to Variant(metadata(), DBUS_SIGNATURE_VARIANT_MAP),
                PLAYER_PROPERTY_POSITION to Variant(0L),
                PLAYER_PROPERTY_CAN_GO_NEXT to Variant(false),
                PLAYER_PROPERTY_CAN_GO_PREVIOUS to Variant(false),
                PLAYER_PROPERTY_CAN_PLAY to Variant(false),
                PLAYER_PROPERTY_CAN_PAUSE to Variant(false),
                PLAYER_PROPERTY_CAN_SEEK to Variant(false),
                PLAYER_PROPERTY_CAN_CONTROL to Variant(false),
            )
        )
    }

    override fun getObjectPath(): String = MPRIS_OBJECT_PATH

    override fun Raise() = Unit

    override fun Quit() = Unit

    override fun Next() {
        launchIfCanControl {
            playbackManager.next()
        }
    }

    override fun Previous() {
        launchIfCanControl {
            playbackManager.previous()
        }
    }

    override fun Pause() {
        if (canControl()) {
            playbackManager.pause()
        }
    }

    override fun PlayPause() {
        launchIfCanControl {
            playbackManager.togglePlayPause()
        }
    }

    override fun Stop() {
        launchIfCanControl {
            playbackManager.stopPlayback(resetPlayedList = false)
        }
    }

    override fun Play() {
        if (canControl()) {
            playbackManager.play()
        }
    }

    override fun Seek(offset: Long) {
        launchIfCanControl {
            val currentPositionMs = currentData?.position ?: 0L
            val targetPositionMs = max(0L, currentPositionMs + offset.toMillis())
            playbackManager.seekTo(targetPositionMs.toInt())
        }
    }

    override fun SetPosition(trackId: DBusPath, position: Long) {
        if (trackId.path != currentTrackPath()) return

        launchIfCanControl {
            playbackManager.seekTo(position.toMillis().toInt())
        }
    }

    override fun OpenUri(uri: String) {
        throw NotSupported("Soul Searching does not support opening URIs through MPRIS.")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <A> Get(
        _interfaceName: String,
        _propertyName: String,
    ): A =
        (GetAll(_interfaceName)[_propertyName]
            ?: throw UnknownProperty("Unknown property: $_propertyName"))
            .value as A

    override fun <A> Set(
        _interfaceName: String,
        _propertyName: String,
        _value: A,
    ) {
        when (_interfaceName) {
            MPRIS_ROOT_INTERFACE -> {
                if (_propertyName == ROOT_PROPERTY_FULLSCREEN) return
                throw PropertyReadOnly("Property is read-only: $_propertyName")
            }

            MPRIS_PLAYER_INTERFACE -> {
                if (_propertyName in WRITABLE_PLAYER_PROPERTIES) return
                throw PropertyReadOnly("Property is read-only: $_propertyName")
            }

            else -> throw UnknownInterface("Unknown interface: $_interfaceName")
        }
    }

    override fun GetAll(_interfaceName: String): Map<String, Variant<*>> =
        when (_interfaceName) {
            MPRIS_ROOT_INTERFACE -> rootProperties()
            MPRIS_PLAYER_INTERFACE -> playerProperties()
            else -> throw UnknownInterface("Unknown interface: $_interfaceName")
        }

    private fun ensureStarted() {
        if (connection != null) return

        runCatching {
            DBusConnectionBuilder
                .forSessionBus()
                .withShared(false)
                .build()
                .also { dbusConnection ->
                    dbusConnection.requestBusName(MPRIS_BUS_NAME)
                    dbusConnection.exportObject(MPRIS_OBJECT_PATH, this)
                    connection = dbusConnection
                }
        }.onFailure { error ->
            println("MPRIS -- Unable to initialize desktop media session: ${error.message}")
        }
    }

    private fun emitPlayerPropertiesChanged(properties: Map<String, Variant<*>>) {
        connection?.sendMessage(
            Properties.PropertiesChanged(
                MPRIS_OBJECT_PATH,
                MPRIS_PLAYER_INTERFACE,
                properties,
                emptyList(),
            )
        )
    }

    private fun rootProperties(): Map<String, Variant<*>> =
        mapOf(
            ROOT_PROPERTY_CAN_QUIT to Variant(false),
            ROOT_PROPERTY_FULLSCREEN to Variant(false),
            ROOT_PROPERTY_CAN_SET_FULLSCREEN to Variant(false),
            ROOT_PROPERTY_CAN_RAISE to Variant(false),
            ROOT_PROPERTY_HAS_TRACK_LIST to Variant(false),
            ROOT_PROPERTY_IDENTITY to Variant(APP_NAME),
            ROOT_PROPERTY_DESKTOP_ENTRY to Variant(DESKTOP_ENTRY),
            ROOT_PROPERTY_SUPPORTED_URI_SCHEMES to Variant(listOf("file", "http", "https"), DBUS_SIGNATURE_STRING_LIST),
            ROOT_PROPERTY_SUPPORTED_MIME_TYPES to Variant(SUPPORTED_MIME_TYPES, DBUS_SIGNATURE_STRING_LIST),
        )

    private fun playerProperties(): Map<String, Variant<*>> {
        val canControl = canControl()

        return mapOf(
            PLAYER_PROPERTY_PLAYBACK_STATUS to Variant(playbackStatus()),
            PLAYER_PROPERTY_LOOP_STATUS to Variant(LOOP_STATUS_NONE),
            PLAYER_PROPERTY_RATE to Variant(1.0),
            PLAYER_PROPERTY_SHUFFLE to Variant(false),
            PLAYER_PROPERTY_METADATA to Variant(metadata(), DBUS_SIGNATURE_VARIANT_MAP),
            PLAYER_PROPERTY_VOLUME to Variant(1.0),
            PLAYER_PROPERTY_POSITION to Variant(currentPositionMicros()),
            PLAYER_PROPERTY_MINIMUM_RATE to Variant(1.0),
            PLAYER_PROPERTY_MAXIMUM_RATE to Variant(1.0),
            PLAYER_PROPERTY_CAN_GO_NEXT to Variant(canControl && (currentData?.playedListSize ?: 0L) > 1L),
            PLAYER_PROPERTY_CAN_GO_PREVIOUS to Variant(canControl),
            PLAYER_PROPERTY_CAN_PLAY to Variant(canControl),
            PLAYER_PROPERTY_CAN_PAUSE to Variant(canControl),
            PLAYER_PROPERTY_CAN_SEEK to Variant(canControl),
            PLAYER_PROPERTY_CAN_CONTROL to Variant(canControl),
        )
    }

    private fun metadata(): Map<String, Variant<*>> {
        val data = currentData

        return buildMap {
            put(METADATA_TRACK_ID, Variant(DBusPath(currentTrackPath())))
            if (data == null) return@buildMap

            put(METADATA_TITLE, Variant(data.music.name))
            put(METADATA_ARTIST, Variant(data.music.mprisArtistNames(), DBUS_SIGNATURE_STRING_LIST))
            put(METADATA_ALBUM, Variant(data.music.album.albumName))
            if (data.music.duration > 0L) {
                put(METADATA_LENGTH, Variant(data.music.duration * MICROSECONDS_IN_MILLISECOND))
            }
            currentArtUrl?.let { artUrl ->
                put(METADATA_ART_URL, Variant(artUrl))
            }
        }
    }

    private fun currentTrackPath(): String =
        currentData
            ?.music
            ?.musicId
            ?.toString()
            ?.replace("-", "_")
            ?.let { musicId -> "$TRACK_OBJECT_PATH_PREFIX/$musicId" }
            ?: NO_TRACK_OBJECT_PATH

    private fun playbackStatus(): String =
        when {
            currentData == null -> PLAYBACK_STATUS_STOPPED
            currentData?.isPlaying == true -> PLAYBACK_STATUS_PLAYING
            else -> PLAYBACK_STATUS_PAUSED
        }

    private fun currentPositionMicros(): Long =
        (currentData?.position ?: 0L) * MICROSECONDS_IN_MILLISECOND

    private fun canControl(): Boolean =
        currentData?.playedListScope?.isAdmin == true

    private fun Music.mprisArtistNames(): List<String> =
        buildList {
            add(album.artist.artistName)
            addAll(artists.map { it.artistName })
        }.distinct()

    private fun launchIfCanControl(block: suspend () -> Unit) {
        if (!canControl()) return

        workScope.launch {
            block()
        }
    }

    private fun Long.toMillis(): Long =
        this / MICROSECONDS_IN_MILLISECOND

    private companion object {
        private const val APP_NAME: String = "Soul Searching"
        private const val DESKTOP_ENTRY: String = "io.github.enteraname74.soulsearching"

        private const val MPRIS_BUS_NAME: String = "org.mpris.MediaPlayer2.SoulSearching"
        private const val MPRIS_OBJECT_PATH: String = "/org/mpris/MediaPlayer2"
        private const val MPRIS_ROOT_INTERFACE: String = "org.mpris.MediaPlayer2"
        private const val MPRIS_PLAYER_INTERFACE: String = "org.mpris.MediaPlayer2.Player"

        private const val NO_TRACK_OBJECT_PATH: String = "/org/mpris/MediaPlayer2/TrackList/NoTrack"
        private const val TRACK_OBJECT_PATH_PREFIX: String = "/com/github/enteraname74/soulsearching/track"

        private const val DBUS_SIGNATURE_STRING_LIST: String = "as"
        private const val DBUS_SIGNATURE_VARIANT_MAP: String = "a{sv}"
        private const val MICROSECONDS_IN_MILLISECOND: Long = 1_000L

        private const val ROOT_PROPERTY_CAN_QUIT: String = "CanQuit"
        private const val ROOT_PROPERTY_FULLSCREEN: String = "Fullscreen"
        private const val ROOT_PROPERTY_CAN_SET_FULLSCREEN: String = "CanSetFullscreen"
        private const val ROOT_PROPERTY_CAN_RAISE: String = "CanRaise"
        private const val ROOT_PROPERTY_HAS_TRACK_LIST: String = "HasTrackList"
        private const val ROOT_PROPERTY_IDENTITY: String = "Identity"
        private const val ROOT_PROPERTY_DESKTOP_ENTRY: String = "DesktopEntry"
        private const val ROOT_PROPERTY_SUPPORTED_URI_SCHEMES: String = "SupportedUriSchemes"
        private const val ROOT_PROPERTY_SUPPORTED_MIME_TYPES: String = "SupportedMimeTypes"

        private const val PLAYER_PROPERTY_PLAYBACK_STATUS: String = "PlaybackStatus"
        private const val PLAYER_PROPERTY_LOOP_STATUS: String = "LoopStatus"
        private const val PLAYER_PROPERTY_RATE: String = "Rate"
        private const val PLAYER_PROPERTY_SHUFFLE: String = "Shuffle"
        private const val PLAYER_PROPERTY_METADATA: String = "Metadata"
        private const val PLAYER_PROPERTY_VOLUME: String = "Volume"
        private const val PLAYER_PROPERTY_POSITION: String = "Position"
        private const val PLAYER_PROPERTY_MINIMUM_RATE: String = "MinimumRate"
        private const val PLAYER_PROPERTY_MAXIMUM_RATE: String = "MaximumRate"
        private const val PLAYER_PROPERTY_CAN_GO_NEXT: String = "CanGoNext"
        private const val PLAYER_PROPERTY_CAN_GO_PREVIOUS: String = "CanGoPrevious"
        private const val PLAYER_PROPERTY_CAN_PLAY: String = "CanPlay"
        private const val PLAYER_PROPERTY_CAN_PAUSE: String = "CanPause"
        private const val PLAYER_PROPERTY_CAN_SEEK: String = "CanSeek"
        private const val PLAYER_PROPERTY_CAN_CONTROL: String = "CanControl"

        private const val METADATA_TRACK_ID: String = "mpris:trackid"
        private const val METADATA_LENGTH: String = "mpris:length"
        private const val METADATA_ART_URL: String = "mpris:artUrl"
        private const val METADATA_TITLE: String = "xesam:title"
        private const val METADATA_ARTIST: String = "xesam:artist"
        private const val METADATA_ALBUM: String = "xesam:album"

        private const val PLAYBACK_STATUS_PLAYING: String = "Playing"
        private const val PLAYBACK_STATUS_PAUSED: String = "Paused"
        private const val PLAYBACK_STATUS_STOPPED: String = "Stopped"
        private const val LOOP_STATUS_NONE: String = "None"

        private val WRITABLE_PLAYER_PROPERTIES: Set<String> =
            setOf(
                PLAYER_PROPERTY_LOOP_STATUS,
                PLAYER_PROPERTY_RATE,
                PLAYER_PROPERTY_SHUFFLE,
                PLAYER_PROPERTY_VOLUME,
            )

        private val SUPPORTED_MIME_TYPES: List<String> =
            listOf(
                "audio/aac",
                "audio/flac",
                "audio/mpeg",
                "audio/ogg",
                "audio/wav",
                "audio/x-flac",
            )
    }
}

@DBusInterfaceName("org.mpris.MediaPlayer2")
internal interface MprisRoot : DBusInterface {
    fun Raise()
    fun Quit()
}

@DBusInterfaceName("org.mpris.MediaPlayer2.Player")
internal interface MprisPlayer : DBusInterface {
    fun Next()
    fun Previous()
    fun Pause()
    fun PlayPause()
    fun Stop()
    fun Play()
    fun Seek(offset: Long)
    fun SetPosition(
        trackId: DBusPath,
        position: Long,
    )

    fun OpenUri(uri: String)
}
