package com.github.enteraname74.soulsearching.features.filemanager.util

import com.github.enteraname74.domain.model.Music

actual class MusicFileUpdater actual constructor() {
    actual suspend fun updateMusic(music: Music): Unit = Unit
}
