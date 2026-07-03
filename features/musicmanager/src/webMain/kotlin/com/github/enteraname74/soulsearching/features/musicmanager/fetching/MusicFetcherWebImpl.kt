package com.github.enteraname74.soulsearching.features.musicmanager.fetching

class MusicFetcherWebImpl : MusicFetcher() {
    override suspend fun fetchMusics(updateProgress: (Float, String?) -> Unit) {
        // no-op on web targets
    }

    override suspend fun fetchMusicsFromSelectedFolders(
        alreadyPresentMusicsPaths: List<String>,
        hiddenFoldersPaths: List<String>
    ): List<SelectableMusicItem> = emptyList()
}