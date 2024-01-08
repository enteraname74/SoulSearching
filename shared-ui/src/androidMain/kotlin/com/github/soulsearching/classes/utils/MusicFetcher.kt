package com.github.soulsearching.classes.utils

import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.github.enteraname74.model.Album
import com.github.enteraname74.model.AlbumArtist
import com.github.enteraname74.model.Artist
import com.github.enteraname74.model.Folder
import com.github.enteraname74.model.ImageCover
import com.github.enteraname74.model.Music
import com.github.enteraname74.model.MusicAlbum
import com.github.enteraname74.model.MusicArtist
import com.github.enteraname74.model.Playlist
import com.github.enteraname74.repository.AlbumArtistRepository
import com.github.enteraname74.repository.AlbumRepository
import com.github.enteraname74.repository.ArtistRepository
import com.github.enteraname74.repository.FolderRepository
import com.github.enteraname74.repository.ImageCoverRepository
import com.github.enteraname74.repository.MusicAlbumRepository
import com.github.enteraname74.repository.MusicArtistRepository
import com.github.enteraname74.repository.MusicRepository
import com.github.enteraname74.repository.PlaylistRepository
import com.github.soulsearching.R
import com.github.soulsearching.classes.SelectableMusicItem
import java.io.File
import java.io.IOException
import java.util.UUID

/**
 * Class handling music fetching.
 */
class MusicFetcher(
    private val context: Context,
    private val musicRepository: MusicRepository,
    private val playlistRepository: PlaylistRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val musicAlbumRepository: MusicAlbumRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val albumArtistRepository: AlbumArtistRepository,
    private val imageCoverRepository: ImageCoverRepository,
    private val folderRepository: FolderRepository
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
                        Utils.BITMAP_SIZE,
                        Utils.BITMAP_SIZE
                    ).asImageBitmap()
                }
            } catch (error: IOException) {
                null
            }
        } catch (error: java.lang.RuntimeException) {
            null
        }
    }

    /**
     * Fetch all musics.
     */
    suspend fun fetchMusics(
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
    fun fetchNewMusics(
        updateProgress: (Float) -> Unit,
        alreadyPresentMusicsPaths: List<String>,
        hiddenFoldersPaths: List<String>
    ) : ArrayList<SelectableMusicItem> {
        val newMusics = ArrayList<SelectableMusicItem>()

        val projection: Array<String> = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Albums.ALBUM_ID
        )

        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )
        Log.d("FETCHING MUSIC", "CURSOR : ${cursor?.count}")
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

    /**
     * Persist a music and its cover.
     */
    private suspend fun addMusic(musicToAdd: Music, musicCover: ImageBitmap?) {
        // Si la musique a déjà été enregistrée, on ne fait rien :
        val existingMusic = musicRepository.getMusicFromPath(musicToAdd.path)
        if (existingMusic != null) {
            return
        }

        val correspondingArtist = artistRepository.getArtistFromInfo(
            artistName = musicToAdd.artist
        )
        // Si l'artiste existe, on regarde si on trouve un album correspondant :
        val correspondingAlbum = if (correspondingArtist == null) {
            null
        } else {
            albumRepository.getCorrespondingAlbum(
                albumName = musicToAdd.album,
                artistId = correspondingArtist.artistId
            )
        }
        val albumId = correspondingAlbum?.albumId ?: UUID.randomUUID()
        val artistId = correspondingArtist?.artistId ?: UUID.randomUUID()
        if (correspondingAlbum == null) {
            val coverId = UUID.randomUUID()
            if (musicCover != null) {
                musicToAdd.coverId = coverId
                imageCoverRepository.insertImageCover(
                    ImageCover(
                        coverId = coverId,
                        cover = musicCover
                    )
                )
            }

            albumRepository.insertAlbum(
                Album(
                    coverId = if (musicCover != null) coverId else null,
                    albumId = albumId,
                    albumName = musicToAdd.album
                )
            )
            artistRepository.insertArtist(
                Artist(
                    coverId = if (musicCover != null) coverId else null,
                    artistId = artistId,
                    artistName = musicToAdd.artist
                )
            )
            albumArtistRepository.insertAlbumIntoArtist(
                AlbumArtist(
                    albumId = albumId,
                    artistId = artistId
                )
            )
        } else {
            // On ajoute si possible la couverture de l'album de la musique :
            val albumCover = if (correspondingAlbum.coverId != null) {
                imageCoverRepository.getCoverOfElement(coverId = correspondingAlbum.coverId!!)
            } else {
                null
            }
            val shouldPutAlbumCoverWithMusic = (albumCover != null)
            val shouldUpdateArtistCover =
                (correspondingArtist?.coverId == null) && ((albumCover != null) || (musicCover != null))

            if (shouldPutAlbumCoverWithMusic) {
                musicToAdd.coverId = albumCover?.coverId
            } else if (musicCover != null) {
                val coverId = UUID.randomUUID()
                musicToAdd.coverId = coverId
                imageCoverRepository.insertImageCover(
                    ImageCover(
                        coverId = coverId,
                        cover = musicCover
                    )
                )
                // Dans ce cas, l'album n'a pas d'image, on lui en ajoute une :
                albumRepository.updateAlbumCover(
                    newCoverId = coverId,
                    albumId = correspondingAlbum.albumId
                )
            }

            if (shouldUpdateArtistCover) {
                val newArtistCover: UUID? = if (shouldPutAlbumCoverWithMusic) {
                    albumCover?.coverId
                } else {
                    musicToAdd.coverId
                }
                if (correspondingArtist != null && newArtistCover != null) {
                    artistRepository.updateArtistCover(
                        newCoverId = newArtistCover,
                        artistId = correspondingArtist.artistId
                    )
                }
            }
        }
        musicRepository.insertMusic(musicToAdd)
        folderRepository.insertFolder(
            Folder(
                folderPath = musicToAdd.folder
            )
        )
        musicAlbumRepository.insertMusicIntoAlbum(
            MusicAlbum(
                musicId = musicToAdd.musicId,
                albumId = albumId
            )
        )
        musicArtistRepository.insertMusicIntoArtist(
            MusicArtist(
                musicId = musicToAdd.musicId,
                artistId = artistId
            )
        )
    }
}