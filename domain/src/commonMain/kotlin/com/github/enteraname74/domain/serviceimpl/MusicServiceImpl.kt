package com.github.enteraname74.domain.serviceimpl

import androidx.compose.ui.graphics.ImageBitmap
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
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.domain.service.MusicService
import com.github.enteraname74.domain.util.CheckAndDeleteVerification
import java.util.UUID

/**
 * Implementation of the MusicService using the specified repositories.
 */
class MusicServiceImpl(
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val albumArtistRepository: AlbumArtistRepository,
    private val musicAlbumRepository: MusicAlbumRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val imageCoverRepository: ImageCoverRepository,
    private val folderRepository: FolderRepository,
    private val playlistRepository: PlaylistRepository,
    private val musicPlaylistRepository: MusicPlaylistRepository,
    private val checkAndDeleteVerification: CheckAndDeleteVerification
) : MusicService {

    /**
     * Tries to retrieve the corresponding album of a music and its artist.
     */
    private suspend fun getCorrespondingAlbumFromAlbumName(
        musicAlbum: String,
        albumArtist: Artist?
    ): Album? = if (albumArtist == null) {
        null
    } else {
        albumRepository.getCorrespondingAlbum(
            albumName = musicAlbum,
            artistId = albumArtist.artistId
        )
    }

    /**
     * Tries to retrieve the corresponding album of a music.
     */
    private suspend fun getCorrespondingAlbumFromMusicId(musicId: UUID): Album? {
        val albumId = musicAlbumRepository.getAlbumIdFromMusicId(
            musicId = musicId
        ) ?: return null
        return albumRepository.getAlbumFromId(
            albumId = albumId
        )
    }

    /**
     * Tries to retrieve the corresponding artist of a music.
     */
    private suspend fun getCorrespondingArtistFromMusicId(musicId: UUID): Artist? {
        val artistId = musicArtistRepository.getArtistIdFromMusicId(
            musicId = musicId
        ) ?: return null
        return artistRepository.getArtistFromId(
            artistId = artistId
        )
    }

    /**
     * Check if a music has already been saved.
     */
    private suspend fun isMusicAlreadySaved(music: Music): Boolean =
        musicRepository.getMusicFromPath(music.path) != null

    private suspend fun saveMusicAndCreateMissingArtistAndAlbum(
        music: Music,
        musicCover: ImageBitmap?,
        albumId: UUID,
        artistId: UUID
    ) {
        val coverId = UUID.randomUUID()
        if (musicCover != null) {
            music.coverId = coverId
            imageCoverRepository.insertImageCover(
                ImageCover(
                    coverId = coverId,
                    cover = musicCover
                )
            )
        }

        albumRepository.insertAlbum(
            Album(
                coverId = if (musicCover != null) coverId else null,
                albumId = albumId,
                albumName = music.album
            )
        )
        artistRepository.insertArtist(
            Artist(
                coverId = if (musicCover != null) coverId else null,
                artistId = artistId,
                artistName = music.artist
            )
        )
        albumArtistRepository.insertAlbumIntoArtist(
            AlbumArtist(
                albumId = albumId,
                artistId = artistId
            )
        )
    }

    override suspend fun save(musicWithCover: MusicWithCover) {
        val music = musicWithCover.music
        val musicCover = musicWithCover.cover?.cover

        // Si la musique a déjà été enregistrée, on ne fait rien :
        if (isMusicAlreadySaved(music = music)) return

        val correspondingArtist = artistRepository.getArtistFromInfo(
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
                imageCoverRepository.getCoverOfElement(coverId = correspondingAlbum.coverId!!)
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
                imageCoverRepository.insertImageCover(
                    ImageCover(
                        coverId = coverId,
                        cover = musicCover
                    )
                )
                // Dans ce cas, l'album n'a pas d'image, on lui en ajoute une :
                albumRepository.updateAlbumCover(
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
                    artistRepository.updateArtistCover(
                        newCoverId = newArtistCover,
                        artistId = correspondingArtist.artistId
                    )
                }
            }
        }
        musicRepository.insertMusic(music)
        folderRepository.insertFolder(
            Folder(
                folderPath = music.folder
            )
        )
        musicAlbumRepository.insertMusicIntoAlbum(
            MusicAlbum(
                musicId = music.musicId,
                albumId = albumId
            )
        )
        musicArtistRepository.insertMusicIntoArtist(
            MusicArtist(
                musicId = music.musicId,
                artistId = artistId
            )
        )
    }

    override suspend fun delete(musicId: UUID) {
        val musicToRemove = musicRepository.getMusicFromId(musicId = musicId)

        val artist = getCorrespondingArtistFromMusicId(musicId = musicToRemove.musicId)
        val album = getCorrespondingAlbumFromMusicId(musicId = musicToRemove.musicId)

        musicRepository.deleteMusic(music = musicToRemove)

        checkAndDeleteVerification.checkAndDeleteAlbum(albumToCheck = album!!)
        checkAndDeleteVerification.checkAndDeleteArtist(artistToCheck = artist!!)
    }

    override suspend fun update(
        legacyMusic: Music,
        newMusicInformation: Music
    ) {
        if (legacyMusic.artist != newMusicInformation.artist) {
            val legacyArtist = artistRepository.getArtistFromInfo(artistName = legacyMusic.artist)
            var newArtist = artistRepository.getArtistFromInfo(artistName = newMusicInformation.artist.trim())

            // It's a new artist, we need to create it.
            if (newArtist == null) {
                newArtist = Artist(
                    artistName = newMusicInformation.artist,
                    coverId = newMusicInformation.coverId
                )
                artistRepository.insertArtist(
                    newArtist
                )
            }

            // On met les infos de la musique à jour :
            musicArtistRepository.updateArtistOfMusic(
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
        val artist = artistRepository.getArtistFromInfo(
            newMusicInformation.artist
        )
        val album = albumRepository.getCorrespondingAlbum(
            albumName = newMusicInformation.album,
            artistId = artist!!.artistId
        )
        // Si l'artiste n'a pas d'image, on lui donne la nouvelle cover
        if (artist.coverId == null) {
            newMusicInformation.coverId?.let { musicCoverId ->
                artistRepository.updateArtistCover(musicCoverId, artist.artistId)
            }
        }
        // Faison de même pour l'album :
        if (album!!.coverId == null) {
            newMusicInformation.coverId?.let { musicCoverId ->
                albumRepository.updateAlbumCover(musicCoverId, album.albumId)
            }
        }
        musicRepository.insertMusic(newMusicInformation)
    }

    override suspend fun updateAlbumOfMusic(
        legacyMusic: Music,
        artistId: UUID,
        newAlbumName: String
    ) {
        val legacyAlbum = getCorrespondingAlbumFromMusicId(musicId = legacyMusic.musicId)

        var newMusicAlbum = albumRepository.getCorrespondingAlbum(
            albumName = newAlbumName,
            artistId = artistId
        )

        // If the album name is not corresponding to an existing album, we create it.
        if (newMusicAlbum == null) {
            newMusicAlbum = Album(
                albumName = newAlbumName,
                coverId = legacyMusic.coverId
            )
            albumRepository.insertAlbum(album = newMusicAlbum)

            // We link the new album to the music's artist.
            albumArtistRepository.insertAlbumIntoArtist(
                AlbumArtist(
                    albumId = newMusicAlbum.albumId,
                    artistId = artistId
                )
            )
        }

        // We update the album of the music.
        musicAlbumRepository.updateAlbumOfMusic(
            musicId = legacyMusic.musicId,
            newAlbumId = newMusicAlbum.albumId
        )

        // We remove the legacy album if there is no music left in it.
        legacyAlbum?.let { album ->
            checkAndDeleteVerification.checkAndDeleteAlbum(albumToCheck = album)
        }
    }

    override suspend fun updateNbPlayed(musicId: UUID) {
        val currentNbPlayed = musicRepository.getNbPlayedOfMusic(musicId)
        musicRepository.updateNbPlayed(
            newNbPlayed = currentNbPlayed + 1,
            musicId = musicId
        )
    }

    override suspend fun toggleQuickAccessState(musicId: UUID) {
        val legacyQuickAccessState = musicRepository.getMusicFromId(musicId = musicId).isInQuickAccess

        musicRepository.updateQuickAccessState(
            musicId = musicId,
            newQuickAccessState = !legacyQuickAccessState
        )
    }

    override suspend fun toggleFavoriteState(musicId: UUID) {
        val isInFavorite = musicRepository.getMusicFromFavoritePlaylist(musicId) != null
        val playlistId = playlistRepository.getFavoritePlaylist().playlistId
        if (isInFavorite) {
            musicPlaylistRepository.deleteMusicFromPlaylist(
                musicId = musicId,
                playlistId = playlistId
            )
        } else {
            musicPlaylistRepository.insertMusicIntoPlaylist(
                MusicPlaylist(
                    musicId = musicId,
                    playlistId = playlistId
                )
            )
        }
    }
}