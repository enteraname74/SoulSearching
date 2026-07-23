package com.github.enteraname74.domain.usecase.musicartist

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.repository.MusicArtistRepository
import kotlin.uuid.Uuid

class CommonMusicArtistUseCase(
    private val musicArtistRepository: MusicArtistRepository,
) {
    suspend fun upsertAll(allMusicArtists: List<MusicArtist>) {
        musicArtistRepository.upsertAll(allMusicArtists)
    }

    suspend fun deleteOfArtist(artistId: Uuid) {
        musicArtistRepository.deleteOfArtist(artistId)
    }

    suspend fun setArtistsOfMusic(
        musicId: Uuid,
        artistIds: List<Uuid>
    ) {
        musicArtistRepository.deleteOfMusic(musicId)
        musicArtistRepository.upsertAll(
            musicArtists = artistIds.map {
                MusicArtist(
                    musicId = musicId,
                    artistId = it,
                )
            }
        )
    }
}
