package com.github.enteraname74.soulsearching.features.playback.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.github.enteraname74.soulsearching.features.playback.notification.SoulSearchingDesktopNotification
import com.github.enteraname74.soulsearching.features.playback.notification.SoulSearchingNotification
import com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingDesktopPlayerImpl
import com.github.enteraname74.soulsearching.features.playback.SoulSearchingPlayer
import com.github.enteraname74.soulsearching.features.playback.PlaybackManagerDesktopImpl
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import org.koin.dsl.bind

internal actual val playbackPlatformModule: Module = module {
    singleOf(::SoulSearchingDesktopNotification) bind SoulSearchingNotification::class
    singleOf(::SoulSearchingDesktopPlayerImpl) bind SoulSearchingPlayer::class
    singleOf(::PlaybackManagerDesktopImpl) bind PlaybackManager::class
}