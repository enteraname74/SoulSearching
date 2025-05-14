package com.github.enteraname74.soulsearching.features.musicmanager.domain

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Holds optimized data access to fetched elements for quick access.
 */
data class OptimizedCachedData(
    var musicsByPath: HashMap<String, Music> = hashMapOf(),
    var artistsByName: HashMap<String, Artist> = hashMapOf(),
    var albumsByInfo: HashMap<AlbumInformation, Album> = hashMapOf(),
    var musicArtists: ArrayList<MusicArtist> = arrayListOf(),
) {
    fun clear() {
        musicsByPath.clear()
        artistsByName.clear()
        albumsByInfo.clear()
        musicArtists.clear()
    }

    companion object : KoinComponent {
        private val commonMusicUseCase: CommonMusicUseCase by inject()
        private val commonArtistUseCase: CommonArtistUseCase by inject()
        private val commonAlbumUseCase: CommonAlbumUseCase by inject()

        suspend fun fromDb(): OptimizedCachedData = OptimizedCachedData(
            musicsByPath = commonMusicUseCase.getAll().first().associateBy { it.path } as HashMap<String, Music>,
            artistsByName =
            commonArtistUseCase.getAll().first().associateBy { it.artistName } as HashMap<String, Artist>,
            albumsByInfo = commonAlbumUseCase.getAllAlbumsWithArtist().first().associate {
                AlbumInformation(
                    name = it.album.albumName,
                    artist = it.artist?.artistName.orEmpty(),
                ) to it.album
            } as HashMap<AlbumInformation, Album>,
        )
    }
}


