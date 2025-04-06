package com.github.enteraname74.soulsearching.features.playback.di

import com.github.enteraname74.soulsearching.features.httpclient.HttpClientNames
import com.github.enteraname74.soulsearching.features.playback.notification.SoulSearchingDesktopNotification
import com.github.enteraname74.soulsearching.features.playback.notification.SoulSearchingNotification
import com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingDesktopPlayerImpl
import com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingPlayer
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val playbackPlatformModule: Module = module {
    singleOf(::SoulSearchingDesktopNotification) bind SoulSearchingNotification::class
    single<SoulSearchingPlayer> {
        SoulSearchingDesktopPlayerImpl(
            client = get(named(HttpClientNames.CLOUD_AUTH)),
            cloudCacheManager = get(),
        )
    }
}