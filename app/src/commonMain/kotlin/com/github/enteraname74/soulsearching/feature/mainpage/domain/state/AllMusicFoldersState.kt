package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.domain.model.MusicFolderList

data class AllMusicFoldersState(
    val allMusicFolders: List<MusicFolderList> = emptyList(),
)
