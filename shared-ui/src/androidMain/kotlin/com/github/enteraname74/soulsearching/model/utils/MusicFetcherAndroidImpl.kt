package com.github.enteraname74.soulsearching.model.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.provider.MediaStore
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.database.getLongOrNull
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.usecase.album.UpsertAllAlbumsUseCase
import com.github.enteraname74.domain.usecase.albumartist.UpsertAllAlbumArtistUseCase
import com.github.enteraname74.domain.usecase.artist.UpsertAllArtistsUseCase
import com.github.enteraname74.domain.usecase.folder.UpsertAllFoldersUseCase
import com.github.enteraname74.domain.usecase.music.UpsertAllMusicsUseCase
import com.github.enteraname74.domain.usecase.musicalbum.UpsertAllMusicAlbumUseCase
import com.github.enteraname74.domain.usecase.musicartist.UpsertAllMusicArtistsUseCase
import com.github.enteraname74.domain.usecase.playlist.UpsertPlaylistUseCase
import com.github.enteraname74.domain.util.CoverFileManager
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.model.MusicFetcher
import com.github.enteraname74.soulsearching.domain.model.SelectableMusicItem
import com.github.soulsearching.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Class handling music fetching for Android devices.
 */
class MusicFetcherAndroidImpl(
    private val context: Context,
    private val upsertPlaylistUseCase: UpsertPlaylistUseCase,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    upsertAllArtistsUseCase: UpsertAllArtistsUseCase,
    upsertAllAlbumsUseCase: UpsertAllAlbumsUseCase,
    upsertAllMusicsUseCase: UpsertAllMusicsUseCase,
    upsertAllFoldersUseCase: UpsertAllFoldersUseCase,
    upsertAllMusicArtistsUseCase: UpsertAllMusicArtistsUseCase,
    upsertAllAlbumArtistUseCase: UpsertAllAlbumArtistUseCase,
    upsertAllMusicAlbumUseCase: UpsertAllMusicAlbumUseCase,
    coverFileManager: CoverFileManager,
): MusicFetcher(
    upsertAllMusicsUseCase = upsertAllMusicsUseCase,
    upsertAllAlbumsUseCase = upsertAllAlbumsUseCase,
    upsertAllArtistsUseCase = upsertAllArtistsUseCase,
    upsertAllFoldersUseCase = upsertAllFoldersUseCase,
    upsertAllMusicAlbumUseCase = upsertAllMusicAlbumUseCase,
    upsertAllAlbumArtistUseCase = upsertAllAlbumArtistUseCase,
    upsertAllMusicArtistsUseCase = upsertAllMusicArtistsUseCase,
    coverFileManager = coverFileManager,
) {
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

    /**
     * Tries to retrieve the cover of a music file in a cursor.
     */
    private fun fetchMusicCoverFromCursorElement(cursor: Cursor): Bitmap? =
        try {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(cursor.getString(4))
            val byteArray = mediaMetadataRetriever.embeddedPicture
            if (byteArray == null) {
                null
            } else {
                val options = BitmapFactory.Options()
                options.inSampleSize = 2
                val tempBitmap = BitmapFactory.decodeByteArray(
                    byteArray,
                    0,
                    byteArray.size,
                    options
                )
                ThumbnailUtils.extractThumbnail(
                    tempBitmap,
                    AndroidUtils.BITMAP_SIZE,
                    AndroidUtils.BITMAP_SIZE
                )
            }
        } catch (_: Exception) {
            null
        }

    private fun getMusicFileCoverPath(albumId: Long): String {
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)
        return uri.toString()
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
                        val music = Music(
                            name = cursor.getString(0).trim(),
                            album = cursor.getString(2).trim(),
                            artist = cursor.getString(1).trim(),
                            duration = cursor.getLong(3),
                            path = cursor.getString(4),
                            folder = File(cursor.getString(4)).parent ?: "",
                            initialCoverPath = cursor.getLongOrNull(5)?.let(::getMusicFileCoverPath)
                        )
                        addMusic(music)
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
        updateProgress: (Float, String?) -> Unit,
        alreadyPresentMusicsPaths: List<String>,
        hiddenFoldersPaths: List<String>
    ) : ArrayList<SelectableMusicItem> {
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
                var count = 0
                while (cursor.moveToNext()) {
                    val musicPath = cursor.getString(4)
                    val musicFolder = File(musicPath).parent ?: ""
                    if (!alreadyPresentMusicsPaths.any { it == musicPath } && !hiddenFoldersPaths.any { it == musicFolder }) {
                        val albumCover: Bitmap? = fetchMusicCoverFromCursorElement(cursor = cursor)

                        try {
                            val music = Music(
                                name = cursor.getString(0).trim(),
                                album = cursor.getString(2).trim(),
                                artist = cursor.getString(1).trim(),
                                duration = cursor.getLong(3),
                                path = cursor.getString(4),
                                folder = File(cursor.getString(4)).parent ?: "",
                                initialCoverPath = cursor.getLongOrNull(5)?.let(::getMusicFileCoverPath),
                            )
                            newMusics.add(
                                SelectableMusicItem(
                                    music = music,
                                    cover = albumCover?.asImageBitmap(),
                                    isSelected = true
                                )
                            )
                        } catch (e: Exception) {
                            println("MusicFetcher -- Exception while fetching song: $e")
                        }
                    }
                    count++
                    updateProgress((count * 1F) / cursor.count, null)
                }
                cursor.close()
            }
        }
        return newMusics
    }
}