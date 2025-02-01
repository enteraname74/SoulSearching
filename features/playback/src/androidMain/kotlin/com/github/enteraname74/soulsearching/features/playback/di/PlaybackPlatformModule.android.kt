package com.github.enteraname74.soulsearching.features.playback.di

import com.github.enteraname74.soulsearching.features.playback.mediasession.MediaSessionManager
import com.github.enteraname74.soulsearching.features.playback.notification.SoulSearchingNotification
import com.github.enteraname74.soulsearching.features.playback.notification.impl.SoulSearchingAndroidNotification
import com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingAndroidPlayerImpl
import com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingPlayer
import com.github.enteraname74.soulsearching.remote.model.HttpClientNames
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.binds
import org.koin.dsl.module

internal actual val playbackPlatformModule: Module = module {
    singleOf(::MediaSessionManager)
    single<SoulSearchingPlayer> {
        SoulSearchingAndroidPlayerImpl(
            context = androidContext(),
            client = get(named(HttpClientNames.CLOUD_AUTH)),
            cloudCacheManager = get(),
        )
    }
//    single {
//        ExoPlayer.Builder(androidContext()).build().apply {
//            setAudioAttributes(
//                AudioAttributes.Builder()
//                    .setUsage(C.USAGE_MEDIA)
//                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
//                    .build(),
//                true
//            )
//        }
//    }
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