package com.github.enteraname74.domain.model

import com.github.enteraname74.domain.ext.coverFromSongs
import java.io.File
import kotlin.uuid.Uuid

/**
 * Represent a list of musics from a folder.
 */
data class MusicFolderList(
    val path: String,
    val musics: List<Music>,
) {
    val name: String = File(path).name
    val cover: Cover? = musics.coverFromSongs()
}
