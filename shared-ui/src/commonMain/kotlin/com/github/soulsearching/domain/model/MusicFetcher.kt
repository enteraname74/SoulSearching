package com.github.soulsearching.domain.model

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumArtist
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicAlbum
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import java.util.UUID

/**
 * Utilities for fetching musics on current device.
 */
abstract class MusicFetcher(
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val musicAlbumRepository: MusicAlbumRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val albumArtistRepository: AlbumArtistRepository,
    private val imageCoverRepository: ImageCoverRepository,
    private val folderRepository: FolderRepository
) {
    /**
     * Fetch all musics on the device.
     */
    abstract suspend fun fetchMusics(
        updateProgress: (Float) -> Unit,
        finishAction: () -> Unit
    )

    /**
     * Fetch musics from specified folders on the device.
     */
    abstract fun fetchMusicsFromSelectedFolders(
        updateProgress: (Float) -> Unit,
        alreadyPresentMusicsPaths: List<String>,
        hiddenFoldersPaths: List<String>
    ) : ArrayList<SelectableMusicItem>

    /**
     * Persist a music and its cover.
     */
    suspend fun addMusic(musicToAdd: Music, musicCover: ImageBitmap?) {
        // Si la musique a déjà été enregistrée, on ne fait rien :
        val existingMusic = musicRepository.getMusicFromPath(musicToAdd.path)
        if (existingMusic != null) {
            return
        }

        val correspondingArtist = artistRepository.getArtistFromInfo(
            artistName = musicToAdd.artist
        )
        // Si l'artiste existe, on regarde si on trouve un album correspondant :
        val correspondingAlbum = if (correspondingArtist == null) {
            null
        } else {
            albumRepository.getCorrespondingAlbum(
                albumName = musicToAdd.album,
                artistId = correspondingArtist.artistId
            )
        }
        val albumId = correspondingAlbum?.albumId ?: UUID.randomUUID()
        val artistId = correspondingArtist?.artistId ?: UUID.randomUUID()
        if (correspondingAlbum == null) {
            val coverId = UUID.randomUUID()
            if (musicCover != null) {
                musicToAdd.coverId = coverId
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
                    albumName = musicToAdd.album
                )
            )
            artistRepository.insertArtist(
                Artist(
                    coverId = if (musicCover != null) coverId else null,
                    artistId = artistId,
                    artistName = musicToAdd.artist
                )
            )
            albumArtistRepository.insertAlbumIntoArtist(
                AlbumArtist(
                    albumId = albumId,
                    artistId = artistId
                )
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
                musicToAdd.coverId = albumCover?.coverId
            } else if (musicCover != null) {
                val coverId = UUID.randomUUID()
                musicToAdd.coverId = coverId
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
                    musicToAdd.coverId
                }
                if (correspondingArtist != null && newArtistCover != null) {
                    artistRepository.updateArtistCover(
                        newCoverId = newArtistCover,
                        artistId = correspondingArtist.artistId
                    )
                }
            }
        }
        musicRepository.insertMusic(musicToAdd)
        folderRepository.insertFolder(
            Folder(
                folderPath = musicToAdd.folder
            )
        )
        musicAlbumRepository.insertMusicIntoAlbum(
            MusicAlbum(
                musicId = musicToAdd.musicId,
                albumId = albumId
            )
        )
        musicArtistRepository.insertMusicIntoArtist(
            MusicArtist(
                musicId = musicToAdd.musicId,
                artistId = artistId
            )
        )
    }
}