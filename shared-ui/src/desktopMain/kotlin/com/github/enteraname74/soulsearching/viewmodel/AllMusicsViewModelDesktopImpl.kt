package com.github.enteraname74.soulsearching.viewmodel

import com.github.enteraname74.domain.model.SoulSearchingSettings
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.music.ToggleMusicFavoriteStatusUseCase
import com.github.enteraname74.domain.usecase.music.UpsertMusicUseCase
import com.github.enteraname74.domain.usecase.musicalbum.GetAlbumIdFromMusicIdUseCase
import com.github.enteraname74.domain.usecase.musicartist.GetArtistIdFromMusicIdUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.soulsearching.domain.model.MusicFetcher
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.AllMusicsViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager

class AllMusicsViewModelDesktopImpl(
    settings: SoulSearchingSettings,
    playbackManager: PlaybackManager,
    getAllMusicsSortedUseCase: GetAllMusicsSortedUseCase,
    getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    getArtistIdFromMusicIdUseCase: GetArtistIdFromMusicIdUseCase,
    getAlbumIdFromMusicIdUseCase: GetAlbumIdFromMusicIdUseCase,
    musicFetcher: MusicFetcher,
    deleteMusicUseCase: DeleteMusicUseCase,
    toggleMusicFavoriteStatusUseCase: ToggleMusicFavoriteStatusUseCase,
    upsertMusicUseCase: UpsertMusicUseCase,
) : AllMusicsViewModel(
    settings = settings,
    musicFetcher = musicFetcher,
    playbackManager = playbackManager,
    getAllMusicsSortedUseCase = getAllMusicsSortedUseCase,
    getAllPlaylistWithMusicsUseCase = getAllPlaylistWithMusicsUseCase,
    getArtistIdFromMusicIdUseCase = getArtistIdFromMusicIdUseCase,
    getAlbumIdFromMusicIdUseCase = getAlbumIdFromMusicIdUseCase,
    deleteMusicUseCase = deleteMusicUseCase,
    toggleMusicFavoriteStatusUseCase = toggleMusicFavoriteStatusUseCase,
    upsertMusicUseCase = upsertMusicUseCase,
) {
    override fun checkAndDeleteMusicIfNotExist() {}
}