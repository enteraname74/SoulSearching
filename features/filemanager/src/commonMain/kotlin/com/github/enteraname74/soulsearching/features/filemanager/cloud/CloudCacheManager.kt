package com.github.enteraname74.soulsearching.features.filemanager.cloud

import com.github.enteraname74.soulsearching.features.filemanager.cache.CacheManager
import java.util.*

interface CloudCacheManager: CacheManager {
    override fun buildFileName(id: UUID): String =
        "$id.m4a"
}