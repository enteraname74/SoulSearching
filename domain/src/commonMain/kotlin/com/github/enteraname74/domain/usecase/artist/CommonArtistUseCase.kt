package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

class CommonArtistUseCase(
    private val artistRepository: ArtistRepository,
) {
    fun getAll(): Flow<List<Artist>> =
        artistRepository.getAll()

    fun getAllFromQuickAccess(): Flow<List<ArtistWithMusics>> =
        artistRepository.getAllArtistWithMusics().map { list ->
            list.filter { it.artist.isInQuickAccess }
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

    fun getAllSortedByMostSongs(): Flow<List<ArtistWithMusics>> =
        artistRepository
            .getAllArtistWithMusics()
            .map { list ->
                list.sortedByDescending { it.musics.size }
            }
}