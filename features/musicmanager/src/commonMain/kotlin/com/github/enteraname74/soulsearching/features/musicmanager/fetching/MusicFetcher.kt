package com.github.enteraname74.soulsearching.features.musicmanager.fetching

import com.github.enteraname74.soulsearching.features.musicmanager.multipleartists.MultipleArtistManager
import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.album.GetAllAlbumsWithArtistUseCase
import com.github.enteraname74.domain.usecase.album.UpsertAllAlbumsUseCase
import com.github.enteraname74.domain.usecase.albumartist.UpsertAllAlbumArtistUseCase
import com.github.enteraname74.domain.usecase.artist.GetAllArtistsUseCase
import com.github.enteraname74.domain.usecase.artist.UpsertAllArtistsUseCase
import com.github.enteraname74.domain.usecase.folder.UpsertAllFoldersUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicUseCase
import com.github.enteraname74.domain.usecase.music.UpsertAllMusicsUseCase
import com.github.enteraname74.domain.usecase.musicalbum.UpsertAllMusicAlbumUseCase
import com.github.enteraname74.domain.usecase.musicartist.UpsertAllMusicArtistsUseCase
import com.github.enteraname74.soulsearching.features.musicmanager.domain.AlbumInformation
import com.github.enteraname74.soulsearching.features.musicmanager.domain.OptimizedFetchData
import com.github.enteraname74.soulsearching.features.musicmanager.multipleartists.FetchAllMultipleArtistManagerImpl
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

/**
 * Utilities for fetching musics on current device.
 */
abstract class MusicFetcher : KoinComponent {
    private val upsertAllArtistsUseCase: UpsertAllArtistsUseCase by inject()
    private val upsertAllAlbumsUseCase: UpsertAllAlbumsUseCase by inject()
    private val upsertAllMusicsUseCase: UpsertAllMusicsUseCase by inject()
    private val upsertAllFoldersUseCase: UpsertAllFoldersUseCase by inject()
    private val upsertAllMusicArtistsUseCase: UpsertAllMusicArtistsUseCase by inject()
    private val upsertAllAlbumArtistUseCase: UpsertAllAlbumArtistUseCase by inject()
    private val upsertAllMusicAlbumUseCase: UpsertAllMusicAlbumUseCase by inject()

    private val getAllMusicUseCase: GetAllMusicUseCase by inject()
    private val getAllArtistsUseCase: GetAllArtistsUseCase by inject()
    private val getAllAlbumsWithArtistUseCase: GetAllAlbumsWithArtistUseCase by inject()

    private val settings: SoulSearchingSettings by inject()

    /**
     * Fetch all musics on the device.
     */
    abstract suspend fun fetchMusics(
        updateProgress: (Float, String?) -> Unit,
    ): Boolean

    /**
     * Fetch musics from specified folders on the device.
     */
    abstract suspend fun fetchMusicsFromSelectedFolders(
        alreadyPresentMusicsPaths: List<String>,
        hiddenFoldersPaths: List<String>
    ): List<SelectableMusicItem>


    private val optimizedFetchData = OptimizedFetchData()

    private val multipleArtistManager: MultipleArtistManager = FetchAllMultipleArtistManagerImpl(
        optimizedFetchData = optimizedFetchData,
    )

    private suspend fun saveAll() {
        upsertAllArtistsUseCase(optimizedFetchData.artistsByName.values.toList())
        upsertAllAlbumsUseCase(optimizedFetchData.albumsByInfo.values.toList())
        upsertAllMusicsUseCase(optimizedFetchData.musicsByPath.values.toList())
        upsertAllAlbumArtistUseCase(optimizedFetchData.albumArtists)
        upsertAllMusicAlbumUseCase(optimizedFetchData.musicAlbums)
        upsertAllMusicArtistsUseCase(optimizedFetchData.musicArtists)
        upsertAllFoldersUseCase(
            optimizedFetchData.musicsByPath.values.map { Folder(folderPath = it.folder) }.distinctBy { it.folderPath }
        )

        optimizedFetchData.clear()

        settings.set(
            SoulSearchingSettingsKeys.HAS_MUSICS_BEEN_FETCHED_KEY.key,
            true
        )
    }

    fun getPotentialMultipleArtist(): List<Artist> =
        optimizedFetchData.artistsByName.values.filter { it.isComposedOfMultipleArtists() }

    /**
     * If songs with multiple artists are found, we do not save the songs directly.
     */
    suspend fun saveAllWithMultipleArtistsCheck(): Boolean =
        if (optimizedFetchData.artistsByName.values.any { it.isComposedOfMultipleArtists() }) {
            // There are songs in the list that may have multiple artists, we will need choice from user:
            false
        } else {
            saveAll()
            true
        }

    suspend fun saveAllWithMultipleArtists(
        artistsToDivide: List<Artist>
    ) {
        multipleArtistManager.handleMultipleArtists(artistsToDivide = artistsToDivide)
        saveAll()
    }

    suspend fun init() {
        optimizedFetchData.musicsByPath = getAllMusicUseCase().first().associateBy { it.path } as HashMap<String, Music>
        optimizedFetchData.artistsByName =
            getAllArtistsUseCase().first().associateBy { it.artistName } as HashMap<String, Artist>
        optimizedFetchData.albumsByInfo = getAllAlbumsWithArtistUseCase().first().associate {
            AlbumInformation(
                name = it.album.albumName,
                artist = it.artist?.artistName.orEmpty(),
            ) to it.album
        } as HashMap<AlbumInformation, Album>
    }

    private fun createAlbumOfSong(
        music: Music,
        albumId: UUID,
    ) {
        val albumToAdd = Album(
            albumId = albumId,
            albumName = music.album,
        )
        optimizedFetchData.albumsByInfo[AlbumInformation(
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
        optimizedFetchData.artistsByName[artistToAdd.artistName] = artistToAdd
    }

    suspend fun saveAllMusics(
        musics: List<Music>,
        onSongSaved: (progress: Float) -> Unit,
    ): Boolean {
        init()
        musics.forEachIndexed { index, music ->
            addMusic(
                musicToAdd = music,
                onSongSaved = {
                    onSongSaved(
                        (index.toFloat() / musics.size)
                    )
                },
            )
        }

        // If there is multiple artists in the given list of new songs, we need to let the user choose if he wants to split them.

        return saveAllWithMultipleArtistsCheck()
    }

    /**
     * Persist a music and its cover.
     */
    fun addMusic(
        musicToAdd: Music,
        onSongSaved: () -> Unit = {},
    ) {
        // If the song has already been saved once, we do nothing.
        if (optimizedFetchData.musicsByPath[musicToAdd.path] != null) return

        val correspondingArtist: Artist? = optimizedFetchData.artistsByName[musicToAdd.artist]
        val correspondingAlbum: Album? = correspondingArtist?.let { artist ->
            val info = AlbumInformation(
                name = musicToAdd.album,
                artist = artist.artistName,
            )
            optimizedFetchData.albumsByInfo[info]
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

            optimizedFetchData.albumArtists.add(
                AlbumArtist(
                    albumId = albumId,
                    artistId = artistId,
                )
            )
        }

        optimizedFetchData.musicsByPath[musicToAdd.path] = musicToAdd
        optimizedFetchData.musicAlbums.add(
            MusicAlbum(
                musicId = musicToAdd.musicId,
                albumId = albumId,
            )
        )
        optimizedFetchData.musicArtists.add(
            MusicArtist(
                musicId = musicToAdd.musicId,
                artistId = artistId,
            )
        )
        onSongSaved()
    }
}