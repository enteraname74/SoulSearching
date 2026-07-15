package com.github.enteraname74.soulsearching.features.musicmanager.fetching

import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase

class MusicFetcherWebImpl(
    commonPlaylistUseCase: CommonPlaylistUseCase,
) : MusicFetcher(commonPlaylistUseCase) {
    override suspend fun fetchMusics(updateProgress: (Float, String?) -> Unit) {
        println("CLUELESS -- FOZBFPIUZBFP%OABIOE%IGUBE")
        ensureFavoritePlaylistCreated()
    }

    override suspend fun fetchMusicsFromSelectedFolders(
        alreadyPresentMusicsPaths: List<String>,
        hiddenFoldersPaths: List<String>
    ): List<SelectableMusicItem> = emptyList()
}