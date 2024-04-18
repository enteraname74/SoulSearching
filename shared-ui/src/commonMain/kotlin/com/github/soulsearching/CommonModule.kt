package com.github.soulsearching

import com.github.soulsearching.player.domain.PlayerDraggableState
import org.koin.core.module.Module
import org.koin.dsl.module

val commonModule: Module = module {
    single {
        PlayerDraggableState()
    }
}