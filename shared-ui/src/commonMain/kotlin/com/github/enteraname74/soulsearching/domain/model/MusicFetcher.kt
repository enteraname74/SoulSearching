package com.github.enteraname74.soulsearching.domain.model

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumArtist
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicAlbum
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.usecase.album.UpsertAllAlbumsUseCase
import com.github.enteraname74.domain.usecase.albumartist.UpsertAllAlbumArtistUseCase
import com.github.enteraname74.domain.usecase.artist.UpsertAllArtistsUseCase
import com.github.enteraname74.domain.usecase.folder.UpsertAllFoldersUseCase
import com.github.enteraname74.domain.usecase.music.UpsertAllMusicsUseCase
import com.github.enteraname74.domain.usecase.musicalbum.UpsertAllMusicAlbumUseCase
import com.github.enteraname74.domain.usecase.musicartist.UpsertAllMusicArtistsUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

/**
 * Utilities for fetching musics on current device.
 */
abstract class MusicFetcher: KoinComponent {
    private val upsertAllArtistsUseCase: UpsertAllArtistsUseCase by inject()
    private val upsertAllAlbumsUseCase: UpsertAllAlbumsUseCase by inject()
    private val upsertAllMusicsUseCase: UpsertAllMusicsUseCase by inject()
    private val upsertAllFoldersUseCase: UpsertAllFoldersUseCase by inject()
    private val upsertAllMusicArtistsUseCase: UpsertAllMusicArtistsUseCase by inject()
    private val upsertAllAlbumArtistUseCase: UpsertAllAlbumArtistUseCase by inject()
    private val upsertAllMusicAlbumUseCase: UpsertAllMusicAlbumUseCase by inject()

    /**
     * Fetch all musics on the device.
     */
    abstract suspend fun fetchMusics(
        updateProgress: (Float, String?) -> Unit,
        finishAction: () -> Unit
    )

    /**
     * Fetch musics from specified folders on the device.
     */
    abstract suspend fun fetchMusicsFromSelectedFolders(
        alreadyPresentMusicsPaths: List<String>,
        hiddenFoldersPaths: List<String>
    ): ArrayList<SelectableMusicItem>


    private data class AlbumInformation(
        val name: String,
        val artist: String,
    )

    private val musicsByPath: HashMap<String, Music> = hashMapOf()
    private val artistsByName: HashMap<String, Artist> = hashMapOf()
    private val albumsByInfo: HashMap<AlbumInformation, Album> = hashMapOf()
    private val albumArtists: ArrayList<AlbumArtist> = arrayListOf()
    private val musicArtists: ArrayList<MusicArtist> = arrayListOf()
    private val musicAlbums: ArrayList<MusicAlbum> = arrayListOf()

    suspend fun saveAll() {
        upsertAllArtistsUseCase(artistsByName.values.toList())
        upsertAllAlbumsUseCase(albumsByInfo.values.toList())
        upsertAllMusicsUseCase(musicsByPath.values.toList())
        upsertAllAlbumArtistUseCase(albumArtists)
        upsertAllMusicAlbumUseCase(musicAlbums)
        upsertAllMusicArtistsUseCase(musicArtists)
        upsertAllFoldersUseCase(
            musicsByPath.values.map { Folder(folderPath = it.folder) }
        )

        musicsByPath.clear()
        artistsByName.clear()
        albumsByInfo.clear()
        albumArtists.clear()
        musicArtists.clear()
        musicAlbums.clear()
    }


    private fun createAlbumOfSong(
        music: Music,
        albumId: UUID,
    ) {
        val albumToAdd = Album(
            albumId = albumId,
            albumName = music.album,
        )
        albumsByInfo.put(
            key = AlbumInformation(
                name = albumToAdd.albumName,
                artist = music.artist
            ),
            value = albumToAdd,
        )
    }


    private fun createArtistOfSong(
        music: Music,
        artistId: UUID,
    ) {
        val artistToAdd = Artist(
            artistId = artistId,
            artistName = music.artist
        )
        artistsByName.put(
            key = artistToAdd.artistName,
            value = artistToAdd
        )
    }

    suspend fun saveAllMusics(
        musics: List<Music>,
        onSongSaved: (progress: Float) -> Unit,
    ) {
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
        saveAll()
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

        musicsByPath.put(
            key = musicToAdd.path,
            value = musicToAdd,
        )
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