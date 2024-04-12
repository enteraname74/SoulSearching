package com.github.soulsearching.model.player

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.player.domain.model.SoulSearchingPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * Implementation of the SoulSearchingAndroidPlayer
 */
class SoulSearchingAndroidPlayerImpl(
    context: Context,
    private val playbackManager: PlaybackManager
) :
    SoulSearchingPlayer,
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener {

    private var player: MediaPlayer = MediaPlayer()
    private var isOnlyLoadingMusic: Boolean = false
    private var positionToReachWhenLoadingMusic: Int = 0
    private val audioManager: PlayerAudioManager = PlayerAudioManager(context, this)

    override fun init() {
        audioManager.init()
        player = MediaPlayer()
        player.apply {
            setAudioAttributes(audioManager.audioAttributes)
            setOnPreparedListener(this@SoulSearchingAndroidPlayerImpl)
            setOnCompletionListener(this@SoulSearchingAndroidPlayerImpl)
            setOnErrorListener(this@SoulSearchingAndroidPlayerImpl)
        }
    }

    override fun setMusic(music: Music) {
        player.stop()
        player.reset()
        if (File(music.path).exists()) {
            player.setDataSource(music.path)
        }
    }

    override fun onlyLoadMusic(seekTo: Int) {
        isOnlyLoadingMusic = true
        positionToReachWhenLoadingMusic = seekTo
        launchMusic()
    }

    override fun isPlaying(): Boolean {
        return try {
            player.isPlaying
        } catch (e: IllegalStateException) {
            false
        }
    }

    override fun launchMusic() {
        try {
            player.prepareAsync()
        } catch (_: IllegalStateException) {

        }
    }

    /**
     * Tries to retrieve the audio focus and play the current music.
     */
    override fun play() {
        when (audioManager.requestAudioFocus()) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                try {
                    player.start()
                    playbackManager.update()
                } catch (_: IllegalStateException) {
                }
            }

            else -> {
                Log.d("PLAYER", "MISSING AUDIO MANAGER")
            }
        }
    }

    /**
     * Release the audio focus and pause the current music.
     */
    override fun pause() {
        try {
            audioManager.abandonAudioFocusRequest()
            player.pause()
            playbackManager.update()
        } catch (_: IllegalStateException) {
        }
    }

    override fun togglePlayPause() {
        try {
            if (player.isPlaying) pause()
            else play()
        } catch (_: Exception) {}
    }

    override fun seekToPosition(position: Int) {
        try {
            player.seekTo(position)
            playbackManager.update()
        } catch (_: IllegalStateException) {

        }
    }

    override fun dismiss() {
        player.release()
        audioManager.release()
    }

    override fun getMusicPosition(): Int {
        return try {
            player.currentPosition
        } catch (e: IllegalStateException) {
            0
        }
    }

    override fun getMusicDuration(): Int {
        return try {
            player.duration
        } catch (_: Exception) {
            0
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        CoroutineScope(Dispatchers.IO).launch {
//            next()
            playbackManager.next()
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d("MEDIA PLAYER", "ERROR CODE : $what, $extra")
        when (what) {
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
//                PlayerUtils.playerViewModel.handler.removeMusicFromCurrentPlaylist(
//                    musicId = PlayerUtils.playerViewModel.handler.currentMusic!!.musicId
//                )
                playbackManager.skipAndRemoveCurrentSong()
            }
        }
        return true
    }

    override fun onPrepared(mp: MediaPlayer?) {
        if (isOnlyLoadingMusic) {
            /*
             * When only loading the music, we try to seek to the last music position
             * (when loading a previous song which was at a given position)
             */
            seekToPosition(positionToReachWhenLoadingMusic)
            isOnlyLoadingMusic = false
            positionToReachWhenLoadingMusic = 0
        } else {
            when (audioManager.requestAudioFocus()) {
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                    player.start()
                    playbackManager.update()
//                    PlayerUtils.playerViewModel.handler.currentMusic?.let {
//                        PlayerUtils.playerViewModel.handler.updateNbPlayed(it.musicId)
//                    }
//                    PlayerUtils.playerViewModel.handler.isPlaying = true
//
//                    releaseDurationJob()
//                    launchDurationJob()
//
//                    updateNotification()
//
//                    settings.saveCurrentMusicInformation(
//                        PlayerUtils.playerViewModel.handler.getIndexOfCurrentMusic(),
//                        PlayerUtils.playerViewModel.handler.currentMusicPosition
//                    )
                }

                else -> {}
            }
        }
    }

//    /**
//     * Launch a job that save the current music position to the shared preferences.
//     * It is used to save the current music position between app sessions.
//     */
//    private fun launchDurationJob() {
//        currentDurationJob = CoroutineScope(Dispatchers.IO).launch {
//            while (true) {
//                withContext(Dispatchers.IO) {
//                    Thread.sleep(1000)
//                }
//                PlayerUtils.playerViewModel.handler.currentMusicPosition =
//                    PlayerService.getCurrentMusicPosition()
//                if (player.isPlaying) {
//                    settings.setInt(
//                        SoulSearchingSettings.PLAYER_MUSIC_POSITION_KEY,
//                        PlayerUtils.playerViewModel.handler.currentMusicPosition
//                    )
//                }
//            }
//        }
//    }

//    /**
//     * Release the duration job.
//     */
//    private fun releaseDurationJob() {
//        if (currentDurationJob != null) {
//            currentDurationJob!!.cancel()
//            currentDurationJob = null
//        }
//    }
}