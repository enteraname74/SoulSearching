package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.soulsearching.shareddi.mainModule
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule: Module = module {
    includes(
        mainModule,
        platformModule,
        viewModelModule,
    )
}