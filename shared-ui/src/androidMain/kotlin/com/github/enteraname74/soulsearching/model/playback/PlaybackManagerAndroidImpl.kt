//package com.github.enteraname74.soulsearching.model.playback
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.os.Build
//import android.util.Log
//import com.github.enteraname74.domain.model.Music
//import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
//import com.github.enteraname74.soulsearching.features.playback.player.MediaSessionManager
//import com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingAndroidPlayerImpl
//
///**
// * Implementation of a MusicPlayerManager for Android.
// * It manages the player, foreground service, media sessions and notification.
// */
//class PlaybackManagerAndroidImpl(
//    private val context: Context,
//): PlaybackManager() {
//    private var shouldLaunchService: Boolean = true
//
//    private val mediaSessionManager =
//        com.github.enteraname74.soulsearching.features.playback.player.MediaSessionManager(
//            context = context,
//            playbackManager = this
//        )
//
//    override val player: com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingAndroidPlayerImpl =
//        com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingAndroidPlayerImpl(
//            context = context,
//            playbackManager = this
//        )
//
//    /**
//     * Initialize the playback manager.
//     */
//    @SuppressLint("UnspecifiedRegisterReceiverFlag")
//    override fun init() {
//        super.init()
//        mediaSessionManager.init()
//        shouldLaunchService = true
//    }
//
//
//    override suspend fun initializePlayerFromSavedList(savedList: List<Music>) {
//        super.initializePlayerFromSavedList(savedList)
////        defineCoverAndPaletteFromCoverId(cover = currentMusic?.cover)
//        launchService()
//    }
//
//    override fun setAndPlayMusic(music: Music) {
//        super.setAndPlayMusic(music)
//
//        if (shouldLaunchService) launchService()
//    }
//
//    override fun stopPlayback(resetPlayedList: Boolean) {
//        if (shouldInit) return
//
//        try {
//            context.unregisterReceiver(broadcastReceiver)
//        } catch (e: Exception) {
//            Log.e("PLAYBACK MANAGER", "EXCEPTION WHILE ON STOP PLAYBACK: $e")
//        }
//        player.dismiss()
//        mediaSessionManager.release()
//
//        if (!shouldLaunchService) stopService()
//
//        shouldInit = true
//        super.stopPlayback(resetPlayedList)
//    }
//
//    override fun updateNotification() {
//        mediaSessionManager.updateMetadata()
//        mediaSessionManager.updateState()
//
//        val intentForUpdatingNotification = Intent(com.github.enteraname74.soulsearching.features.playback.PlayerService.SERVICE_BROADCAST)
//        intentForUpdatingNotification.putExtra(com.github.enteraname74.soulsearching.features.playback.PlayerService.UPDATE_WITH_PLAYING_STATE, isPlaying)
//        intentForUpdatingNotification.setPackage(context.packageName)
//        context.sendBroadcast(intentForUpdatingNotification)
//    }
//}