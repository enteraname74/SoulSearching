package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.domain.usecase.artist.GetCorrespondingArtistUseCase
import com.github.enteraname74.domain.util.MusicFileUpdater

class UpdateMusicUseCase(
    private val musicRepository: MusicRepository,
    private val artistRepository: ArtistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val updateAlbumOfMusicUseCase: UpdateAlbumOfMusicUseCase,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
    private val getCorrespondingArtistUseCase: GetCorrespondingArtistUseCase,
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
        newMusicInformation: Music,
    ) {
        if (legacyMusic.artist != newMusicInformation.artist) {
            val legacyArtist = artistRepository.getFromName(artistName = legacyMusic.artist)
            var newArtist =
                artistRepository.getFromName(artistName = newMusicInformation.artist.trim())

            // It's a new artist, we need to create it.
            if (newArtist == null) {
                newArtist = Artist(
                    artistName = newMusicInformation.artist,
                    cover = (newMusicInformation.cover as? Cover.FileCover)?.fileCoverId?.let { fileCoverId ->
                        Cover.FileCover(
                            fileCoverId = fileCoverId,
                        )
                    }
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

        musicRepository.upsert(newMusicInformation)
        musicFileUpdater.updateMusic(music = newMusicInformation)
    }
}