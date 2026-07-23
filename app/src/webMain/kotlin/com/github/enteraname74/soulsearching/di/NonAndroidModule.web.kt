package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettingsImpl
import com.github.enteraname74.soulsearching.feature.musiclink.MusicLinkHandler
import com.github.enteraname74.soulsearching.util.FileOperation
import com.russhwolf.settings.StorageSettings
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val nonAndroidModule: Module = module {
    single<SoulSearchingSettings> {
        SoulSearchingSettingsImpl(settings = StorageSettings())
    }
    singleOf(::MusicLinkHandler)
    factory { FileOperation() }
}