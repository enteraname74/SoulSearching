package com.github.enteraname74.domain.usecase.music

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.album.GetCorrespondingAlbumUseCase
import com.github.enteraname74.domain.usecase.album.UpdateAlbumCoverUseCase
import com.github.enteraname74.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.domain.usecase.artist.GetCorrespondingArtistUseCase
import com.github.enteraname74.domain.usecase.artist.UpdateArtistCoverUseCase
import com.github.enteraname74.domain.usecase.imagecover.GetCoverOfElementUseCase
import com.github.enteraname74.domain.util.MusicFileUpdater

class UpdateMusicUseCase(
    private val musicRepository: MusicRepository,
    private val artistRepository: ArtistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val updateAlbumOfMusicUseCase: UpdateAlbumOfMusicUseCase,
    private val updateArtistCoverUseCase: UpdateArtistCoverUseCase,
    private val updateAlbumCoverUseCase: UpdateAlbumCoverUseCase,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
    private val getCorrespondingArtistUseCase: GetCorrespondingArtistUseCase,
    private val getCorrespondingAlbumUseCase: GetCorrespondingAlbumUseCase,
    private val getCoverOfElementUseCase: GetCoverOfElementUseCase,
    private val musicFileUpdater: MusicFileUpdater,
) {
    /**
     * Update a music.
     *
     * @param legacyMusic the information of the previous version of the music to update
     * (used for comparison between the legacy and new music information for better updating).
     * @param newMusicInformation the new music information to save.
     */
    suspend operator fun invoke(
        legacyMusic: Music,
        newMusicInformation: Music
    ) {
        if (legacyMusic.artist != newMusicInformation.artist) {
            val legacyArtist = artistRepository.getFromName(artistName = legacyMusic.artist)
            var newArtist =
                artistRepository.getFromName(artistName = newMusicInformation.artist.trim())

            // It's a new artist, we need to create it.
            if (newArtist == null) {
                newArtist = Artist(
                    artistName = newMusicInformation.artist,
                    coverId = newMusicInformation.coverId
                )
                artistRepository.upsert(artist = newArtist)
            }

            // We update the link between the artist and the music.
            musicArtistRepository.updateArtistOfMusic(
                musicId = legacyMusic.musicId,
                newArtistId = newArtist.artistId
            )

            updateAlbumOfMusicUseCase(
                artistId = newArtist.artistId,
                legacyMusic = legacyMusic,
                newAlbumName = newMusicInformation.album,
            )
            legacyArtist?.let { artist ->
                deleteArtistIfEmptyUseCase(artistId = artist.artistId)
            }
        } else if (legacyMusic.album != newMusicInformation.album) {
            val musicArtist = getCorrespondingArtistUseCase(musicId = legacyMusic.musicId)

            musicArtist?.let { artist ->
                updateAlbumOfMusicUseCase(
                    legacyMusic = legacyMusic,
                    newAlbumName = newMusicInformation.album,
                    artistId = artist.artistId
                )
            }
        }
        // We update the cover of the artist and album of the music.
        val artist = artistRepository.getFromName(
            newMusicInformation.artist
        )
        val album = getCorrespondingAlbumUseCase(
            albumName = newMusicInformation.album,
            artistId = artist!!.artistId
        )
        // If the artist does not have a cover, we give him the one of the music if it has one.
        if (artist.coverId == null && newMusicInformation.coverId != null) {
            updateArtistCoverUseCase(
                newCoverId = newMusicInformation.coverId!!,
                artistId =  artist.artistId
            )
        }
        // We do the same for the album.
        if (album!!.coverId == null && newMusicInformation.coverId != null) {
            updateAlbumCoverUseCase(
                newCoverId = newMusicInformation.coverId!!,
                albumId = album.albumId
            )
        }
        musicRepository.upsert(newMusicInformation)

        // We only set a new cover if the previous one has been changed.
        val musicCover: ImageBitmap? = if (legacyMusic.coverId != newMusicInformation.coverId) {
            newMusicInformation.coverId?.let { coverId ->
                getCoverOfElementUseCase(coverId = coverId)?.cover
            }
        } else {
            null
        }

        musicFileUpdater.updateMusic(
            music = newMusicInformation,
            cover = musicCover
        )
    }
}