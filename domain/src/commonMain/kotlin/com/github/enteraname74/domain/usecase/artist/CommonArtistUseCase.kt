package com.github.enteraname74.domain.usecase.artist

import androidx.paging.PagingData
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistPreview
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.uuid.Uuid

class CommonArtistUseCase(
    private val artistRepository: ArtistRepository,
) {
    fun getAllPaged(): Flow<PagingData<ArtistPreview>> =
        artistRepository.getAllPaged()

    suspend fun getAll(page: Int, pageSize: Int): List<ArtistPreview> =
        artistRepository.getAll(page = page, pageSize = pageSize)

    fun getAllFromQuickAccess(): Flow<List<ArtistPreview>> =
        artistRepository.getAllFromQuickAccess()

    /**
     * Use [DeleteArtistUseCase] for a clean deletion of the songs and albums of an artist.
     */
    suspend fun delete(artist: Artist) {
        artistRepository.delete(
            artist = artist,
        )
    }

    /**
     * Use [DeleteArtistUseCase] for a clean deletion of the songs and albums of artists.
     */
    suspend fun deleteAll(artistsIds: List<Uuid>) {
        artistRepository.deleteAll(
            artistsIds = artistsIds,
        )
    }

    suspend fun deleteIfEmpty(
        artistId: Uuid
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

    fun getArtistWithMusic(artistId: Uuid): Flow<ArtistWithMusics?> =
        artistRepository.getArtistWithMusics(
            artistId = artistId,
        )

    suspend fun getDuplicatedArtist(
        artistId: Uuid,
        artistName: String
    ): ArtistWithMusics? =
        artistRepository.getDuplicatedArtist(
            artistId = artistId,
            artistName = artistName,
        )

    fun getArtistsWithMostMusics(): Flow<List<ArtistPreview>> =
        artistRepository.getArtistsWithMostMusics()

    suspend fun incrementArtistNbPlayed(artistId: Uuid) {
        val artist: Artist = artistRepository.getFromId(artistId).first() ?: return
        artistRepository.upsert(
            artist = artist.copy(
                nbPlayed = artist.nbPlayed + 1,
            )
        )
    }

    fun getFromIds(artistIds: List<Uuid>) : Flow<List<ArtistWithMusics>> =
        artistRepository.getFromIds(artistIds)

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

    fun getMostListened(): Flow<List<ArtistPreview>> =
        artistRepository.getMostListened()

    fun getArtistPreview(artistId: Uuid): Flow<ArtistPreview?> =
        artistRepository.getArtistPreview(artistId)

    fun searchAll(search: String): Flow<List<ArtistPreview>> =
        artistRepository.searchAll(search)

    suspend fun getPotentialMultipleArtists(): List<Artist> =
        artistRepository.getPotentialMultipleArtists()
}
