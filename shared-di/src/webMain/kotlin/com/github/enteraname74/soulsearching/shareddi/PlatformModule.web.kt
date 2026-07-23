package com.github.enteraname74.soulsearching.shareddi

import com.github.enteraname74.domain.util.WorkDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    factory { WorkDispatcher(dispatcher = Dispatchers.Default) }
}