package com.github.enteraname74.domain.repository

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.datasource.AlbumArtistDataSource
import com.github.enteraname74.domain.datasource.AlbumDataSource
import com.github.enteraname74.domain.datasource.ArtistDataSource
import com.github.enteraname74.domain.datasource.FolderDataSource
import com.github.enteraname74.domain.datasource.ImageCoverDataSource
import com.github.enteraname74.domain.datasource.MusicAlbumDataSource
import com.github.enteraname74.domain.datasource.MusicArtistDataSource
import com.github.enteraname74.domain.datasource.MusicDataSource
import com.github.enteraname74.domain.datasource.MusicPlaylistDataSource
import com.github.enteraname74.domain.datasource.PlaylistDataSource
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumArtist
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicAlbum
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.MusicWithCover
import com.github.enteraname74.domain.util.CheckAndDeleteVerification
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository for handling Music related work.
 */
class MusicRepository(
    private val musicDataSource: MusicDataSource,
    private val albumDataSource: AlbumDataSource,
    private val artistDataSource: ArtistDataSource,
    private val albumArtistDataSource: AlbumArtistDataSource,
    private val musicAlbumDataSource: MusicAlbumDataSource,
    private val musicArtistDataSource: MusicArtistDataSource,
    private val imageCoverDataSource: ImageCoverDataSource,
    private val folderDataSource: FolderDataSource,
    private val playlistDataSource: PlaylistDataSource,
    private val musicPlaylistDataSource: MusicPlaylistDataSource,
    private val checkAndDeleteVerification: CheckAndDeleteVerification
) {
    /**
     * Tries to retrieve the corresponding album of a music and its artist.
     */
    private suspend fun getCorrespondingAlbumFromAlbumName(
        musicAlbum: String,
        albumArtist: Artist?
    ): Album? = if (albumArtist == null) {
        null
    } else {
        albumDataSource.getCorrespondingAlbum(
            albumName = musicAlbum,
            artistId = albumArtist.artistId
        )
    }

    /**
     * Tries to retrieve the corresponding album of a music.
     */
    private suspend fun getCorrespondingAlbumFromMusicId(musicId: UUID): Album? {
        val albumId = musicAlbumDataSource.getAlbumIdFromMusicId(
            musicId = musicId
        ) ?: return null
        return albumDataSource.getAlbumFromId(
            albumId = albumId
        )
    }

    /**
     * Tries to retrieve the corresponding artist of a music.
     */
    private suspend fun getCorrespondingArtistFromMusicId(musicId: UUID): Artist? {
        val artistId = musicArtistDataSource.getArtistIdFromMusicId(
            musicId = musicId
        ) ?: return null
        return artistDataSource.getArtistFromId(
            artistId = artistId
        )
    }

    /**
     * Check if a music has already been saved.
     */
    private suspend fun isMusicAlreadySaved(music: Music): Boolean =
        musicDataSource.getMusicFromPath(music.path) != null


    private suspend fun saveMusicAndCreateMissingArtistAndAlbum(
        music: Music,
        musicCover: ImageBitmap?,
        albumId: UUID,
        artistId: UUID
    ) {
        val coverId = UUID.randomUUID()
        if (musicCover != null) {
            music.coverId = coverId
            imageCoverDataSource.insertImageCover(
                ImageCover(
                    coverId = coverId,
                    cover = musicCover
                )
            )
        }

        albumDataSource.insertAlbum(
            Album(
                coverId = if (musicCover != null) coverId else null,
                albumId = albumId,
                albumName = music.album
            )
        )
        artistDataSource.insertArtist(
            Artist(
                coverId = if (musicCover != null) coverId else null,
                artistId = artistId,
                artistName = music.artist
            )
        )
        albumArtistDataSource.insertAlbumIntoArtist(
            AlbumArtist(
                albumId = albumId,
                artistId = artistId
            )
        )
    }

    /**
     * Save a music.
     */
    suspend fun save(musicWithCover: MusicWithCover) {
        val music = musicWithCover.music
        val musicCover = musicWithCover.cover?.cover

        // Si la musique a déjà été enregistrée, on ne fait rien :
        if (isMusicAlreadySaved(music = music)) return

        val correspondingArtist = artistDataSource.getArtistFromInfo(
            artistName = music.artist
        )
        // Si l'artiste existe, on regarde si on trouve un album correspondant :
        val correspondingAlbum = getCorrespondingAlbumFromAlbumName(
            musicAlbum = music.album,
            albumArtist = correspondingArtist
        )
        val albumId = correspondingAlbum?.albumId ?: UUID.randomUUID()
        val artistId = correspondingArtist?.artistId ?: UUID.randomUUID()

        if (correspondingAlbum == null) {
            saveMusicAndCreateMissingArtistAndAlbum(
                music = music,
                albumId = albumId,
                artistId = artistId,
                musicCover = musicCover
            )
        } else {
            // On ajoute si possible la couverture de l'album de la musique :
            val albumCover = if (correspondingAlbum.coverId != null) {
                imageCoverDataSource.getCoverOfElement(coverId = correspondingAlbum.coverId!!)
            } else {
                null
            }
            val shouldPutAlbumCoverWithMusic = (albumCover != null)
            val shouldUpdateArtistCover =
                (correspondingArtist?.coverId == null) && ((albumCover != null) || (musicCover != null))

            if (shouldPutAlbumCoverWithMusic) {
                music.coverId = albumCover?.coverId
            } else if (musicCover != null) {
                val coverId = UUID.randomUUID()
                music.coverId = coverId
                imageCoverDataSource.insertImageCover(
                    ImageCover(
                        coverId = coverId,
                        cover = musicCover
                    )
                )

                // Dans ce cas, l'album n'a pas d'image, on lui en ajoute une :
                albumDataSource.updateAlbumCover(
                    newCoverId = coverId,
                    albumId = correspondingAlbum.albumId
                )
            }

            if (shouldUpdateArtistCover) {
                val newArtistCover: UUID? = if (shouldPutAlbumCoverWithMusic) {
                    albumCover?.coverId
                } else {
                    music.coverId
                }
                if (correspondingArtist != null && newArtistCover != null) {
                    artistDataSource.updateArtistCover(
                        newCoverId = newArtistCover,
                        artistId = correspondingArtist.artistId
                    )
                }
            }
        }
        musicDataSource.insertMusic(music)
        folderDataSource.insertFolder(
            Folder(
                folderPath = music.folder
            )
        )
        musicAlbumDataSource.insertMusicIntoAlbum(
            MusicAlbum(
                musicId = music.musicId,
                albumId = albumId
            )
        )
        musicArtistDataSource.insertMusicIntoArtist(
            MusicArtist(
                musicId = music.musicId,
                artistId = artistId
            )
        )
    }

    /**
     * Delete a music from its id.
     */
    suspend fun delete(musicId: UUID) {
        val musicToRemove = musicDataSource.getMusicFromId(musicId = musicId)

        musicDataSource.deleteMusic(music = musicToRemove)

        val artist = getCorrespondingArtistFromMusicId(musicId = musicToRemove.musicId)
        val album = getCorrespondingAlbumFromMusicId(musicId = musicToRemove.musicId)
        album?.let { musicAlbum ->
            checkAndDeleteVerification.checkAndDeleteAlbum(albumToCheck = musicAlbum)
        }
        artist?.let { musicArtist ->
            checkAndDeleteVerification.checkAndDeleteArtist(artistToCheck = musicArtist)
        }

    }

    /**
     * Update a music.
     *
     * @param legacyMusic the information of the previous version of the music to update
     * (used for comparison between the legacy and new music information for better updating).
     * @param newMusicInformation the new music information to save.
     */
    suspend fun update(
        legacyMusic: Music,
        newMusicInformation: Music
    ) {
        if (legacyMusic.artist != newMusicInformation.artist) {
            val legacyArtist = artistDataSource.getArtistFromInfo(artistName = legacyMusic.artist)
            var newArtist =
                artistDataSource.getArtistFromInfo(artistName = newMusicInformation.artist.trim())

            // It's a new artist, we need to create it.
            if (newArtist == null) {
                newArtist = Artist(
                    artistName = newMusicInformation.artist,
                    coverId = newMusicInformation.coverId
                )
                artistDataSource.insertArtist(
                    newArtist
                )
            }

            // On met les infos de la musique à jour :
            musicArtistDataSource.updateArtistOfMusic(
                musicId = legacyMusic.musicId,
                newArtistId = newArtist.artistId
            )

            updateAlbumOfMusic(
                artistId = newArtist.artistId,
                legacyMusic = legacyMusic,
                newAlbumName = newMusicInformation.album,
            )
            legacyArtist?.let { artist ->
                checkAndDeleteVerification.checkAndDeleteArtist(artistToCheck = artist)
            }
        } else if (legacyMusic.album != newMusicInformation.album) {
            val musicArtist = getCorrespondingArtistFromMusicId(musicId = legacyMusic.musicId)

            musicArtist?.let { artist ->
                updateAlbumOfMusic(
                    legacyMusic = legacyMusic,
                    newAlbumName = newMusicInformation.album,
                    artistId = artist.artistId
                )
            }
        }
        // On mets à jour la cover pour l'album et l'artiste :
        val artist = artistDataSource.getArtistFromInfo(
            newMusicInformation.artist
        )
        val album = albumDataSource.getCorrespondingAlbum(
            albumName = newMusicInformation.album,
            artistId = artist!!.artistId
        )
        // Si l'artiste n'a pas d'image, on lui donne la nouvelle cover
        if (artist.coverId == null) {
            newMusicInformation.coverId?.let { musicCoverId ->
                artistDataSource.updateArtistCover(musicCoverId, artist.artistId)
            }
        }
        // Faison de même pour l'album :
        if (album!!.coverId == null) {
            newMusicInformation.coverId?.let { musicCoverId ->
                albumDataSource.updateAlbumCover(musicCoverId, album.albumId)
            }
        }
        musicDataSource.insertMusic(newMusicInformation)
    }

    /**
     * Update the album of a music.
     * @param legacyMusic the legacy music information to update.
     * @param artistId the id of the music's artist.
     * @param newAlbumName the new album name of the music.
     */
    suspend fun updateAlbumOfMusic(
        legacyMusic: Music,
        artistId: UUID,
        newAlbumName: String
    ) {
        val legacyAlbum = getCorrespondingAlbumFromMusicId(musicId = legacyMusic.musicId)

        var newMusicAlbum = albumDataSource.getCorrespondingAlbum(
            albumName = newAlbumName,
            artistId = artistId
        )

        // If the album name is not corresponding to an existing album, we create it.
        if (newMusicAlbum == null) {
            newMusicAlbum = Album(
                albumName = newAlbumName,
                coverId = legacyMusic.coverId
            )
            albumDataSource.insertAlbum(album = newMusicAlbum)

            // We link the new album to the music's artist.
            albumArtistDataSource.insertAlbumIntoArtist(
                AlbumArtist(
                    albumId = newMusicAlbum.albumId,
                    artistId = artistId
                )
            )
        }

        // We update the album of the music.
        musicAlbumDataSource.updateAlbumOfMusic(
            musicId = legacyMusic.musicId,
            newAlbumId = newMusicAlbum.albumId
        )

        // We remove the legacy album if there is no music left in it.
        legacyAlbum?.let { album ->
            checkAndDeleteVerification.checkAndDeleteAlbum(albumToCheck = album)
        }
    }

    /**
     * Increment the total number of play of a music.
     */
    suspend fun updateNbPlayed(musicId: UUID) {
        val currentNbPlayed = musicDataSource.getNbPlayedOfMusic(musicId)
        musicDataSource.updateNbPlayed(
            newNbPlayed = currentNbPlayed + 1,
            musicId = musicId
        )
    }

    /**
     * Toggle the quick access state of a given music id.
     */
    suspend fun toggleQuickAccessState(musicId: UUID) {
        val legacyQuickAccessState =
            musicDataSource.getMusicFromId(musicId = musicId).isInQuickAccess

        musicDataSource.updateQuickAccessState(
            musicId = musicId,
            newQuickAccessState = !legacyQuickAccessState
        )
    }

    /**
     * Toggle the favorite state of a given music id.
     */
    suspend fun toggleFavoriteState(musicId: UUID) {
        val isInFavorite = musicDataSource.getMusicFromFavoritePlaylist(musicId) != null
        val playlistId = playlistDataSource.getFavoritePlaylist().playlistId
        if (isInFavorite) {
            musicPlaylistDataSource.deleteMusicFromPlaylist(
                musicId = musicId,
                playlistId = playlistId
            )
        } else {
            musicPlaylistDataSource.insertMusicIntoPlaylist(
                MusicPlaylist(
                    musicId = musicId,
                    playlistId = playlistId
                )
            )
        }
    }

    /**
     * Inserts or updates a Music.
     */
    suspend fun insertMusic(music: Music) = musicDataSource.insertMusic(
        music = music
    )

    /**
     * Deletes a Music.
     */
    suspend fun deleteMusic(music: Music) = musicDataSource.deleteMusic(
        music = music
    )

    /**
     * Remove a Music from an Album.
     */
    suspend fun deleteMusicFromAlbum(album: String, artist: String) =
        musicDataSource.deleteMusicFromAlbum(
            album = album,
            artist = artist
        )

    /**
     * Tries to retrieve a Music from its path.
     */
    suspend fun getMusicFromPath(musicPath: String): Music? = musicDataSource.getMusicFromPath(
        musicPath = musicPath
    )

    /**
     * Retrieve a music from its id.
     */
    suspend fun getMusicFromId(musicId: UUID): Music = musicDataSource.getMusicFromId(
        musicId = musicId
    )

    /**
     * Tries to retrieve a music from the favorite playlist.
     */
    suspend fun getMusicFromFavoritePlaylist(musicId: UUID): Music? =
        musicDataSource.getMusicFromFavoritePlaylist(
            musicId = musicId
        )

    /**
     * Retrieves all musics path.
     */
    suspend fun getAllMusicsPaths(): List<String> = musicDataSource.getAllMusicsPaths()

    /**
     * Retrieves a flow of all Music, sorted by name asc.
     */
    fun getAllMusicsSortByNameAscAsFlow(): Flow<List<Music>> =
        musicDataSource.getAllMusicsSortByNameAscAsFlow()

    /**
     * Retrieves a flow of all Music, sorted by name desc.
     */
    fun getAllMusicsSortByNameDescAsFlow(): Flow<List<Music>> =
        musicDataSource.getAllMusicsSortByNameDescAsFlow()

    /**
     * Retrieves a flow of all Music, sorted by added date asc.
     */
    fun getAllMusicsSortByAddedDateAscAsFlow(): Flow<List<Music>> =
        musicDataSource.getAllMusicsSortByAddedDateAscAsFlow()

    /**
     * Retrieves a flow of all Music, sorted by added date desc.
     */
    fun getAllMusicsSortByAddedDateDescAsFlow(): Flow<List<Music>> =
        musicDataSource.getAllMusicsSortByAddedDateDescAsFlow()

    /**
     * Retrieves a flow of all Music, sorted by nb played asc.
     */
    fun getAllMusicsSortByNbPlayedAscAsFlow(): Flow<List<Music>> =
        musicDataSource.getAllMusicsSortByNbPlayedAscAsFlow()

    /**
     * Retrieves a flow of all Music, sorted by nb played desc.
     */
    fun getAllMusicsSortByNbPlayedDescAsFlow(): Flow<List<Music>> =
        musicDataSource.getAllMusicsSortByNbPlayedDescAsFlow()

    /**
     * Retrieves a flow of all Music in the quick access.
     */
    fun getAllMusicsFromQuickAccessAsFlow(): Flow<List<Music>> =
        musicDataSource.getAllMusicsFromQuickAccessAsFlow()

    /**
     * Retrieves all musics of an Album.
     */
    suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music> =
        musicDataSource.getAllMusicFromAlbum(
            albumId = albumId
        )

    /**
     * Retrieves the number of musics sharing the same cover.
     */
    suspend fun getNumberOfMusicsWithCoverId(coverId: UUID): Int =
        musicDataSource.getNumberOfMusicsWithCoverId(
            coverId = coverId
        )

    /**
     * Updates the status of quick access of a Music.
     */
    suspend fun updateQuickAccessState(newQuickAccessState: Boolean, musicId: UUID) =
        musicDataSource.updateQuickAccessState(
            newQuickAccessState = newQuickAccessState,
            musicId = musicId
        )

    /**
     * Retrieves the number of time a Music has been played.
     */
    suspend fun getNbPlayedOfMusic(musicId: UUID): Int = musicDataSource.getNbPlayedOfMusic(
        musicId = musicId
    )

    /**
     * Update the total of played time of a Music.
     */
    suspend fun updateNbPlayed(newNbPlayed: Int, musicId: UUID) = musicDataSource.updateNbPlayed(
        newNbPlayed = newNbPlayed,
        musicId = musicId
    )

    /**
     * Hide or show musics in a given folder.
     */
    suspend fun updateMusicsHiddenState(folderName: String, newIsHidden: Boolean) =
        musicDataSource.updateMusicsHiddenState(
            folderName = folderName,
            newIsHidden = newIsHidden
        )

    /**
     * Retrieves all musics from a Folder.
     */
    suspend fun getMusicsFromFolder(folderName: String): List<Music> =
        musicDataSource.getMusicsFromFolder(
            folderName = folderName
        )
}