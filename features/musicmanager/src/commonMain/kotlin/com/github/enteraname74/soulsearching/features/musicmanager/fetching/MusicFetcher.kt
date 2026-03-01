package com.github.enteraname74.soulsearching.features.musicmanager.fetching

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.features.musicmanager.domain.OptimizedCachedData
import org.koin.core.component.KoinComponent

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

    suspend fun cacheSelectedMusics(
        musics: List<Music>,
        onSongSaved: (progress: Float) -> Unit,
    ): List<Music> {
        optimizedCachedData = OptimizedCachedData.fromDb()
        val musicsToSave: MutableSet<Music> = mutableSetOf()
        musics.forEachIndexed { index, music ->
            cacheMusic(
                musicToAdd = music,
                onSongSaved = { music ->
                    onSongSaved((index.toFloat() / musics.size))
                    musicsToSave.add(music)
                },
            )
        }

        return musicsToSave.toList()
    }

    /**
     * Cache a music to be saved later.
     */
    protected fun cacheMusic(
        musicToAdd: Music,
        onSongSaved: (Music) -> Unit = {},
    ) {
        // If the song has already been saved once, we do nothing.
        if (optimizedCachedData.musicsByPath[musicToAdd.path] != null) return

        /*
        We updated the list of artist of the music to check if an artist might already exist.
        If that is the case, we replace the musicToAdd's artist with the existing one to avoid duplicates of a same artist.
         */
        val updatedListOfArtist: List<Artist> = musicToAdd.artists.map { artist ->
            val existingArtist =
                optimizedCachedData.musicsByPath.values.firstNotNullOfOrNull { music ->
                    music.artists.find { it.artistName == artist.artistName }
                }
            existingArtist ?: artist
        }
        val updatedAlbum: Album = optimizedCachedData.musicsByPath.values.find { music ->
            music.album.albumName == musicToAdd.album.albumName
                    && music.album.artist.artistName == musicToAdd.album.artist.artistName
        }?.album ?: musicToAdd.album.copy(artist = updatedListOfArtist.first())

        /*
        We add the new music to the optimized cached data,
        as we may use information from this music for the following songs to save (artist, albums,...)
        to ensure no duplicate are created (artists, albums,...)
         */
        val fixedMusic = musicToAdd.copy(
            album = updatedAlbum,
            artists = updatedListOfArtist,
        )
        optimizedCachedData.musicsByPath[fixedMusic.path] = fixedMusic

        onSongSaved(fixedMusic)
    }
}