package com.github.enteraname74.soulsearching.domain.model

import com.github.enteraname74.soulsearching.domain.ext.filenameFromPath

data class MusicFolderPreview(
    val folder: String,
    val cover: Cover,
    val totalMusics: Int,
) {
    val name: String = folder.filenameFromPath()
}
