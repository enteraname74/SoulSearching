package com.github.enteraname74.soulsearching.feature.musiclink

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toUri
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.folder.CommonFolderUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.features.musicmanager.ext.toMusic
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager

actual class MusicLinkHandler(
    private val context: Context,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val commonFolderUseCase: CommonFolderUseCase,
    private val playbackManager: PlaybackManager,
    private val loadingManager: LoadingManager,
    private val playerViewManager: PlayerViewManager,
) {
    actual suspend fun handleLink(link: String) {
        loadingManager.withLoading {
            val builtMusic: Music = getCursor(link.toUri())?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.toMusic()
                } else {
                    null
                }
            } ?: return@withLoading

            var fromPath: Music? = builtMusic.localPath?.let { commonMusicUseCase.getFromPath(path = it) }

            if (fromPath == null) {
                commonMusicUseCase.upsert(builtMusic)
                commonFolderUseCase.upsertAll(
                    allFolders = listOf(
                        Folder(
                            folderPath = builtMusic.folder,
                            isSelected = true,
                        )
                    )
                )
                fromPath = builtMusic.localPath?.let { commonMusicUseCase.getFromPath(path = it) } ?: return@withLoading
            }

            playbackManager.setCurrentPlaylistAndMusic(
                music = fromPath,
                musicList = listOf(fromPath),
                playlistId = null,
                isMainPlaylist = false,
            )
            playerViewManager.animateTo(BottomSheetStates.EXPANDED)
        }
    }

    private fun getCursor(uri: Uri): Cursor? {
        val projection: Array<String> = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.ALBUM_ARTIST,
        )

        val id = ContentUris.parseId(uri)

        val selection = "${MediaStore.Audio.Media._ID}=?"
        val selectionArgs = arrayOf(id.toString())

        return context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )
    }

}
