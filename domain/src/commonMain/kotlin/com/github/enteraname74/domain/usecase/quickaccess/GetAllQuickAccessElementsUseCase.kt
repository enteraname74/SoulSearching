package com.github.enteraname74.domain.usecase.quickaccess

import com.github.enteraname74.domain.model.QuickAccessible
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.GetAllArtistWithMusicsFromQuickAccessUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicFromQuickAccessUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsNumberFromQuickAccessUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetAllQuickAccessElementsUseCase(
    private val getAllMusicFromQuickAccessUseCase: GetAllMusicFromQuickAccessUseCase,
    private val getAllArtistWithMusicsFromQuickAccessUseCase: GetAllArtistWithMusicsFromQuickAccessUseCase,
    private val commonAlbumUseCase: CommonAlbumUseCase,
    private val getAllPlaylistWithMusicsNumberFromQuickAccessUseCase: GetAllPlaylistWithMusicsNumberFromQuickAccessUseCase
) {
    operator fun invoke(): Flow<List<QuickAccessible>> = combine(
        getAllMusicFromQuickAccessUseCase(),
        getAllArtistWithMusicsFromQuickAccessUseCase(),
        commonAlbumUseCase.getAllFromQuickAccess(),
        getAllPlaylistWithMusicsNumberFromQuickAccessUseCase()
    ) { musics, artists, albums, playlists ->
        buildList {
            addAll(musics)
            addAll(artists)
            addAll(albums)
            addAll(playlists)
        }
    }
}