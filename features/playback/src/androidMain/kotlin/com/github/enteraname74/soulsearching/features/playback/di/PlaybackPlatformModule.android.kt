package com.github.enteraname74.soulsearching.features.playback.di

import com.github.enteraname74.soulsearching.features.playback.environment.AndroidPlaybackEnvironment
import com.github.enteraname74.soulsearching.features.playback.environment.MediaServiceConnector
import com.github.enteraname74.soulsearching.features.playback.environment.SoulSearchingPlaybackEnvironment
import com.github.enteraname74.soulsearching.features.playback.mediasession.MediaItemUtils
import com.github.enteraname74.soulsearching.features.playback.mediasession.MediaSessionManager
import com.github.enteraname74.soulsearching.features.playback.notification.SoulSearchingAndroidNotification
import com.github.enteraname74.soulsearching.features.playback.notification.SoulSearchingNotification
import com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingExoPlayerImpl
import com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingPlayer
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val playbackPlatformModule: Module = module {
    singleOf(::MediaServiceConnector)
    singleOf(::AndroidPlaybackEnvironment) bind SoulSearchingPlaybackEnvironment::class
    singleOf(::MediaSessionManager)
    singleOf(::SoulSearchingExoPlayerImpl) bind SoulSearchingPlayer::class
    singleOf(::SoulSearchingAndroidNotification) bind SoulSearchingNotification::class
    singleOf(::MediaItemUtils)
}
