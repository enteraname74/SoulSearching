package com.github.enteraname74.soulsearching.repository.repositoryimpl

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumArtist
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicAlbum
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.model.MusicWithCover
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.util.CheckAndDeleteVerification
import com.github.enteraname74.domain.util.MusicFileUpdater
import com.github.enteraname74.soulsearching.repository.datasource.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

/**
 * Repository for handling Music related work.
 */
class MusicRepositoryImpl(
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
): MusicRepository {
    private val musicFileUpdater = MusicFileUpdater()

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
        return albumDataSource.getFromId(
            albumId = albumId
        ).first()
    }

    /**
     * Tries to retrieve the corresponding artist of a music.
     */
    private suspend fun getCorrespondingArtistFromMusicId(musicId: UUID): Artist? {
        val artistId = musicArtistDataSource.getArtistIdFromMusicId(
            musicId = musicId
        ) ?: return null
        return artistDataSource.getFromId(
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
            imageCoverDataSource.upsert(
                ImageCover(
                    coverId = coverId,
                    cover = musicCover
                )
            )
        }

        albumDataSource.insert(
            Album(
                coverId = if (musicCover != null) coverId else null,
                albumId = albumId,
                albumName = music.album
            )
        )
        artistDataSource.upsert(
            Artist(
                coverId = if (musicCover != null) coverId else null,
                artistId = artistId,
                artistName = music.artist
            )
        )
        albumArtistDataSource.upsert(
            AlbumArtist(
                albumId = albumId,
                artistId = artistId
            )
        )
    }

    override suspend fun upsert(musicWithCover: MusicWithCover) {
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
                imageCoverDataSource.upsert(
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
        folderDataSource.upsert(
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

    override suspend fun delete(musicId: UUID) {
        val musicToRemove = musicDataSource.getFromId(musicId = musicId).first() ?: return

        musicDataSource.delete(music = musicToRemove)

        val artist = getCorrespondingArtistFromMusicId(musicId = musicToRemove.musicId)
        val album = getCorrespondingAlbumFromMusicId(musicId = musicToRemove.musicId)
        album?.let { musicAlbum ->
            checkAndDeleteVerification.checkAndDeleteAlbum(albumToCheck = musicAlbum)
        }
        artist?.let { musicArtist ->
            checkAndDeleteVerification.checkAndDeleteArtist(artistToCheck = musicArtist)
        }

    }

    override suspend fun update(
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
                artistDataSource.upsert(
                    newArtist
                )
            }

            // We update the link between the artist and the music.
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
        // We update the cover of the artist and album of the music.
        val artist = artistDataSource.getArtistFromInfo(
            newMusicInformation.artist
        )
        val album = albumDataSource.getCorrespondingAlbum(
            albumName = newMusicInformation.album,
            artistId = artist!!.artistId
        )
        // If the artist does not have a cover, we give him the one of the music if it has one.
        if (artist.coverId == null) {
            newMusicInformation.coverId?.let { musicCoverId ->
                artistDataSource.updateArtistCover(musicCoverId, artist.artistId)
            }
        }
        // We do the same for the album.
        if (album!!.coverId == null) {
            newMusicInformation.coverId?.let { musicCoverId ->
                albumDataSource.updateAlbumCover(musicCoverId, album.albumId)
            }
        }
        musicDataSource.insertMusic(newMusicInformation)

        // We only set a new cover if the previous one has been changed.
        val musicCover: ImageBitmap? = if (legacyMusic.coverId != newMusicInformation.coverId) {
            newMusicInformation.coverId?.let { coverId ->
                imageCoverDataSource.getCoverOfElement(coverId = coverId)?.cover
            }
        } else {
            null
        }

        musicFileUpdater.updateMusic(
            music = newMusicInformation,
            cover = musicCover
        )
    }

    override suspend fun updateAlbumOfMusic(
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
            albumDataSource.insert(album = newMusicAlbum)

            // We link the new album to the music's artist.
            albumArtistDataSource.upsert(
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

    override suspend fun deleteAllMusicOfAlbum(album: String, artist: String) =
        musicDataSource.deleteMusicFromAlbum(
            album = album,
            artist = artist
        )

    override fun getFromId(musicId: UUID): Flow<Music?> = musicDataSource.getFromId(
        musicId = musicId
    )


    override fun getAll(): Flow<List<Music>> =
        musicDataSource.getAll()

    override suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music> =
        musicDataSource.getAllMusicFromAlbum(
            albumId = albumId
        )
}