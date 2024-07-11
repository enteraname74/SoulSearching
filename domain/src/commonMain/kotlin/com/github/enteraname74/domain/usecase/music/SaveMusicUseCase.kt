package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.repository.*
import com.github.enteraname74.domain.usecase.album.GetCorrespondingAlbumUseCase
import com.github.enteraname74.domain.usecase.album.UpdateAlbumCoverUseCase
import com.github.enteraname74.domain.usecase.artist.UpdateArtistCoverUseCase
import java.util.*

/**
 * Preferred music use case when needing to save a new music.
 */
class SaveMusicUseCase(
    private val musicRepository: MusicRepository,
    private val imageCoverRepository: ImageCoverRepository,
    private val folderRepository: FolderRepository,
    private val artistRepository: ArtistRepository,
    private val musicAlbumRepository: MusicAlbumRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val isMusicAlreadySavedUseCase: IsMusicAlreadySavedUseCase,
    private val getCorrespondingAlbumUseCase: GetCorrespondingAlbumUseCase,
    private val saveMusicAndCreateMissingArtistAndAlbumUseCase: SaveMusicAndCreateMissingArtistAndAlbumUseCase,
    private val updateAlbumCoverUseCase: UpdateAlbumCoverUseCase,
    private val updateArtistCoverUseCase: UpdateArtistCoverUseCase,
) {

    suspend operator fun invoke(
        musicWithCover: MusicWithCover,
    ) {
        val music = musicWithCover.music
        val musicCover = musicWithCover.cover?.cover

        // If the song was already saved, we do nothing
        if (isMusicAlreadySavedUseCase(musicId= music.musicId)) return

        val correspondingArtist: Artist? = artistRepository.getFromName(artistName = music.artist)

        // If the artist, we try to found a corresponding album
        val correspondingAlbum: Album? = correspondingArtist?.let { artist ->
            getCorrespondingAlbumUseCase(
                albumName = music.album,
                artistId = artist.artistId
            )
        }
        val albumId = correspondingAlbum?.albumId ?: UUID.randomUUID()
        val artistId = correspondingArtist?.artistId ?: UUID.randomUUID()

        if (correspondingAlbum == null) {
            saveMusicAndCreateMissingArtistAndAlbumUseCase(
                music = music,
                albumId = albumId,
                artistId = artistId,
                musicCover = musicCover
            )
        } else {
            // We add if possible the album cover to the song
            val albumCover = if (correspondingAlbum.coverId != null) {
                imageCoverRepository.getCoverOfElement(coverId = correspondingAlbum.coverId!!)
            } else {
                null
            }
            val shouldPutAlbumCoverWithMusic = (albumCover != null)
            val shouldUpdateArtistCover =
                (correspondingArtist.coverId == null) && ((albumCover != null) || (musicCover != null))

            if (shouldPutAlbumCoverWithMusic) {
                music.coverId = albumCover?.coverId
            } else if (musicCover != null) {
                val coverId = UUID.randomUUID()
                music.coverId = coverId
                imageCoverRepository.upsert(
                    ImageCover(
                        coverId = coverId,
                        cover = musicCover
                    )
                )

                // In this case, the album has no cover, we will add one to it
                updateAlbumCoverUseCase(
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
                if (newArtistCover != null) {
                    updateArtistCoverUseCase(
                        newCoverId = newArtistCover,
                        artistId = correspondingArtist.artistId
                    )
                }
            }
        }
        musicRepository.upsert(music)
        folderRepository.upsert(
            Folder(
                folderPath = music.folder
            )
        )
        musicAlbumRepository.upsertMusicIntoAlbum(
            MusicAlbum(
                musicId = music.musicId,
                albumId = albumId
            )
        )
        musicArtistRepository.upsertMusicIntoArtist(
            MusicArtist(
                musicId = music.musicId,
                artistId = artistId
            )
        )
    }
}
