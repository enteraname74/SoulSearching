package com.github.enteraname74.soulsearching.features.filemanager.musicfetching

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


    private data class AlbumInformation(
        val name: String,
        val artist: String,
    )

    private var musicsByPath: HashMap<String, Music> = hashMapOf()
    private var artistsByName: HashMap<String, Artist> = hashMapOf()
    private var albumsByInfo: HashMap<AlbumInformation, Album> = hashMapOf()
    private val albumArtists: ArrayList<AlbumArtist> = arrayListOf()
    private val musicArtists: ArrayList<MusicArtist> = arrayListOf()
    private val musicAlbums: ArrayList<MusicAlbum> = arrayListOf()

    private suspend fun saveAll() {

        println("----")
        albumsByInfo.forEach { (info, album) ->
            println("KEY: ${info.artist} && ${info.name}, ALBUM: ${album.albumName}")
        }
        println("----")

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
        // We update the concerned cached artists
        artistsToDivide.forEach { artist ->

            // We then create new artists and link songs from the initial artist to it
            val musicIdsOfInitialArtist: List<UUID> = musicArtists
                .filter { it.artistId == artist.artistId }
                .map { it.musicId }

            val albumIdsOfInitialArtist: List<UUID> = albumArtists
                .filter { it.artistId == artist.artistId }
                .map { it.albumId }

            val allArtists: List<String> = artist.getMultipleArtists()
            allArtists.forEach { name ->
                val alreadySavedArtist: Artist? = artistsByName[name]
                val artistId: UUID = if (alreadySavedArtist == null) {
                    val id = UUID.randomUUID()
                    artistsByName[name] = Artist(
                        artistId = id,
                        artistName = name,
                    )
                    id
                } else {
                    alreadySavedArtist.artistId
                }

                musicIdsOfInitialArtist.forEach { musicId ->
                    musicArtists.add(
                        MusicArtist(
                            musicId = musicId,
                            artistId = artistId,
                        )
                    )
                }
                albumIdsOfInitialArtist.forEach { albumId ->
                    albumArtists.add(
                        AlbumArtist(
                            albumId = albumId,
                            artistId = artistId,
                        )
                    )
                }
            }

            /*
            For a song with multiple artists, its albums should be linked to the first artist.
            If there is already an existing album, with the same name and artist,
            we delete the album of the multiple artists and link its songs to the found one.
             */
            val albumsOfMultipleArtist: List<Album> = albumsByInfo.filter { (key, _) ->
                key.artist == artist.artistName
            }.values.toList()

            println("For artist: ${artist.artistName}, got albums: $albumsOfMultipleArtist")

            albumsOfMultipleArtist.forEach { album ->
                /*
                If we found an existing album with a single artist as the first one of the multiple artist,
                we link the songs of the current album to it.
                Else, we create it.
                 */
                val albumKey =  AlbumInformation(
                    name = album.albumName,
                    artist = allArtists.first(),
                )
                val albumWithSingleArtist: Album? = albumsByInfo[albumKey]
                if (albumWithSingleArtist != null) {
                    println("For album with muliptle artists, found single one: $albumWithSingleArtist")
                    // We redirect the songs of the multiple artist album
                    val musicsIdsOfAlbumWithMultipleArtists = musicAlbums.filter { it.albumId == album.albumId }.map { it.musicId }
                    println("Musics ids of the multiple artists album to redirect to single music album (id ${album.albumId}): $musicsIdsOfAlbumWithMultipleArtists")
                    musicsIdsOfAlbumWithMultipleArtists.forEach { musicId ->
                        musicAlbums.add(
                            MusicAlbum(
                                musicId = musicId,
                                albumId = albumWithSingleArtist.albumId,
                            )
                        )
                    }
                    // We delete the multiple artists album
                    musicAlbums.removeIf { it.albumId == album.albumId }
                    albumArtists.removeIf { it.albumId == album.albumId }
                    albumsByInfo.remove(
                        AlbumInformation(
                            name = album.albumName,
                            artist = artist.artistName,
                        )
                    )
                }
            }

            // We need to delete the links and artist with the legacy information:
            artistsByName.remove(artist.artistName)
            musicIdsOfInitialArtist.forEach { musicId ->
                musicArtists.removeIf { it.musicId == musicId && it.artistId == artist.artistId }
            }
            albumIdsOfInitialArtist.forEach { albumId ->
                albumArtists.removeIf { it.albumId == albumId && it.artistId == artist.artistId }
            }
        }

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