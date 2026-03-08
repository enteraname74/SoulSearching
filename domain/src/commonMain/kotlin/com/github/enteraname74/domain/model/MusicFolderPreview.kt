package com.github.enteraname74.domain.model

import java.io.File

data class MusicFolderPreview(
    val folder: String,
    val cover: Cover,
    val totalMusics: Int,
) {
    val name: String = File(folder).name
}
