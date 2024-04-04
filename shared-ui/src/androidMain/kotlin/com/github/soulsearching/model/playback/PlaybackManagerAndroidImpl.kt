package com.github.soulsearching.model.playback

import android.content.Context
import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.model.utils.AndroidUtils
import com.github.soulsearching.model.PlaybackManager
import com.github.soulsearching.utils.PlayerUtils

/**
 * Implementation of a MusicPlayerManager for Android.
 * It is primarily used in this Android implementation as a 
 * bridge for communicating with the PlayerService.
 */
class PlaybackManagerAndroidImpl(
    private val context: Context
): PlaybackManager {

    override fun initializePlayerFromSavedList(savedMusicList: ArrayList<Music>) {
        PlayerUtils.playerViewModel.handler.setPlayerInformationFromSavedList(
            savedMusicList
        )
        AndroidUtils.launchService(
            context = context,
            isFromSavedList = true
        )
        PlayerUtils.playerViewModel.handler.shouldServiceBeLaunched = true
    }

    override fun initializeMusicPlayerManager(isFromSavedList: Boolean) = AndroidUtils.launchService(
        context = context,
        isFromSavedList = isFromSavedList
    )

    override fun setAndPlayCurrentMusic() = PlayerService.setAndPlayCurrentMusic()

    override fun onlyLoadMusic() = PlayerService.onlyLoadMusic()

    override fun isPlayerPlaying(): Boolean = PlayerService.isPlayerPlaying()

    override fun togglePlayPause() = PlayerService.togglePlayPause()

    override fun playNext() = PlayerService.playNext()

    override fun playPrevious() = PlayerService.playPrevious()

    override fun seekToPosition(position: Int) = PlayerService.seekToPosition(position)

    override fun getMusicDuration(): Int = PlayerService.getMusicDuration()

    override fun getCurrentMusicPosition(): Int = PlayerService.getCurrentMusicPosition()

    override fun stopMusic() = PlayerService.stopMusic(context)

    /**
     * Force the update of the notification on Android devices.
     */
    override fun updateNotification() = PlayerService.updateNotification()
}