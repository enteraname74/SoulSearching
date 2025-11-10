package com.github.enteraname74.soulsearching.features.filemanager.usecase

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.album.DeleteAlbumIfEmptyUseCase
import com.github.enteraname74.domain.usecase.album.GetCorrespondingAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.soulsearching.features.filemanager.util.MusicFileUpdater

class UpdateMusicUseCase(
    private val musicRepository: MusicRepository,
    private val artistRepository: ArtistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val getCorrespondingAlbumUseCase: GetCorrespondingAlbumUseCase,
    private val deleteAlbumIfEmptyUseCase: DeleteAlbumIfEmptyUseCase,
    private val commonArtistUseCase: CommonArtistUseCase,
    private val musicFileUpdater: MusicFileUpdater,
) {
    private suspend fun getOrCreateAlbum(
        artist: Artist,
        updateInformation: UpdateInformation,
    ): Album =
        getCorrespondingAlbumUseCase(
            albumName = updateInformation.newAlbumName,
            artistId = artist.artistId,
        ) ?: Album(
            albumName = updateInformation.newAlbumName,
            cover = (updateInformation.legacyMusic.cover as? Cover.CoverFile)?.fileCoverId?.let {
                Cover.CoverFile(fileCoverId = it)
            },
            artist = artist,
        )

    private suspend fun getOrCreateArtist(
        artistName: String,
        musicCover: Cover,
    ): Artist {
        var existingNewArtist = artistRepository.getFromName(artistName = artistName)

        // It's a new artist, we need to create it.
        if (existingNewArtist == null) {
            existingNewArtist = Artist(
                artistName = artistName,
                cover = (musicCover as? Cover.CoverFile)?.fileCoverId?.let { fileCoverId ->
                    Cover.CoverFile(
                        fileCoverId = fileCoverId,
                    )
                }
            )
            artistRepository.upsert(artist = existingNewArtist)
        }

        return existingNewArtist
    }

    private suspend fun handleArtists(
        updateInformation: UpdateInformation,
    ): InformationToSave {
        // We will remove the link of the music to all its other previous artists
        updateInformation.legacyMusic.artists.forEach { artist ->
            musicArtistRepository.deleteMusicArtist(
                musicArtist = MusicArtist(
                    musicId = updateInformation.legacyMusic.musicId,
                    artistId = artist.artistId,
                )
            )
        }

        val primaryArtist = getOrCreateArtist(
            artistName = updateInformation.sortedNewArtistsNames.first(),
            musicCover = updateInformation.newCover,
        )

        val secondaryArtists = updateInformation.sortedNewArtistsNames.subList(
            1,
            updateInformation.sortedNewArtistsNames.size,
        ).map { newArtistName ->
            getOrCreateArtist(
                artistName = newArtistName,
                musicCover = updateInformation.newCover,
            )
        }

        val updatedAlbum = getOrCreateAlbum(
            artist = primaryArtist,
            updateInformation = updateInformation,
        )

        return InformationToSave(
            album = updatedAlbum,
            primaryArtist = primaryArtist,
            secondaryArtists = secondaryArtists,
        )
    }

    private fun buildNewMusic(
        updateInformation: UpdateInformation,
        informationToSave: InformationToSave,
    ): Music =
        updateInformation.legacyMusic.copy(
            name = updateInformation.newName,
            cover = updateInformation.newCover,
            albumPosition = updateInformation.newAlbumPosition,
            album = informationToSave.album,
            artists = buildList {
                add(informationToSave.primaryArtist)
                addAll(informationToSave.secondaryArtists)
            }
        )

    /**
     * Update a music.
     *
     */
    suspend operator fun invoke(
        updateInformation: UpdateInformation,
    ): Music {
        val allPreviousArtists: List<Artist> = updateInformation.legacyMusic.artists.distinctBy { it.artistName }
        val allNewArtistsNames: List<String> = updateInformation.sortedNewArtistsNames.distinct()

        val hasArtistsChanged = allPreviousArtists.map { it.artistName } != allNewArtistsNames

        val informationToSave: InformationToSave = if (hasArtistsChanged) {
            handleArtists(updateInformation = updateInformation)
        } else if (updateInformation.legacyMusic.album.albumName != updateInformation.newAlbumName) {
            InformationToSave(
                album = getOrCreateAlbum(
                    artist = updateInformation.legacyMusic.artists.first(),
                    updateInformation = updateInformation,
                ),
                primaryArtist = updateInformation.legacyMusic.artists.first(),
                secondaryArtists = updateInformation.legacyMusic.artists.drop(1),
            )
        } else {
            InformationToSave(
                album = updateInformation.legacyMusic.album,
                primaryArtist = updateInformation.legacyMusic.artists.first(),
                secondaryArtists = updateInformation.legacyMusic.artists.drop(1),
            )
        }

        val updatedMusic = buildNewMusic(
            updateInformation = updateInformation,
            informationToSave = informationToSave,
        )
        musicRepository.upsert(updatedMusic)

        // Check if we can delete legacy album and artists if empty
        deleteAlbumIfEmptyUseCase(albumId = updateInformation.legacyMusic.album.albumId)
        updateInformation.legacyMusic.artists.forEach { artist ->
            commonArtistUseCase.deleteIfEmpty(
                artistId = artist.artistId,
            )
        }
        musicFileUpdater.updateMusic(music = updatedMusic)
        return updatedMusic
    }

    data class UpdateInformation(
        val legacyMusic: Music,
        val newName: String,
        val newAlbumName: String,
        val newCover: Cover,
        val newAlbumPosition: Int?,
        val newAlbumArtistName: String,
        private val newArtistsNames: List<String>
    ) {
        val sortedNewArtistsNames: List<String> = buildList {
            newAlbumArtistName.takeIf { it.isNotBlank() }?.let {
                add(it)
            }
            addAll(newArtistsNames)
        }.distinct()
    }

    data class InformationToSave(
        val album: Album,
        val primaryArtist: Artist,
        val secondaryArtists: List<Artist>,
    )
}