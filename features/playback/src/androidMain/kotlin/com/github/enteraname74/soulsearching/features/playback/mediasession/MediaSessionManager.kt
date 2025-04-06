package com.github.enteraname74.soulsearching.features.playback.mediasession

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
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverRetriever
import com.github.enteraname74.soulsearching.features.playback.R
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

/**
 * Manage media session related things.
 */
class MediaSessionManager(
    private val context: Context,
    private val playbackManager: PlaybackManager,
    private val coverRetriever: CoverRetriever,
) {
    private var mediaSession: MediaSessionCompat? = null
    private var seekToJob: Job? = null

    private val standardNotificationBitmap: Bitmap = Bitmap.createScaledBitmap(
        BitmapFactory.decodeResource(context.resources, R.drawable.new_notification_default),
        DEFAULT_NOTIFICATION_SIZE,
        DEFAULT_NOTIFICATION_SIZE,
        false
    )

    suspend fun getUpdatedMediaSessionToken(
        playbackState: PlaybackManagerState.Data,
    ): MediaSessionCompat.Token {
        if (mediaSession == null) {
            init(playbackState.isPlaying)
        } else {
            updateMetadata(playbackState)
            updateState(playbackState.isPlaying)
        }
        return mediaSession!!.sessionToken
    }

    /**
     * Initialize the media session used by the player.
     */
    @Suppress("DEPRECATION")
    private fun init(
        isPlaying: Boolean,
    ) {
        mediaSession =
            MediaSessionCompat(context, context.packageName + "soulSearchingMediaSession")

        mediaSession?.setCallback(object : MediaSessionCompat.Callback() {
            override fun onSeekTo(pos: Long) {
                seekToJob?.cancel()
                seekToJob = CoroutineScope(Dispatchers.IO).launch {
                    playbackManager.seekToPosition(pos.toInt())
                }
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
                CoroutineScope(Dispatchers.IO).launch {
                    playbackManager.next()
                }
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                CoroutineScope(Dispatchers.IO).launch {
                    playbackManager.previous()
                }
            }
        })
        updateState(isPlaying = isPlaying)
        mediaSession?.isActive = true
    }

    /**
     * Release all elements related to the media session.
     */
    fun release() {
        mediaSession?.release()
        mediaSession = null
    }

    /**
     * Update media session data with information the current played song in the player view model.
     */
    private suspend fun updateMetadata(playbackState: PlaybackManagerState.Data) {
        // TODO: Test this new fetch method of cover on Android.
        val bitmap = coverRetriever.getImageBitmap(cover = playbackState.currentMusic.cover)?.asAndroidBitmap() ?: standardNotificationBitmap

        mediaSession?.setMetadata(
            MediaMetadataCompat.Builder()
                .putBitmap(
                    MediaMetadata.METADATA_KEY_ALBUM_ART,
                    bitmap
                )
                .putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION,
                    playbackState.currentMusic.duration
                )
                .putString(
                    MediaMetadata.METADATA_KEY_DISPLAY_TITLE,
                    playbackState.currentMusic.name
                )
                .putLong(
                    MediaMetadata.METADATA_KEY_TRACK_NUMBER,
                    playbackState.currentMusicIndex.toLong()
                )
                .putLong(
                    MediaMetadata.METADATA_KEY_NUM_TRACKS,
                    playbackState.playedList.size.toLong()
                )
                // For old versions of Android
                .putString(
                    MediaMetadata.METADATA_KEY_TITLE,
                    playbackState.currentMusic.name
                )
                .putString(
                    MediaMetadata.METADATA_KEY_ARTIST,
                    playbackState.currentMusic.artist
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
    private fun updateState(
        isPlaying: Boolean
    ) {
        val musicState = if (isPlaying) {
            PlaybackState.STATE_PLAYING
        } else {
            PlaybackState.STATE_PAUSED
        }

        mediaSession?.setPlaybackState(
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
                    playbackManager.getMusicPosition().toLong(),
                    1.0F
                )
                .build()
        )
    }

    companion object {
        private const val DEFAULT_NOTIFICATION_SIZE = 300
    }
}