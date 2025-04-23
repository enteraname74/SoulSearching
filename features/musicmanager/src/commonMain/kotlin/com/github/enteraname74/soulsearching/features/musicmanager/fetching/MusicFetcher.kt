package com.github.enteraname74.soulsearching.features.musicmanager.fetching

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.soulsearching.features.musicmanager.domain.AlbumInformation
import com.github.enteraname74.soulsearching.features.musicmanager.domain.OptimizedCachedData
import org.koin.core.component.KoinComponent
import java.util.*

/**
 * Utilities for fetching musics on current device.
 */
abstract class MusicFetcher : KoinComponent {
    /**
     * Fetch all musics on the device.
     */
    abstract suspend fun fetchMusics(
        updateProgress: (Float, String?) -> Unit,
    )

    /**
     * Fetch musics from specified folders on the device.
     */
    abstract suspend fun fetchMusicsFromSelectedFolders(
        alreadyPresentMusicsPaths: List<String>,
        hiddenFoldersPaths: List<String>
    ): List<SelectableMusicItem>

    var optimizedCachedData = OptimizedCachedData()
        protected set

    private fun createAlbumOfSong(
        music: Music,
        albumId: UUID,
    ) {
        val albumToAdd = Album(
            albumId = albumId,
            albumName = music.album,
        )
        optimizedCachedData.albumsByInfo[AlbumInformation(
            name = albumToAdd.albumName,
            artist = music.artistOfAlbum,
        )] = albumToAdd
    }


    private fun createArtist(
        name: String,
        id: UUID,
    ) {
        val artistToAdd = Artist(
            artistId = id,
            artistName = name,
        )
        optimizedCachedData.artistsByName[artistToAdd.artistName] = artistToAdd
    }

    suspend fun cacheSelectedMusics(
        musics: List<Music>,
        onSongSaved: (progress: Float) -> Unit,
    ) {
        optimizedCachedData = OptimizedCachedData.fromDb()
        musics.forEachIndexed { index, music ->
            cacheMusic(
                musicToAdd = music,
                onSongSaved = {
                    onSongSaved(
                        (index.toFloat() / musics.size)
                    )
                },
            )
        }
    }

    /**
     * Cache a music to be saved later.
     */
    protected fun cacheMusic(
        musicToAdd: Music,
        onSongSaved: () -> Unit = {},
    ) {
        // If the song has already been saved once, we do nothing.
        if (optimizedCachedData.musicsByPath[musicToAdd.path] != null) return

        val correspondingArtist: Artist? = optimizedCachedData.artistsByName[musicToAdd.artist]
        val correspondingAlbumArtist: Artist? = optimizedCachedData.artistsByName[musicToAdd.artistOfAlbum]

        val correspondingAlbum: Album? = correspondingAlbumArtist?.let { artist ->
            val info = AlbumInformation(
                name = musicToAdd.album,
                artist = artist.artistName,
            )
            optimizedCachedData.albumsByInfo[info]
        }

        val albumId = correspondingAlbum?.albumId ?: UUID.randomUUID()
        val artistId = correspondingArtist?.artistId ?: UUID.randomUUID()
        val albumArtistId: UUID? = if (musicToAdd.hasDifferentAlbumArtist()) {
            correspondingAlbumArtist?.artistId ?: UUID.randomUUID()
        } else {
            null
        }

        if (correspondingArtist == null) {
            createArtist(
                name = musicToAdd.artist,
                id = artistId,
            )
        }

        if (correspondingAlbumArtist == null && albumArtistId != null) {
            musicToAdd.albumArtist?.let { albumArtistName ->
                createArtist(
                    name = albumArtistName,
                    id = albumArtistId,
                )
            }
        }

        if (correspondingAlbum == null) {

            createAlbumOfSong(
                music = musicToAdd,
                albumId = albumId,
            )

            optimizedCachedData.albumArtists.add(
                AlbumArtist(
                    albumId = albumId,
                    artistId = albumArtistId ?: artistId,
                )
            )
        }

        optimizedCachedData.musicsByPath[musicToAdd.path] = musicToAdd
        optimizedCachedData.musicAlbums.add(
            MusicAlbum(
                musicId = musicToAdd.musicId,
                albumId = albumId,
            )
        )
        optimizedCachedData.musicArtists.add(
            MusicArtist(
                musicId = musicToAdd.musicId,
                artistId = artistId,
            )
        )
        albumArtistId?.let { safeId ->
            optimizedCachedData.musicArtists.add(
                MusicArtist(
                    musicId = musicToAdd.musicId,
                    artistId = safeId,
                )
            )
        }
        onSongSaved()
    }
}