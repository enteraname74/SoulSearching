package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

import com.github.enteraname74.soulsearching.domain.model.MusicFolderPreview

data class AllMusicFoldersState(
    val allMusicFolders: List<MusicFolderPreview> = emptyList(),
)
