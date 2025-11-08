package com.github.enteraname74.soulsearching.features.filemanager.usecase

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.music.UpdateAlbumOfMusicUseCase
import com.github.enteraname74.soulsearching.features.filemanager.util.MusicFileUpdater

class UpdateMusicUseCase(
    private val musicRepository: MusicRepository,
    private val artistRepository: ArtistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val updateAlbumOfMusicUseCase: UpdateAlbumOfMusicUseCase,
    private val commonArtistUseCase: CommonArtistUseCase,
    private val musicFileUpdater: MusicFileUpdater,
) {
    private suspend fun getOrCreateArtist(
        artistName: String,
        music: Music,
    ): Artist {
        var existingNewArtist = artistRepository.getFromName(artistName = artistName)

        // It's a new artist, we need to create it.
        if (existingNewArtist == null) {
            existingNewArtist = Artist(
                artistName = artistName,
                cover = (music.cover as? Cover.CoverFile)?.fileCoverId?.let { fileCoverId ->
                    Cover.CoverFile(
                        fileCoverId = fileCoverId,
                    )
                }
            )
            artistRepository.upsert(artist = existingNewArtist)
        }

        return existingNewArtist
    }

    private suspend fun handlePrimaryArtistOfMusic(
        legacyMusic: Music,
        newMusicInformation: Music,
        newArtist: String,
    ) {
        val existingNewArtist = getOrCreateArtist(
            artistName = newArtist,
            music = newMusicInformation
        )

        // We update the link between the artist and the music.
        musicArtistRepository.upsertMusicIntoArtist(
            musicArtist = MusicArtist(
                musicId = newMusicInformation.musicId,
                artistId = existingNewArtist.artistId,
            ),
        )

        updateAlbumOfMusicUseCase(
            artist = existingNewArtist,
            legacyMusic = legacyMusic,
            newAlbum = newMusicInformation.album,
        )
    }

    private suspend fun handleArtists(
        legacyMusic: Music,
        newMusicInformation: Music,
        previousArtists: List<Artist>,
        newArtistsName: List<String>,
    ) {
        // We will remove the link of the music to all its other previous artists
        previousArtists.forEach { artist ->
            musicArtistRepository.deleteMusicArtist(
                musicArtist = MusicArtist(
                    musicId = legacyMusic.musicId,
                    artistId = artist.artistId,
                )
            )
        }

        // We then need to handle the primary artist of the song (it's the one that possess the album of the song)
        handlePrimaryArtistOfMusic(
            legacyMusic = legacyMusic,
            newMusicInformation = newMusicInformation,
            newArtist = newArtistsName.first(),
        )

        // We then link the music to its new artists
        newArtistsName.subList(1, newArtistsName.size).forEach { newArtistName ->
            val newArtist: Artist = getOrCreateArtist(
                artistName = newArtistName,
                music = newMusicInformation
            )
            musicArtistRepository.upsertMusicIntoArtist(
                musicArtist = MusicArtist(
                    musicId = newMusicInformation.musicId,
                    artistId = newArtist.artistId,
                )
            )
        }

        // We finally check if the previous artists can be deleted :
        previousArtists.forEach { artist ->
            commonArtistUseCase.deleteIfEmpty(
                artistId = artist.artistId,
            )
        }
    }

    /**
     * Update a music.
     *
     * @param legacyMusic the information of the previous version of the music to update
     * (used for comparison between the legacy and new music information for better updating).
     * @param newMusicInformation the new music information to save.
     */
    suspend operator fun invoke(
        legacyMusic: Music,
        newArtistsNames: List<String>,
        newMusicInformation: Music,
    ) {
        val allPreviousArtists: List<Artist> = legacyMusic.artists.distinctBy { it.artistName }
        val allNewArtistsNames: List<String> = newArtistsNames.distinct()

        val hasArtistsChanged = allPreviousArtists.map { it.artistName } != allNewArtistsNames

        if (hasArtistsChanged) {
            handleArtists(
                legacyMusic = legacyMusic,
                newMusicInformation = newMusicInformation,
                previousArtists = allPreviousArtists,
                newArtistsName = allNewArtistsNames,
            )
        } else if (legacyMusic.album != newMusicInformation.album) {
            updateAlbumOfMusicUseCase(
                legacyMusic = legacyMusic,
                newAlbum = newMusicInformation.album,
                artist = legacyMusic.artists.first()
            )
        }

        musicRepository.upsert(
            newMusicInformation
        )
        musicFileUpdater.updateMusic(music = newMusicInformation)
    }
}