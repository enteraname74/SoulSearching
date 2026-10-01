package com.github.enteraname74.soulsearching.features.playback.mediasession

import android.net.Uri
import kotlin.uuid.Uuid

internal object AndroidAutoMediaIdsUtils {
    const val MUSIC_PREFIX: String = "soul_searching_music:"
    private const val ALBUM_PREFIX: String = "soul_searching_album:"
    private const val ARTIST_PREFIX: String = "soul_searching_artist:"
    private const val PLAYLIST_PREFIX: String = "soul_searching_playlist:"
    private const val FOLDER_PREFIX: String = "soul_searching_folder:"
    private const val ALBUM_MUSIC_PREFIX: String = "soul_searching_album_music:"
    private const val ARTIST_MUSIC_PREFIX: String = "soul_searching_artist_music:"
    private const val PLAYLIST_MUSIC_PREFIX: String = "soul_searching_playlist_music:"
    private const val FOLDER_MUSIC_PREFIX: String = "soul_searching_folder_music:"
    private const val CONTEXT_SEPARATOR: String = "|"

    fun forMusic(musicId: Uuid): String =
        "$MUSIC_PREFIX$musicId"

    fun forMusicFromAlbum(musicId: Uuid, albumId: Uuid): String =
        "$ALBUM_MUSIC_PREFIX$albumId$CONTEXT_SEPARATOR$musicId"

    fun forMusicFromArtist(musicId: Uuid, artistId: Uuid): String =
        "$ARTIST_MUSIC_PREFIX$artistId$CONTEXT_SEPARATOR$musicId"

    fun forMusicFromPlaylist(musicId: Uuid, playlistId: Uuid): String =
        "$PLAYLIST_MUSIC_PREFIX$playlistId$CONTEXT_SEPARATOR$musicId"

    fun forMusicFromFolder(musicId: Uuid, folder: String): String =
        "$FOLDER_MUSIC_PREFIX${Uri.encode(folder)}$CONTEXT_SEPARATOR$musicId"

    fun forAlbum(albumId: Uuid): String =
        "$ALBUM_PREFIX$albumId"

    fun forArtist(artistId: Uuid): String =
        "$ARTIST_PREFIX$artistId"

    fun forPlaylist(playlistId: Uuid): String =
        "$PLAYLIST_PREFIX$playlistId"

    fun forFolder(folder: String): String =
        "$FOLDER_PREFIX${Uri.encode(folder)}"

    fun musicRequestFrom(mediaId: String): AndroidAutoMusicRequest? =
        uuidFrom(mediaId = mediaId, prefix = MUSIC_PREFIX)?.let { musicId ->
            AndroidAutoMusicRequest(
                musicId = musicId,
                context = AndroidAutoPlaybackContext.AllSongs,
            )
        }
            ?: contextualMusicRequestFrom(
                mediaId = mediaId,
                prefix = ALBUM_MUSIC_PREFIX,
            ) { albumId, musicId ->
                AndroidAutoMusicRequest(
                    musicId = musicId,
                    context = AndroidAutoPlaybackContext.Album(albumId),
                )
            }
            ?: contextualMusicRequestFrom(
                mediaId = mediaId,
                prefix = ARTIST_MUSIC_PREFIX,
            ) { artistId, musicId ->
                AndroidAutoMusicRequest(
                    musicId = musicId,
                    context = AndroidAutoPlaybackContext.Artist(artistId),
                )
            }
            ?: contextualMusicRequestFrom(
                mediaId = mediaId,
                prefix = PLAYLIST_MUSIC_PREFIX,
            ) { playlistId, musicId ->
                AndroidAutoMusicRequest(
                    musicId = musicId,
                    context = AndroidAutoPlaybackContext.Playlist(playlistId),
                )
            }
            ?: folderMusicRequestFrom(mediaId)

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

    private fun contextualMusicRequestFrom(
        mediaId: String,
        prefix: String,
        buildRequest: (contextId: Uuid, musicId: Uuid) -> AndroidAutoMusicRequest,
    ): AndroidAutoMusicRequest? {
        val parts = mediaId
            .takeIf { it.startsWith(prefix) }
            ?.removePrefix(prefix)
            ?.split(CONTEXT_SEPARATOR, limit = 2)
            ?.takeIf { it.size == 2 }
            ?: return null

        val contextId = runCatching { Uuid.parse(parts[0]) }.getOrNull() ?: return null
        val musicId = runCatching { Uuid.parse(parts[1]) }.getOrNull() ?: return null

        return buildRequest(contextId, musicId)
    }

    private fun folderMusicRequestFrom(mediaId: String): AndroidAutoMusicRequest? {
        val parts = mediaId
            .takeIf { it.startsWith(FOLDER_MUSIC_PREFIX) }
            ?.removePrefix(FOLDER_MUSIC_PREFIX)
            ?.split(CONTEXT_SEPARATOR, limit = 2)
            ?.takeIf { it.size == 2 }
            ?: return null

        val folder = Uri.decode(parts[0])
        val musicId = runCatching { Uuid.parse(parts[1]) }.getOrNull() ?: return null

        return AndroidAutoMusicRequest(
            musicId = musicId,
            context = AndroidAutoPlaybackContext.Folder(folder),
        )
    }
}

internal data class AndroidAutoMusicRequest(
    val musicId: Uuid,
    val context: AndroidAutoPlaybackContext,
)

internal sealed interface AndroidAutoPlaybackContext {
    data object AllSongs : AndroidAutoPlaybackContext
    data class Album(val albumId: Uuid) : AndroidAutoPlaybackContext
    data class Artist(val artistId: Uuid) : AndroidAutoPlaybackContext
    data class Playlist(val playlistId: Uuid) : AndroidAutoPlaybackContext
    data class Folder(val folder: String) : AndroidAutoPlaybackContext
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
