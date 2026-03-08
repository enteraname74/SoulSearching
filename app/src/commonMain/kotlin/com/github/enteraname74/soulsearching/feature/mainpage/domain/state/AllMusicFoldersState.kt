package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.domain.model.MusicFolderList
import com.github.enteraname74.domain.model.MusicFolderPreview

data class AllMusicFoldersState(
    val allMusicFolders: List<MusicFolderPreview> = emptyList(),
)
