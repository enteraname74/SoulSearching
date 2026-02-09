package com.github.enteraname74.soulsearching.features.playback.mediasession

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.session.PlaybackState
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.graphics.scale
import com.github.enteraname74.domain.usecase.music.ToggleMusicFavoriteStatusUseCase
import com.github.enteraname74.soulsearching.features.playback.R
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Manage media session related things.
 */
class MediaSessionManager(
    private val context: Context,
    private val playbackManager: PlaybackManager,
    private val toggleMusicFavoriteStatusUseCase: ToggleMusicFavoriteStatusUseCase,
) {
    private var mediaSession: MediaSessionCompat? = null
    private var seekToJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val standardNotificationBitmap: Bitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.new_notification_default)
            .scale(DEFAULT_NOTIFICATION_SIZE, DEFAULT_NOTIFICATION_SIZE, false)

    suspend fun getUpdatedMediaSessionToken(
        playbackState: PlaybackManagerState.Data,
    ): MediaSessionCompat.Token {
        if (mediaSession == null) {
            init(
                isPlaying = playbackState.isPlaying,
                isFavorite = playbackState.isCurrentMusicInFavorite,
            )
        } else {
            updateMetadata(playbackState)
            updateState(
                isPlaying = playbackState.isPlaying,
                isFavorite = playbackState.isCurrentMusicInFavorite,
            )
        }
        return mediaSession!!.sessionToken
    }

    /**
     * Initialize the media session used by the player.
     */
    @Suppress("DEPRECATION")
    private fun init(
        isPlaying: Boolean,
        isFavorite: Boolean,
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

            override fun onCustomAction(action: String?, extras: Bundle?) {
                super.onCustomAction(action, extras)
                when (action) {
                    FAVORITE_ACTION -> {
                        playbackManager.currentSong.value?.musicId?.let {
                            coroutineScope.launch {
                                toggleMusicFavoriteStatusUseCase(musicId = it)
                            }
                        }
                    }
                }
            }
        })
        updateState(
            isPlaying = isPlaying,
            isFavorite = isFavorite,
        )
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
        val bitmap = playbackManager.currentCoverState.first()?.asAndroidBitmap() ?: standardNotificationBitmap

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
                    playbackState.currentMusic.artistsNames
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
        isPlaying: Boolean,
        isFavorite: Boolean,
    ) {
        val musicState = if (isPlaying) {
            PlaybackState.STATE_PLAYING
        } else {
            PlaybackState.STATE_PAUSED
        }

        val favoriteCustomAction = PlaybackStateCompat.CustomAction.Builder(
            FAVORITE_ACTION,
            FAVORITE_ACTION,
            if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite
        ).build()

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
                .addCustomAction(favoriteCustomAction)
                .setState(
                    musicState,
                    playbackManager.getMusicPosition().toLong(),
                    1.0F
                )
                .build()
        )
    }

    companion object {
        private const val DEFAULT_NOTIFICATION_SIZE: Int = 300
        private const val FAVORITE_ACTION: String = "FAVORITE_ACTION"
    }
}