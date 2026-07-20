package com.github.enteraname74.soulsearching.features.playback.di

import com.github.enteraname74.soulsearching.features.playback.notification.SoulSearchingNotification
import com.github.enteraname74.soulsearching.features.playback.notification.SoulSearchingWebNotification
import com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingPlayer
import com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingWebPlayerImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val nonAndroidPlatformModule: Module = module {
    singleOf(::SoulSearchingWebNotification) bind SoulSearchingNotification::class
    singleOf(::SoulSearchingWebPlayerImpl) bind SoulSearchingPlayer::class
}