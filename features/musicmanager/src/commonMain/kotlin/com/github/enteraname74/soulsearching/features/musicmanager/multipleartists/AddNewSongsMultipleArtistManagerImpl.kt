package com.github.enteraname74.soulsearching.features.musicmanager.multipleartists

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.usecase.album.DeleteAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.DeleteArtistUseCase
import com.github.enteraname74.soulsearching.features.musicmanager.domain.OptimizedCachedData
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

/**
 * In this format, we need to write to the database when needed.
 */
class AddNewSongsMultipleArtistManagerImpl(
    optimizedCachedData: OptimizedCachedData,
): FetchAllMultipleArtistManagerImpl(
    optimizedCachedData = optimizedCachedData,
), KoinComponent {
    private val deleteArtistUseCase: DeleteArtistUseCase by inject()
    private val deleteAlbumUseCase: DeleteAlbumUseCase by inject()

    override suspend fun deleteArtist(
        artist: Artist,
        musicIdsOfInitialArtist: List<UUID>,
        albumIdsOfInitialArtist: List<UUID>
    ) {
        super.deleteArtist(artist, musicIdsOfInitialArtist, albumIdsOfInitialArtist)
        deleteArtistUseCase(artistId = artist.artistId)
    }

    override suspend fun moveSongsOfAlbum(fromAlbum: Album, toAlbum: Album, multipleArtistName: String) {
        super.moveSongsOfAlbum(fromAlbum, toAlbum, multipleArtistName)
        deleteAlbumUseCase.onlyAlbum(albumId = fromAlbum.albumId)
    }
}