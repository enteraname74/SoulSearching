package com.github.enteraname74.soulsearching.shareddi

import com.github.enteraname74.domain.di.domainModule
import com.github.enteraname74.localdb.localModule
import com.github.enteraname74.soulsearching.features.filemanager.di.fileManagerModule
import com.github.enteraname74.soulsearching.features.playback.di.playbackModule
import com.github.enteraname74.soulsearching.repository.di.repositoryModule
import com.github.enteraname74.soulsearching.features.musicmanager.di.musicManagerModule
import com.github.enteraname74.soulsearching.remote.di.remoteModule
import org.koin.dsl.module

val mainModule = module {
    includes(
        localModule,
        remoteModule,
        repositoryModule,
        domainModule,
        playbackModule,
        fileManagerModule,
        musicManagerModule,
    )
}