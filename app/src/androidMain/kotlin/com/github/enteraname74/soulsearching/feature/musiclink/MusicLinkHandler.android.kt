package com.github.enteraname74.soulsearching.feature.musiclink

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.soulsearching.features.musicmanager.ext.toMusic
import androidx.core.net.toUri
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.flow.firstOrNull

actual class MusicLinkHandler(
    private val context: Context,
    private val commonMusicUseCase: CommonMusicUseCase,
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

            println("CLUELESS -- built music: $builtMusic")
            var fromPath: Music? = commonMusicUseCase.getFromPath(path = builtMusic.path)

            if (fromPath == null) {
                println("CLUELESS -- built music does not already exist!")
                commonMusicUseCase.upsert(builtMusic)
                fromPath = commonMusicUseCase.getFromPath(path = builtMusic.path) ?: return@withLoading
            }

            println("CLUELESS -- got existing music, will play")

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