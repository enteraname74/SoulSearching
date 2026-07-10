package com.github.enteraname74.soulsearching.features.playback.mediasession

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.scale
import androidx.media3.common.MediaMetadata
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.features.playback.R
import java.io.ByteArrayOutputStream

class MediaItemUtils(
    context: Context,
) {
    private val standardNotificationBitmap: Bitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.new_notification_default)
            .scale(DEFAULT_NOTIFICATION_SIZE, DEFAULT_NOTIFICATION_SIZE, false)

    fun metadataBuilderFromMusic(
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