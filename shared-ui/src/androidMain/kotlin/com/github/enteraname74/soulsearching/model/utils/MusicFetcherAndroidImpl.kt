package com.github.enteraname74.soulsearching.model.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import androidx.core.database.getLongOrNull
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.usecase.playlist.UpsertPlaylistUseCase
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.model.MusicFetcher
import com.github.enteraname74.soulsearching.domain.model.SelectableMusicItem
import com.github.soulsearching.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

/**
 * Class handling music fetching for Android devices.
 */
class MusicFetcherAndroidImpl(
    private val context: Context,
    private val upsertPlaylistUseCase: UpsertPlaylistUseCase,
    private val feedbackPopUpManager: FeedbackPopUpManager,
) : MusicFetcher() {
    /**
     * Build a cursor for fetching musics on device.
     */
    private fun buildMusicCursor(): Cursor? {
        val projection: Array<String> = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Albums.ALBUM_ID,
        )

        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        return context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )
    }

    private fun getMusicFileCoverPath(albumId: Long): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above, use the content URI
            ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId).toString()
        } else {
            // For older versions, use the direct file path via ALBUM_ART
            getMusicFileCoverPathForOldDevices(albumId)
        }
    }

    private fun getMusicFileCoverPathForOldDevices(albumId: Long): String? {
        val projection = arrayOf(MediaStore.Audio.Albums.ALBUM_ART)
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)
        var albumArtPath: String? = null

        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                albumArtPath = it.getString(it.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
            }
        }
        return albumArtPath
    }

    private fun Cursor.toMusic(): Music? =
        try {
            Music(
                name = this.getString(0).trim(),
                album = this.getString(2).trim(),
                artist = this.getString(1).trim(),
                duration = this.getLong(3),
                path = this.getString(4),
                folder = File(this.getString(4)).parent ?: "",
                cover = Cover.FileCover(
//                    initialCoverPath = this.getString(4)
                    initialCoverPath = this.getLongOrNull(5)?.let(::getMusicFileCoverPath)
                ),
            )
        } catch (e: Exception) {
            println("MusicFetcher -- Exception while fetching song on the device: $e")
            null
        }

    override suspend fun fetchMusics(
        updateProgress: (Float, String?) -> Unit,
        finishAction: () -> Unit
    ) {
        val cursor = buildMusicCursor()

        when (cursor?.count) {
            null -> {
                feedbackPopUpManager.showFeedback(
                    feedback = strings.cannotRetrieveSongs
                )
            }

            else -> {
                var count = 0
                while (cursor.moveToNext()) {
                    try {
                        val music: Music? = cursor.toMusic()
                        music?.let {
                            addMusic(musicToAdd = it)
                        }
                    } catch (e: Exception) {
                        println("MusicFetcher -- Exception while saving song: $e")
                    }
                    count++
                    updateProgress((count * 1F) / cursor.count, null)
                }
                cursor.close()
                saveAll()
                upsertPlaylistUseCase(
                    Playlist(
                        playlistId = UUID.randomUUID(),
                        name = context.getString(R.string.favorite),
                        isFavorite = true
                    )
                )
                finishAction()
            }
        }
    }

    /**
     * Fetch new musics.
     */
    override suspend fun fetchMusicsFromSelectedFolders(
        alreadyPresentMusicsPaths: List<String>,
        hiddenFoldersPaths: List<String>
    ): ArrayList<SelectableMusicItem> {
        val newMusics = ArrayList<SelectableMusicItem>()
        val cursor = buildMusicCursor()

        when (cursor?.count) {
            null -> {
                CoroutineScope(Dispatchers.Main).launch {
                    feedbackPopUpManager.showFeedback(
                        feedback = strings.cannotRetrieveSongs
                    )
                }
            }

            else -> {
                while (cursor.moveToNext()) {
                    val musicPath = cursor.getString(4)
                    val musicFolder = File(musicPath).parent ?: ""
                    if (!alreadyPresentMusicsPaths.any { it == musicPath } && !hiddenFoldersPaths.any { it == musicFolder }) {
                        try {
                            val music: Music? = cursor.toMusic()
                            music?.let {
                                newMusics.add(
                                    SelectableMusicItem(
                                        music = it,
                                        isSelected = true
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            println("MusicFetcher -- Exception while fetching song: $e")
                        }
                    }
                }
                cursor.close()
            }
        }
        return newMusics
    }
}