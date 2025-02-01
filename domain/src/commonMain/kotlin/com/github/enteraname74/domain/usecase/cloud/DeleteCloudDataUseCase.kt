package com.github.enteraname74.domain.usecase.cloud

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.repository.*

class DeleteCloudDataUseCase(
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val playlistRepository: PlaylistRepository,
    private val cloudRepository: CloudRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val musicPlaylistRepository: MusicPlaylistRepository,
) {
    suspend operator fun invoke() {
        artistRepository.deleteAll(DataMode.Cloud)
        albumRepository.deleteAll(DataMode.Cloud)
        musicRepository.deleteAll(DataMode.Cloud)
        playlistRepository.deleteAll(DataMode.Cloud)
        musicArtistRepository.deleteAll(DataMode.Cloud)
        musicPlaylistRepository.deleteAll(DataMode.Cloud)

        cloudRepository.deleteCloudCache()
        cloudRepository.clearLastUpdateDate()
    }
}