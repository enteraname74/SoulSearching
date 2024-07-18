package com.github.enteraname74.soulsearching.di

import org.koin.dsl.module

import com.github.enteraname74.soulsearching.commondelegate.*
import org.koin.core.module.dsl.singleOf

internal val delegateModule = module {
    singleOf(::MusicBottomSheetDelegateImpl)
}