package com.github.enteraname74.soulsearching.model.player

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.session.PlaybackState
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import androidx.compose.ui.graphics.asAndroidBitmap
import com.github.soulsearching.R
import com.github.enteraname74.soulsearching.model.playback.PlaybackManagerAndroidImpl
import com.github.enteraname74.soulsearching.model.utils.AndroidUtils

/**
 * Manage media session related things.
 */
class MediaSessionManager(
    private val context: Context,
    private val playbackManager: PlaybackManagerAndroidImpl
) {
    private var mediaSession: MediaSessionCompat =
        MediaSessionCompat(context, context.packageName + "soulSearchingMediaSession")

    private val standardNotificationBitmap: Bitmap = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(context.resources, R.drawable.notification_default),
        AndroidUtils.BITMAP_SIZE,
        AndroidUtils.BITMAP_SIZE,
        false
    )

    /**
     * The token of the media session.
     */
    val token: MediaSessionCompat.Token
        get() = mediaSession.sessionToken

    /**
     * Initialize the media session used by the player.
     */
    @Suppress("DEPRECATION")
    fun init() {
        mediaSession =
            MediaSessionCompat(context, context.packageName + "soulSearchingMediaSession")

        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onSeekTo(pos: Long) {
                playbackManager.seekToPosition(pos.toInt())
            }

            override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                val keyEvent = mediaButtonIntent.extras?.get(Intent.EXTRA_KEY_EVENT) as KeyEvent
                if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                    when (keyEvent.keyCode) {
                        KeyEvent.KEYCODE_MEDIA_PAUSE, KeyEvent.KEYCODE_MEDIA_PLAY -> playbackManager.togglePlayPause()
                    }
                }
                return super.onMediaButtonEvent(mediaButtonIntent)
            }

            override fun onPlay() {
                super.onPlay()
                playbackManager.play()
            }

            override fun onPause() {
                super.onPause()
                playbackManager.pause()
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                playbackManager.next()
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                playbackManager.previous()
            }
        })
        updateState()

        mediaSession.isActive = true
    }

    /**
     * Release all elements related to the media session.
     */
    fun release() = mediaSession.release()

    /**
     * Update media session data with information the current played song in the player view model.
     */
    fun updateMetadata() {
        val bitmap = playbackManager.currentMusicCover?.asAndroidBitmap() ?: standardNotificationBitmap

        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putBitmap(
                    MediaMetadata.METADATA_KEY_ALBUM_ART,
                    bitmap
                )
                .putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION,
                    playbackManager.currentMusicDuration.toLong()
                )
                .putString(
                    MediaMetadata.METADATA_KEY_DISPLAY_TITLE,
                    playbackManager.currentMusic?.name
                )
                .putLong(
                    MediaMetadata.METADATA_KEY_TRACK_NUMBER,
                    playbackManager.currentMusicPosition.toLong()
                )
                .putLong(
                    MediaMetadata.METADATA_KEY_NUM_TRACKS,
                    playbackManager.playedList.size.toLong()
                )
                // For old versions of Android
                .putString(
                    MediaMetadata.METADATA_KEY_TITLE,
                    playbackManager.currentMusic?.name
                )
                .putString(
                    MediaMetadata.METADATA_KEY_ARTIST,
                    playbackManager.currentMusic?.artist
                )
                // A small bitmap for the artwork is also recommended
                .putBitmap(
                    MediaMetadata.METADATA_KEY_ART,
                    bitmap
                )
                .build()
        )
    }

    /**
     * Update the state of the player's media session.
     */
    fun updateState() {
        val musicState = if (playbackManager.isPlaying) {
            PlaybackState.STATE_PLAYING
        } else {
            PlaybackState.STATE_PAUSED
        }

        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_SEEK_TO
                            or PlaybackStateCompat.ACTION_PAUSE
                            or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                            or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
                .setState(
                    musicState,
                    playbackManager.currentMusicPosition.toLong(),
                    1.0F
                )
                .build()
        )
    }
}