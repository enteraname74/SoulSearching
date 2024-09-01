package com.github.enteraname74.soulsearching.model.utils

import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.provider.MediaStore
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.usecase.album.GetCorrespondingAlbumUseCase
import com.github.enteraname74.domain.usecase.album.UpdateAlbumCoverUseCase
import com.github.enteraname74.domain.usecase.album.UpsertAlbumUseCase
import com.github.enteraname74.domain.usecase.albumartist.UpsertAlbumArtistUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistFromNameUseCase
import com.github.enteraname74.domain.usecase.artist.UpdateArtistCoverUseCase
import com.github.enteraname74.domain.usecase.artist.UpsertArtistUseCase
import com.github.enteraname74.domain.usecase.folder.UpsertFolderUseCase
import com.github.enteraname74.domain.usecase.imagecover.GetCoverOfElementUseCase
import com.github.enteraname74.domain.usecase.imagecover.UpsertImageCoverUseCase
import com.github.enteraname74.domain.usecase.music.IsMusicAlreadySavedUseCase
import com.github.enteraname74.domain.usecase.music.UpsertMusicUseCase
import com.github.enteraname74.domain.usecase.musicalbum.UpsertMusicIntoAlbumUseCase
import com.github.enteraname74.domain.usecase.musicartist.UpsertMusicIntoArtistUseCase
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
import java.io.IOException
import java.util.*

/**
 * Class handling music fetching for Android devices.
 */
class MusicFetcherAndroidImpl(
    private val context: Context,
    private val upsertPlaylistUseCase: UpsertPlaylistUseCase,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    isMusicAlreadySavedUseCase: IsMusicAlreadySavedUseCase,
    getArtistFromNameUseCase: GetArtistFromNameUseCase,
    getCorrespondingAlbumUseCase: GetCorrespondingAlbumUseCase,
    upsertImageCoverUseCase: UpsertImageCoverUseCase,
    upsertAlbumUseCase: UpsertAlbumUseCase,
    upsertArtistUseCase: UpsertArtistUseCase,
    upsertAlbumArtistUseCase: UpsertAlbumArtistUseCase,
    getCoverOfElementUseCase: GetCoverOfElementUseCase,
    updateAlbumCoverUseCase: UpdateAlbumCoverUseCase,
    updateArtistCoverUseCase: UpdateArtistCoverUseCase,
    upsertMusicUseCase: UpsertMusicUseCase,
    upsertFolderUseCase: UpsertFolderUseCase,
    upsertMusicIntoAlbumUseCase: UpsertMusicIntoAlbumUseCase,
    upsertMusicIntoArtistUseCase: UpsertMusicIntoArtistUseCase,
): MusicFetcher(
    isMusicAlreadySavedUseCase = isMusicAlreadySavedUseCase,
    getArtistFromNameUseCase = getArtistFromNameUseCase,
    getCorrespondingAlbumUseCase = getCorrespondingAlbumUseCase,
    upsertImageCoverUseCase = upsertImageCoverUseCase,
    upsertAlbumUseCase = upsertAlbumUseCase,
    upsertArtistUseCase = upsertArtistUseCase,
    getCoverOfElementUseCase = getCoverOfElementUseCase,
    upsertMusicIntoAlbumUseCase = upsertMusicIntoAlbumUseCase,
    upsertFolderUseCase = upsertFolderUseCase,
    upsertMusicIntoArtistUseCase = upsertMusicIntoArtistUseCase,
    upsertMusicUseCase = upsertMusicUseCase,
    updateArtistCoverUseCase = updateArtistCoverUseCase,
    upsertAlbumArtistUseCase = upsertAlbumArtistUseCase,
    updateAlbumCoverUseCase = updateAlbumCoverUseCase,
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
            MediaStore.Audio.Albums.ALBUM_ID
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
    private fun fetchMusicCoverFromCursorElement(cursor: Cursor): ImageBitmap? {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        return try {
            mediaMetadataRetriever.setDataSource(cursor.getString(4))
            try {
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
                    ).asImageBitmap()
                }
            } catch (error: IOException) {
                null
            }
        } catch (error: java.lang.RuntimeException) {
            null
        }
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
                    val cover: ImageBitmap? = fetchMusicCoverFromCursorElement(cursor = cursor)

                    val music = Music(
                        name = cursor.getString(0).trim(),
                        album = cursor.getString(2).trim(),
                        artist = cursor.getString(1).trim(),
                        duration = cursor.getLong(3),
                        path = cursor.getString(4),
                        folder = File(cursor.getString(4)).parent ?: ""
                    )
                    addMusic(music, cover)
                    cover?.asAndroidBitmap()?.recycle()
                    count++
                    updateProgress((count * 1F) / cursor.count, null)
                }
                cursor.close()
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
                        val albumCover: ImageBitmap? = fetchMusicCoverFromCursorElement(cursor = cursor)

                        val music = Music(
                            name = cursor.getString(0).trim(),
                            album = cursor.getString(2).trim(),
                            artist = cursor.getString(1).trim(),
                            duration = cursor.getLong(3),
                            path = cursor.getString(4),
                            folder = File(cursor.getString(4)).parent ?: ""
                        )
                        newMusics.add(
                            SelectableMusicItem(
                                music = music,
                                cover = albumCover,
                                isSelected = true
                            )
                        )
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