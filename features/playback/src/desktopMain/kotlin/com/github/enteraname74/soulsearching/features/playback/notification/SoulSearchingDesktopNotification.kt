package com.github.enteraname74.soulsearching.features.playback.notification

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
//import es.blackleg.jlibnotify.JLibnotify
//import es.blackleg.jlibnotify.JLibnotifyNotification
//import es.blackleg.jlibnotify.core.DefaultJLibnotifyLoader

class SoulSearchingDesktopNotification : SoulSearchingNotification {
//    private var libNotify: JLibnotify? = null
//    private var notification: JLibnotifyNotification? = null

//    init {
//        libNotify = DefaultJLibnotifyLoader().load()
//        libNotify?.init("Soul Searching")
//    }

    override suspend fun updateNotification(playbackManagerState: PlaybackManagerState.Data, cover: ImageBitmap?) {
//        if (libNotify?.isInitted == false) {
//            libNotify?.init("Soul Searching")
//        }
//
//        if (notification == null) {
//            notification = libNotify
//                ?.createNotification(
//                    playbackManagerState.currentMusic.name,
//                    playbackManagerState.currentMusic.artist,
//                    ""
//                )
//            notification?.show()
//        } else {
//            notification?.update(
//                playbackManagerState.currentMusic.name,
//                playbackManagerState.currentMusic.artist,
//                ""
//            )
//            notification?.show()
//        }

    }

    override fun dismissNotification() {
//        libNotify?.unInit()
//        notification?.close()
    }
}