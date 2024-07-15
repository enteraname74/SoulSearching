package com.github.enteraname74.soulsearching.domain.model

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
import com.github.enteraname74.domain.usecase.album.GetCorrespondingAlbumUseCase
import com.github.enteraname74.domain.usecase.album.UpdateAlbumCoverUseCase
import com.github.enteraname74.domain.usecase.album.UpsertAlbumUseCase
import com.github.enteraname74.domain.usecase.albumartist.UpsertAlbumArtistUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistFromNameUseCase
import com.github.enteraname74.domain.usecase.artist.UpdateArtistCoverUseCase
import com.github.enteraname74.domain.usecase.artist.UpsertArtistUseCase
import com.github.enteraname74.domain.usecase.folder.UpsertFolderUseCase
import com.github.enteraname74.domain.usecase.imagecover.GetCoverOfElementUseCase
import com.github.enteraname74.domain.usecase.imagecover.UpsertImageCoverUseCase
import com.github.enteraname74.domain.usecase.music.IsMusicAlreadySavedUseCase
import com.github.enteraname74.domain.usecase.music.UpsertMusicUseCase
import com.github.enteraname74.domain.usecase.musicalbum.UpsertMusicIntoAlbumUseCase
import com.github.enteraname74.domain.usecase.musicartist.UpsertMusicIntoArtistUseCase
import java.util.UUID

/**
 * Utilities for fetching musics on current device.
 */
abstract class MusicFetcher(
    private val isMusicAlreadySavedUseCase: IsMusicAlreadySavedUseCase,
    private val getArtistFromNameUseCase: GetArtistFromNameUseCase,
    private val getCorrespondingAlbumUseCase: GetCorrespondingAlbumUseCase,
    private val upsertImageCoverUseCase: UpsertImageCoverUseCase,
    private val upsertAlbumUseCase: UpsertAlbumUseCase,
    private val upsertArtistUseCase: UpsertArtistUseCase,
    private val upsertAlbumArtistUseCase: UpsertAlbumArtistUseCase,
    private val getCoverOfElementUseCase: GetCoverOfElementUseCase,
    private val updateAlbumCoverUseCase: UpdateAlbumCoverUseCase,
    private val updateArtistCoverUseCase: UpdateArtistCoverUseCase,
    private val upsertMusicUseCase: UpsertMusicUseCase,
    private val upsertFolderUseCase: UpsertFolderUseCase,
    private val upsertMusicIntoAlbumUseCase: UpsertMusicIntoAlbumUseCase,
    private val upsertMusicIntoArtistUseCase: UpsertMusicIntoArtistUseCase,
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
    ): ArrayList<SelectableMusicItem>

    /**
     * Persist a music and its cover.
     */
    suspend fun addMusic(musicToAdd: Music, musicCover: ImageBitmap?) {
        // If the song has already been saved once, we do nothing.
        println(musicToAdd)
        if (isMusicAlreadySavedUseCase(musicToAdd.path)) return

        val correspondingArtist = getArtistFromNameUseCase(
            artistName = musicToAdd.artist
        )

        // If we can found a corresponding artist, we check if we can found a corresponding album.
        val correspondingAlbum = if (correspondingArtist == null) {
            null
        } else {
            getCorrespondingAlbumUseCase(
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
                upsertImageCoverUseCase(
                    ImageCover(
                        coverId = coverId,
                        cover = musicCover
                    )
                )
            }

            upsertAlbumUseCase(
                Album(
                    coverId = if (musicCover != null) coverId else null,
                    albumId = albumId,
                    albumName = musicToAdd.album
                )
            )

            val artistCoverId = if (correspondingArtist != null) correspondingArtist.coverId
            else if (musicCover != null) coverId else null

            upsertArtistUseCase(
                Artist(
                    coverId = artistCoverId,
                    artistId = artistId,
                    artistName = musicToAdd.artist
                )
            )
            upsertAlbumArtistUseCase(
                AlbumArtist(
                    albumId = albumId,
                    artistId = artistId
                )
            )
        } else {
            // We add, if possible, the cover art of the album of the music to the music.
            val albumCover = correspondingAlbum.coverId?.let { coverId ->
                getCoverOfElementUseCase(coverId = coverId)
            }
            val shouldPutAlbumCoverWithMusic = (albumCover != null)
            val shouldUpdateArtistCover =
                (correspondingArtist?.coverId == null) && ((albumCover != null) || (musicCover != null))

            if (shouldPutAlbumCoverWithMusic) {
                musicToAdd.coverId = albumCover?.coverId
            } else if (musicCover != null) {
                val coverId = UUID.randomUUID()
                musicToAdd.coverId = coverId
                upsertImageCoverUseCase(
                    ImageCover(
                        coverId = coverId,
                        cover = musicCover
                    )
                )
                // In this case, the album has no cover, so we add the one from the music.
                updateAlbumCoverUseCase(
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
                    updateArtistCoverUseCase(
                        newCoverId = newArtistCover,
                        artistId = correspondingArtist.artistId
                    )
                }
            }
        }
        upsertMusicUseCase(musicToAdd)
        upsertFolderUseCase(
            Folder(
                folderPath = musicToAdd.folder
            )
        )
        upsertMusicIntoAlbumUseCase(
            MusicAlbum(
                musicId = musicToAdd.musicId,
                albumId = albumId
            )
        )
        upsertMusicIntoArtistUseCase(
            MusicArtist(
                musicId = musicToAdd.musicId,
                artistId = artistId
            )
        )
    }
}