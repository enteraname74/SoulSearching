package com.github.enteraname74.soulsearching.features.filemanager.usecase

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistsOfMusicUseCase
import com.github.enteraname74.domain.usecase.music.UpdateAlbumOfMusicUseCase
import com.github.enteraname74.soulsearching.features.filemanager.util.MusicFileUpdater
import kotlinx.coroutines.flow.firstOrNull

class UpdateMusicUseCase(
    private val musicRepository: MusicRepository,
    private val artistRepository: ArtistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val updateAlbumOfMusicUseCase: UpdateAlbumOfMusicUseCase,
    private val commonArtistUseCase: CommonArtistUseCase,
    private val getArtistsOfMusicUseCase: GetArtistsOfMusicUseCase,
    private val updateArtistNameOfMusicUseCase: UpdateArtistNameOfMusicUseCase,
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
            artistId = existingNewArtist.artistId,
            legacyMusic = legacyMusic,
            newAlbumName = newMusicInformation.album,
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
        previousArtists: List<Artist>,
        newArtistsNames: List<String>,
        newMusicInformation: Music,
    ) {
        /*
        We will put the album artist with the other artists.
        It will be in the first place as it's the artist that possess the album.
         */
        val allPreviousArtists: List<Artist> = buildList {
            legacyMusic.albumArtist?.let { name ->
                artistRepository.getFromName(artistName = name)?.let { add(it) }
            }
            addAll(previousArtists)
        }.distinctBy { it.artistName }

        /*
        Same for the new artists. It will be easier to handle them all at once.
         */
        val allNewArtistsNames: List<String> = buildList {
            newMusicInformation.albumArtist?.let {
                add(it)
            }
            addAll(newArtistsNames)
        }.distinct()

        val hasArtistsChanged = allPreviousArtists.map { it.artistName } != allNewArtistsNames

        if (hasArtistsChanged) {
            handleArtists(
                legacyMusic = legacyMusic,
                newMusicInformation = newMusicInformation,
                previousArtists = allPreviousArtists,
                newArtistsName = allNewArtistsNames,
            )
        } else if (legacyMusic.album != newMusicInformation.album) {
            val artistForAlbum: Artist? = if (newMusicInformation.albumArtist != null) {
                getOrCreateArtist(
                    artistName = newMusicInformation.albumArtist.orEmpty(),
                    music = newMusicInformation,
                )
            } else {
                getArtistsOfMusicUseCase(music = legacyMusic)
                    .firstOrNull()
                    ?.firstOrNull()
            }

            artistForAlbum?.let { artist ->
                updateAlbumOfMusicUseCase(
                    legacyMusic = legacyMusic,
                    newAlbumName = newMusicInformation.album,
                    artistId = artist.artistId
                )
            }
        }

        musicRepository.upsert(
            newMusicInformation.copy(
                artist = updateArtistNameOfMusicUseCase.buildUpdatedMusicArtistsString(music = newMusicInformation),
            )
        )
        musicFileUpdater.updateMusic(music = newMusicInformation)
    }
}