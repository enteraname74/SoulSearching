package com.github.enteraname74.soulsearching.features.filemanager.cover

import android.content.Context
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.features.filemanager.util.MusicCoverUtils
import java.io.File

internal class CoverFileManagerAndroidImpl(
    private val context: Context,
): CoverFileManager {

    override fun getCoverFolder(): File {
        val filesDir: File = context.filesDir
        val folder = File(filesDir, COVER_FOLDER)
        if (!folder.exists()) {
            folder.mkdirs()
        }

        return folder
    }

    override fun getCleanFileCoverForMusic(music: Music): Cover.FileCover {
        val albumId = MusicCoverUtils.getAlbumIdFromMusicPath(context, music.path) ?: return Cover.FileCover()
        val path = MusicCoverUtils.getMusicFileCoverPath(context, albumId) ?: return Cover.FileCover()
        return Cover.FileCover(
            initialCoverPath = path,
        )
    }

    companion object {
        private const val COVER_FOLDER = "covers"
    }
}