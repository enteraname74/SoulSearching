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
import com.github.enteraname74.domain.util.CoverFileManager
import org.jaudiotagger.audio.AudioFileIO
import java.io.File
import java.util.UUID

/**
 * Utilities for fetching musics on current device.
 */
abstract class MusicFetcher(
    private val upsertAllArtistsUseCase: UpsertAllArtistsUseCase,
    private val upsertAllAlbumsUseCase: UpsertAllAlbumsUseCase,
    private val upsertAllMusicsUseCase: UpsertAllMusicsUseCase,
    private val upsertAllFoldersUseCase: UpsertAllFoldersUseCase,
    private val upsertAllMusicArtistsUseCase: UpsertAllMusicArtistsUseCase,
    private val upsertAllAlbumArtistUseCase: UpsertAllAlbumArtistUseCase,
    private val upsertAllMusicAlbumUseCase: UpsertAllMusicAlbumUseCase,
    private val coverFileManager: CoverFileManager,
) {
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
        updateProgress: (Float, String?) -> Unit,
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

    private fun getCoverFromPath(musicPath: String): ByteArray? =
        try {
            val audioFile = AudioFileIO.read(File(musicPath))
            audioFile.tag.firstArtwork.binaryData.takeUnless { it.isEmpty() }
        } catch (_: Exception) {
            null
        }

    private suspend fun saveCoverOfSong(musicPath: String): UUID? {
        val cover: ByteArray? = getCoverFromPath(musicPath = musicPath)
        val coverId: UUID? = cover?.let {data ->
            val id = UUID.randomUUID()
            coverFileManager.saveCover(
                id = id,
                data = data
            )
            id
        }

        return coverId
    }


    private fun createAlbumOfSong(
        music: Music,
        albumId: UUID,
        coverId: UUID?,
    ) {
        val albumToAdd = Album(
            coverId = coverId,
            albumId = albumId,
            albumName = music.album
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
        coverId: UUID?,
        artistId: UUID,
    ) {
        val artistToAdd = Artist(
            coverId = coverId,
            artistId = artistId,
            artistName = music.artist
        )
        artistsByName.put(
            key = artistToAdd.artistName,
            value = artistToAdd
        )
    }

    /**
     * Persist a music and its cover.
     */
    suspend fun addMusic(musicToAdd: Music) {
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
            val coverId: UUID? = saveCoverOfSong(musicPath = musicToAdd.path)

            createAlbumOfSong(
                music = musicToAdd,
                albumId = albumId,
                coverId = coverId
            )

            createArtistOfSong(
                music = musicToAdd,
                coverId = coverId,
                artistId = artistId,
            )

            albumArtists.add(
                AlbumArtist(
                    albumId = albumId,
                    artistId = artistId,
                )
            )
        } else {
            // We add, if possible, the cover art of the corresponding album to the music.
            if (correspondingAlbum.coverId != null && musicToAdd.initialCoverPath == null) {
                musicToAdd.coverId = correspondingAlbum.coverId
            }

            // In this case, the album has no cover, so we add the one from the music.
            if (correspondingAlbum.coverId == null && musicToAdd.initialCoverPath != null) {
                val coverId: UUID? = saveCoverOfSong(musicToAdd.path)

                albumsByInfo.put(
                    key = AlbumInformation(
                        name = correspondingAlbum.albumName,
                        artist = musicToAdd.artist,
                    ),
                    value = correspondingAlbum.copy(
                        coverId = coverId,
                    )
                )

                // If the artist has no cover, we can give it one:
                if (correspondingArtist.coverId == null) {
                    artistsByName.put(
                        key = correspondingArtist.artistName,
                        value = correspondingArtist.copy(
                            coverId = coverId,
                        )
                    )
                }
            }
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
    }
}