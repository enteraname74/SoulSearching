package com.github.enteraname74.soulsearching.feature.search.musics

import kotlin.uuid.Uuid

interface SearchMusicsNavScope {
    fun navigateBack()
    fun showMusicBottomSheet(musicId: Uuid)
}