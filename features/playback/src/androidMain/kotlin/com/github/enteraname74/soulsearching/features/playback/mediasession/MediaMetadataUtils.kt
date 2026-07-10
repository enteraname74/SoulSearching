package com.github.enteraname74.soulsearching.features.playback.mediasession

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.scale
import androidx.media3.common.MediaMetadata
import com.github.enteraname74.domain.model.AlbumPreview
import com.github.enteraname74.domain.model.ArtistPreview
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicFolderPreview
import com.github.enteraname74.domain.model.PlaylistPreview
import com.github.enteraname74.soulsearching.features.playback.R
import java.io.ByteArrayOutputStream

class MediaMetadataUtils(
    context: Context,
) {
    private val standardNotificationBitmap: Bitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.new_notification_default)
            .scale(DEFAULT_NOTIFICATION_SIZE, DEFAULT_NOTIFICATION_SIZE, false)

    fun fromMusic(
        music: Music,
        cover: Bitmap?,
    ): MediaMetadata.Builder =
        MediaMetadata.Builder()
            .setArtworkData(
                (cover ?: standardNotificationBitmap).toPngBytes(),
                MediaMetadata.PICTURE_TYPE_FRONT_COVER,
            )
            .setTitle(music.name)
            .setDisplayTitle(music.name)
            .setArtist(music.artistsNames)
            .setAlbumTitle(music.album.albumName)
            .setAlbumArtist(music.album.artist.artistName)
            .setDurationMs(music.duration)
            .setIsBrowsable(false)
            .setIsPlayable(true)
            .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)

    fun fromAlbum(
        album: AlbumPreview,
        cover: Bitmap?,
    ): MediaMetadata.Builder =
        browsableMetadata(
            title = album.name,
            cover = cover,
            mediaType = MediaMetadata.MEDIA_TYPE_ALBUM,
        )
            .setArtist(album.artist)
            .setAlbumTitle(album.name)
            .setAlbumArtist(album.artist)

    fun fromArtist(
        artist: ArtistPreview,
        cover: Bitmap?,
    ): MediaMetadata.Builder =
        browsableMetadata(
            title = artist.name,
            cover = cover,
            mediaType = MediaMetadata.MEDIA_TYPE_ARTIST,
        )
            .setArtist(artist.name)

    fun fromPlaylist(
        playlist: PlaylistPreview,
        cover: Bitmap?,
    ): MediaMetadata.Builder =
        browsableMetadata(
            title = playlist.name,
            cover = cover,
            mediaType = MediaMetadata.MEDIA_TYPE_PLAYLIST,
        )

    fun fromFolder(
        folder: MusicFolderPreview,
        cover: Bitmap?,
    ): MediaMetadata.Builder =
        browsableMetadata(
            title = folder.name,
            cover = cover,
            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED,
        )

    private fun browsableMetadata(
        title: String,
        cover: Bitmap?,
        mediaType: Int,
    ): MediaMetadata.Builder =
        MediaMetadata.Builder()
            .setArtworkData(
                (cover ?: standardNotificationBitmap).toPngBytes(),
                MediaMetadata.PICTURE_TYPE_FRONT_COVER,
            )
            .setTitle(title)
            .setDisplayTitle(title)
            .setIsBrowsable(true)
            .setIsPlayable(false)
            .setMediaType(mediaType)

    private fun Bitmap.toPngBytes(): ByteArray =
        ByteArrayOutputStream().use { outputStream ->
            compress(Bitmap.CompressFormat.PNG, BITMAP_COMPRESS_QUALITY, outputStream)
            outputStream.toByteArray()
        }

    private companion object {
        const val DEFAULT_NOTIFICATION_SIZE: Int = 300
        const val BITMAP_COMPRESS_QUALITY: Int = 100
    }
}
