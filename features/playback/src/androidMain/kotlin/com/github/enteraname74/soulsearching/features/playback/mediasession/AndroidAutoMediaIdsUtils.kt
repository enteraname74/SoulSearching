package com.github.enteraname74.soulsearching.features.playback.mediasession

import android.net.Uri
import kotlin.uuid.Uuid

internal object AndroidAutoMediaIdsUtils {
    const val MUSIC_PREFIX: String = "soul_searching_music:"
    private const val ALBUM_PREFIX: String = "soul_searching_album:"
    private const val ARTIST_PREFIX: String = "soul_searching_artist:"
    private const val PLAYLIST_PREFIX: String = "soul_searching_playlist:"
    private const val FOLDER_PREFIX: String = "soul_searching_folder:"

    fun forMusic(musicId: Uuid): String =
        "$MUSIC_PREFIX$musicId"

    fun forAlbum(albumId: Uuid): String =
        "$ALBUM_PREFIX$albumId"

    fun forArtist(artistId: Uuid): String =
        "$ARTIST_PREFIX$artistId"

    fun forPlaylist(playlistId: Uuid): String =
        "$PLAYLIST_PREFIX$playlistId"

    fun forFolder(folder: String): String =
        "$FOLDER_PREFIX${Uri.encode(folder)}"

    fun musicIdFrom(mediaId: String): Uuid? =
        uuidFrom(mediaId = mediaId, prefix = MUSIC_PREFIX)

    fun albumIdFrom(mediaId: String): Uuid? =
        uuidFrom(mediaId = mediaId, prefix = ALBUM_PREFIX)

    fun artistIdFrom(mediaId: String): Uuid? =
        uuidFrom(mediaId = mediaId, prefix = ARTIST_PREFIX)

    fun playlistIdFrom(mediaId: String): Uuid? =
        uuidFrom(mediaId = mediaId, prefix = PLAYLIST_PREFIX)

    fun folderFrom(mediaId: String): String? =
        mediaId
            .takeIf { it.startsWith(FOLDER_PREFIX) }
            ?.removePrefix(FOLDER_PREFIX)
            ?.let(Uri::decode)

    private fun uuidFrom(mediaId: String, prefix: String): Uuid? =
        mediaId
            .takeIf { it.startsWith(prefix) }
            ?.removePrefix(prefix)
            ?.let { runCatching { Uuid.parse(it) }.getOrNull() }
}

enum class AndroidAutoMediaIds(val value: String) {
    Root("root"),
    AllSongs("all"),
    AllAlbums("albums"),
    AllArtists("artists"),
    AllPlaylists("playlists"),
    AllFolders("folders");

    companion object {
        fun fromString(string: String): AndroidAutoMediaIds? =
            entries.find { it.value == string }
    }
}
