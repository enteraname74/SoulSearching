package com.github.enteraname74.domain.usecase.artist

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistPreview
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

class CommonArtistUseCase(
    private val artistRepository: ArtistRepository,
) {
    @Deprecated("Avoid fetching all artist from DB because of performance issue")
    fun getAll(): Flow<List<Artist>> =
        artistRepository.getAll()

    fun getAllPaged(): Flow<PagingData<ArtistPreview>> =
        artistRepository.getAllPaged()

    fun getAllFromQuickAccess(): Flow<List<ArtistPreview>> =
        artistRepository.getAllFromQuickAccess()

    suspend fun delete(artist: Artist) {
        artistRepository.delete(
            artist = artist,
        )
    }

    suspend fun deleteAll(artistsIds: List<UUID>) {
        artistRepository.deleteAll(
            artistsIds = artistsIds,
        )
    }

    suspend fun deleteIfEmpty(
        artistId: UUID
    ) {
        val artistWithMusics: ArtistWithMusics =
            artistRepository.getArtistWithMusics(artistId = artistId).first() ?: return

        if (artistWithMusics.musics.isEmpty()) {
            artistRepository.delete(artist = artistWithMusics.artist)
        }
    }

    suspend fun getAllFromName(artistsNames: List<String>): List<Artist> =
        artistRepository.getAllFromName(artistsNames)

    suspend fun getArtistsNameFromSearch(searchString: String): List<String> =
        if (searchString.isBlank()) {
            emptyList()
        } else {
            artistRepository.getArtistNamesContainingSearch(searchString)
        }

    fun getArtistWithMusic(artistId: UUID): Flow<ArtistWithMusics?> =
        artistRepository.getArtistWithMusics(
            artistId = artistId,
        )

    suspend fun getDuplicatedArtist(
        artistId: UUID,
        artistName: String
    ): ArtistWithMusics? =
        artistRepository.getDuplicatedArtist(
            artistId = artistId,
            artistName = artistName,
        )

    fun getStatisticsData(): Flow<List<ArtistPreview>> =
        artistRepository.getStatisticsData()

    suspend fun incrementArtistNbPlayed(artistId: UUID) {
        val artist: Artist = artistRepository.getFromId(artistId).first() ?: return
        artistRepository.upsert(
            artist = artist.copy(
                nbPlayed = artist.nbPlayed + 1,
            )
        )
    }

    suspend fun upsertAll(allArtists: List<Artist>) {
        artistRepository.upsertAll(allArtists)
    }

    suspend fun upsert(artist: Artist) {
        artistRepository.upsert(artist)
    }

    suspend fun toggleCoverFolderMode(isActivated: Boolean) {
        artistRepository.toggleCoverFolderMode(isActivated)
    }

    suspend fun cleanAllCovers() {
        artistRepository.cleanAllCovers()
    }
}