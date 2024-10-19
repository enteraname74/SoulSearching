package com.github.enteraname74.soulsearching.features.playback.di

import com.github.enteraname74.soulsearching.features.playback.PlaybackManagerAndroidImpl
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.mediasession.MediaSessionManager
import com.github.enteraname74.soulsearching.features.playback.notification.SoulSearchingNotification
import com.github.enteraname74.soulsearching.features.playback.notification.impl.SoulSearchingAndroidNotification
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

internal actual val playbackPlatformModule: Module = module {
    singleOf(::MediaSessionManager)
    singleOf(::PlaybackManagerAndroidImpl) bind PlaybackManager::class
    single {
        SoulSearchingAndroidNotification.buildNotification(
            context = get(),
            playbackManager = get(),
        )
    }.binds(
        arrayOf(
            SoulSearchingNotification::class,
            SoulSearchingAndroidNotification::class,
        )
    )
}