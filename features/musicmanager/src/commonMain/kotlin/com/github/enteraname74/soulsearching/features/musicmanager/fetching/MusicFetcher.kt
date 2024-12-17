package com.github.enteraname74.soulsearching.features.musicmanager.fetching

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.usecase.album.GetAllAlbumsWithArtistUseCase
import com.github.enteraname74.domain.usecase.albumartist.GetAllAlbumArtistUseCase
import com.github.enteraname74.domain.usecase.artist.GetAllArtistsUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicUseCase
import com.github.enteraname74.domain.usecase.musicalbum.GetAllMusicAlbumUseCase
import com.github.enteraname74.domain.usecase.musicartist.GetAllMusicArtistUseCase
import com.github.enteraname74.soulsearching.features.musicmanager.domain.AlbumInformation
import com.github.enteraname74.soulsearching.features.musicmanager.domain.OptimizedCachedData
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

/**
 * Utilities for fetching musics on current device.
 */
abstract class MusicFetcher : KoinComponent {
    private val getAllMusicUseCase: GetAllMusicUseCase by inject()
    private val getAllArtistsUseCase: GetAllArtistsUseCase by inject()
    private val getAllAlbumsWithArtistUseCase: GetAllAlbumsWithArtistUseCase by inject()
    private val getAllMusicAlbumUseCase: GetAllMusicAlbumUseCase by inject()
    private val getAllMusicArtistUseCase: GetAllMusicArtistUseCase by inject()
    private val getAllAlbumArtistUseCase: GetAllAlbumArtistUseCase by inject()

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

    private suspend fun init() {
        optimizedCachedData.musicsByPath =
            getAllMusicUseCase().first().associateBy { it.path } as HashMap<String, Music>
        optimizedCachedData.artistsByName =
            getAllArtistsUseCase().first().associateBy { it.artistName } as HashMap<String, Artist>
        optimizedCachedData.albumsByInfo = getAllAlbumsWithArtistUseCase().first().associate {
            AlbumInformation(
                name = it.album.albumName,
                artist = it.artist?.artistName.orEmpty(),
            ) to it.album
        } as HashMap<AlbumInformation, Album>

        optimizedCachedData.albumArtists = ArrayList(getAllAlbumArtistUseCase())
        optimizedCachedData.musicArtists = ArrayList(getAllMusicArtistUseCase())
        optimizedCachedData.musicAlbums = ArrayList(getAllMusicAlbumUseCase())
    }

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
            artist = music.artist
        )] = albumToAdd
    }


    private fun createArtistOfSong(
        music: Music,
        artistId: UUID,
    ) {
        val artistToAdd = Artist(
            artistId = artistId,
            artistName = music.artist
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
        val correspondingAlbum: Album? = correspondingArtist?.let { artist ->
            val info = AlbumInformation(
                name = musicToAdd.album,
                artist = artist.artistName,
            )
            optimizedCachedData.albumsByInfo[info]
        }

        val albumId = correspondingAlbum?.albumId ?: UUID.randomUUID()
        val artistId = correspondingArtist?.artistId ?: UUID.randomUUID()

        if (correspondingAlbum == null) {

            createAlbumOfSong(
                music = musicToAdd,
                albumId = albumId,
            )

            if (correspondingArtist == null) {
                createArtistOfSong(
                    music = musicToAdd,
                    artistId = artistId,
                )
            }

            optimizedCachedData.albumArtists.add(
                AlbumArtist(
                    albumId = albumId,
                    artistId = artistId,
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
        onSongSaved()
    }
}