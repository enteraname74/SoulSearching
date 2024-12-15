package com.github.enteraname74.soulsearching.features.musicmanager.fetching

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.usecase.playlist.GetFavoritePlaylistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.playlist.UpsertPlaylistUseCase
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

/**
 * Class handling music fetching for Android devices.
 */
internal class MusicFetcherAndroidImpl(
    private val context: Context,
    private val upsertPlaylistUseCase: UpsertPlaylistUseCase,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    private val getFavoritePlaylistWithMusicsUseCase: GetFavoritePlaylistWithMusicsUseCase,
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


    private fun Cursor.toMusic(): Music? =
        try {
            Music(
                name = this.getString(0).trim(),
                album = this.getString(2).trim(),
                artist = this.getString(1).trim(),
                duration = this.getLong(3),
                path = this.getString(4),
                folder = File(this.getString(4)).parent ?: "",
                cover = Cover.CoverFile(initialCoverPath = this.getString(4)),
            )
        } catch (e: Exception) {
            println("MusicFetcher -- Exception while fetching song on the device: $e")
            null
        }

    override suspend fun fetchMusics(
        updateProgress: (Float, String?) -> Unit,
    ): Boolean {
        val cursor = buildMusicCursor()

        return when (cursor?.count) {
            null -> {
                feedbackPopUpManager.showFeedback(
                    feedback = strings.cannotRetrieveSongs
                )
                true
            }

            else -> {
                var count = 0
                while (cursor.moveToNext()) {
                    try {
                        val music: Music? = cursor.toMusic()
                        music?.let { addMusic(musicToAdd = it) }
                    } catch (e: Exception) {
                        println("MusicFetcher -- Exception while saving song: $e")
                    }
                    count++
                    updateProgress((count * 1F) / cursor.count, null)
                }
                cursor.close()
                if (getFavoritePlaylistWithMusicsUseCase().first() == null) {
                    upsertPlaylistUseCase(
                        Playlist(
                            playlistId = UUID.randomUUID(),
                            name = strings.favorite,
                            isFavorite = true
                        )
                    )
                }
                saveAllWithMultipleArtistsCheck()
            }
        }
    }

    /**
     * Fetch new musics.
     */
    override suspend fun fetchMusicsFromSelectedFolders(
        alreadyPresentMusicsPaths: List<String>,
        hiddenFoldersPaths: List<String>
    ): List<SelectableMusicItem> {
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