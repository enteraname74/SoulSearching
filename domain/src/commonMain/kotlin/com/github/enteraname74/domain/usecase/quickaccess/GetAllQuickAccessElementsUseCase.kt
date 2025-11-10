package com.github.enteraname74.domain.usecase.quickaccess

import com.github.enteraname74.domain.model.QuickAccessible
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetAllQuickAccessElementsUseCase(
    private val commMusicUseCase: CommonMusicUseCase,
    private val commonArtistUseCase: CommonArtistUseCase,
    private val commonAlbumUseCase: CommonAlbumUseCase,
    private val commonPlaylistUseCase: CommonPlaylistUseCase,
) {
    operator fun invoke(): Flow<List<QuickAccessible>> = combine(
        commMusicUseCase.getAllFromQuickAccess(),
        commonArtistUseCase.getAllFromQuickAccess(),
        commonAlbumUseCase.getAllFromQuickAccess(),
        commonPlaylistUseCase.getAllFromQuickAccess(),
    ) { musics, artists, albums, playlists ->
        buildList {
            addAll(musics)
            addAll(artists)
            addAll(albums)
            addAll(playlists)
        }
    }
}