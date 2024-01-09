package com.github.soulsearching.classes.utils

import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.R
import com.github.soulsearching.model.MusicFetcher
import com.github.soulsearching.model.SelectableMusicItem
import java.io.File
import java.io.IOException
import java.util.UUID

/**
 * Class handling music fetching for Android devices.
 */
class MusicFetcherAndroidImpl(
    private val context: Context,
    private val playlistRepository: PlaylistRepository,
    musicRepository: MusicRepository,
    albumRepository: AlbumRepository,
    artistRepository: ArtistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    albumArtistRepository: AlbumArtistRepository,
    imageCoverRepository: ImageCoverRepository,
    folderRepository: FolderRepository
): MusicFetcher(
    musicRepository = musicRepository,
    albumRepository = albumRepository,
    artistRepository = artistRepository,
    musicAlbumRepository = musicAlbumRepository,
    musicArtistRepository = musicArtistRepository,
    albumArtistRepository = albumArtistRepository,
    imageCoverRepository = imageCoverRepository,
    folderRepository = folderRepository
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
        updateProgress: (Float) -> Unit,
        finishAction: () -> Unit
    ) {
        val cursor = buildMusicCursor()

        when (cursor?.count) {
            null -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.cannot_retrieve_musics),
                    Toast.LENGTH_SHORT
                ).show()
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
                    updateProgress((count * 1F) / cursor.count)
                }
                cursor.close()
                playlistRepository.insertPlaylist(
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
    override fun fetchMusicsFromSelectedFolders(
        updateProgress: (Float) -> Unit,
        alreadyPresentMusicsPaths: List<String>,
        hiddenFoldersPaths: List<String>
    ) : ArrayList<SelectableMusicItem> {
        val newMusics = ArrayList<SelectableMusicItem>()
        val cursor = buildMusicCursor()

        when (cursor?.count) {
            null -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.cannot_retrieve_musics),
                    Toast.LENGTH_SHORT
                ).show()
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
                    updateProgress((count * 1F) / cursor.count)
                }
                cursor.close()
            }
        }
        return newMusics
    }
}