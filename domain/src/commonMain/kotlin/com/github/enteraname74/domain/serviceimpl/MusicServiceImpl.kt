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
import com.github.enteraname74.domain.model.MusicWithCover
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.service.MusicService
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
    private val folderRepository: FolderRepository
): MusicService {

    /**
     * Tries to retrieve the corresponding album of a music and its artist.
     */
    private suspend fun getCorrespondingAlbum(
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
     * Check if a music has already been saved.
     */
    private suspend fun isMusicAlreadySaved(music: Music): Boolean =  musicRepository.getMusicFromPath(music.path) != null

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
        val correspondingAlbum = getCorrespondingAlbum(
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
        TODO("Not yet implemented")
    }

    override suspend fun update(musicWithCover: MusicWithCover) {
        TODO("Not yet implemented")
    }
}