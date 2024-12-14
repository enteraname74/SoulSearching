package com.github.enteraname74.soulsearching.features.filemanager.musicfetching

import com.github.enteraname74.domain.feature.multipleartistsmanager.MultipleArtistManager
import com.github.enteraname74.domain.feature.multipleartistsmanager.MultipleArtistManagerCache
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


    private var musicsByPath: HashMap<String, Music> = hashMapOf()
    private var artistsByName: HashMap<String, Artist> = hashMapOf()
    private var albumsByInfo: HashMap<AlbumInformation, Album> = hashMapOf()
    private val albumArtists: ArrayList<AlbumArtist> = arrayListOf()
    private val musicArtists: ArrayList<MusicArtist> = arrayListOf()
    private val musicAlbums: ArrayList<MusicAlbum> = arrayListOf()

    private val multipleArtistManager: MultipleArtistManager = object : MultipleArtistManager() {
        override suspend fun getAlbumsOfMultipleArtist(artistName: String): List<Album> =
            albumsByInfo.filter { (key, _) -> key.artist == artistName }.values.toList()

        override suspend fun getArtistFromName(artistName: String): Artist? =
            artistsByName[artistName]

        override suspend fun createNewArtist(artistName: String): Artist {
            val newArtist = Artist(
                artistId = UUID.randomUUID(),
                artistName = artistName,
            )
            artistsByName[artistName] = newArtist
            return newArtist
        }

        override suspend fun deleteArtist(
            artist: Artist,
            musicIdsOfInitialArtist: List<UUID>,
            albumIdsOfInitialArtist: List<UUID>,
        ) {
            artistsByName.remove(artist.artistName)
            musicIdsOfInitialArtist.forEach { musicId ->
                musicArtists.removeIf { it.musicId == musicId && it.artistId == artist.artistId }
            }
            albumIdsOfInitialArtist.forEach { albumId ->
                albumArtists.removeIf { it.albumId == albumId && it.artistId == artist.artistId }
            }
        }

        override suspend fun getMusicIdsOfArtist(artistId: UUID): List<UUID> =
            musicArtists
                .filter { it.artistId == artistId }
                .map { it.musicId }

        override suspend fun getAlbumIdsOfArtist(artistId: UUID): List<UUID> =
            albumArtists
                .filter { it.artistId == artistId }
                .map { it.albumId }

        override suspend fun linkMusicToArtist(musicId: UUID, artistId: UUID) {
            musicArtists.add(
                MusicArtist(
                    musicId = musicId,
                    artistId = artistId,
                )
            )
        }

        override suspend fun linkAlbumToArtist(albumId: UUID, artistId: UUID) {
            albumArtists.add(
                AlbumArtist(
                    albumId = albumId,
                    artistId = artistId,
                )
            )
        }

        override suspend fun getExistingAlbumOfFirstArtist(albumName: String, firstArtistName: String): Album? {
            val albumKey = AlbumInformation(
                name = albumName,
                artist = firstArtistName,
            )
            return albumsByInfo[albumKey]
        }

        override suspend fun moveSongsOfAlbum(
            fromAlbum: Album,
            toAlbum: Album,
            multipleArtistName: String,
        ) {
            // We redirect the songs of the multiple artist album
            val musicsIdsOfAlbumWithMultipleArtists =
                musicAlbums.filter { it.albumId == fromAlbum.albumId }.map { it.musicId }
            musicsIdsOfAlbumWithMultipleArtists.forEach { musicId ->
                musicAlbums.add(
                    MusicAlbum(
                        musicId = musicId,
                        albumId = toAlbum.albumId,
                    )
                )
            }
            // We delete the multiple artists album
            musicAlbums.removeIf { it.albumId == fromAlbum.albumId }
            albumArtists.removeIf { it.albumId == fromAlbum.albumId }
            albumsByInfo.remove(
                AlbumInformation(
                    name = fromAlbum.albumName,
                    artist = multipleArtistName,
                )
            )
        }
    }

    private suspend fun saveAll() {
        upsertAllArtistsUseCase(artistsByName.values.toList())
        upsertAllAlbumsUseCase(albumsByInfo.values.toList())
        upsertAllMusicsUseCase(musicsByPath.values.toList())
        upsertAllAlbumArtistUseCase(albumArtists)
        upsertAllMusicAlbumUseCase(musicAlbums)
        upsertAllMusicArtistsUseCase(musicArtists)
        upsertAllFoldersUseCase(
            musicsByPath.values.map { Folder(folderPath = it.folder) }.distinctBy { it.folderPath }
        )

        musicsByPath.clear()
        artistsByName.clear()
        albumsByInfo.clear()
        albumArtists.clear()
        musicArtists.clear()
        musicAlbums.clear()

        settings.set(
            SoulSearchingSettingsKeys.HAS_MUSICS_BEEN_FETCHED_KEY.key,
            true
        )
    }

    fun getPotentialMultipleArtist(): List<Artist> =
        artistsByName.values.filter { it.isComposedOfMultipleArtists() }

    /**
     * If songs with multiple artists are found, we do not save the songs directly.
     */
    suspend fun saveAllWithMultipleArtistsCheck(): Boolean =
        if (artistsByName.values.any { it.isComposedOfMultipleArtists() }) {
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
        musicsByPath = getAllMusicUseCase().first().associateBy { it.path } as HashMap<String, Music>
        artistsByName = getAllArtistsUseCase().first().associateBy { it.artistName } as HashMap<String, Artist>
        albumsByInfo = getAllAlbumsWithArtistUseCase().first().associate {
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
        albumsByInfo[AlbumInformation(
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
        artistsByName[artistToAdd.artistName] = artistToAdd
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
        if (musicsByPath[musicToAdd.path] != null) return

        val correspondingArtist: Artist? = artistsByName[musicToAdd.artist]
        val correspondingAlbum: Album? = correspondingArtist?.let { artist ->
            val info = AlbumInformation(
                name = musicToAdd.album,
                artist = artist.artistName,
            )
            albumsByInfo[info]
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

            albumArtists.add(
                AlbumArtist(
                    albumId = albumId,
                    artistId = artistId,
                )
            )
        }

        musicsByPath[musicToAdd.path] = musicToAdd
        musicAlbums.add(
            MusicAlbum(
                musicId = musicToAdd.musicId,
                albumId = albumId,
            )
        )
        musicArtists.add(
            MusicArtist(
                musicId = musicToAdd.musicId,
                artistId = artistId,
            )
        )
        onSongSaved()
    }
}