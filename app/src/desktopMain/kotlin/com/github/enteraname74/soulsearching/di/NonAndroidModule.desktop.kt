package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.soulsearching.feature.musiclink.MusicLinkHandler
import com.github.enteraname74.soulsearching.util.FileOperation
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val nonAndroidModule: Module = module {
    singleOf(::MusicLinkHandler)
    factory {
        FileOperation()
    }
}
