package com.github.enteraname74.soulsearching.shareddi

import com.github.enteraname74.domain.di.domainModule
import com.github.enteraname74.soulsearching.features.playback.di.playbackModule
import com.github.enteraname74.soulsearching.repository.di.repositoryModule
import org.koin.dsl.module

val mainModule = module {
    includes(
        localModule,
        repositoryModule,
        domainModule,
        playbackModule,
    )
}