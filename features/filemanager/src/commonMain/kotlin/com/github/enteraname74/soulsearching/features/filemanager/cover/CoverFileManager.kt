package com.github.enteraname74.soulsearching.features.filemanager.cover

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.features.filemanager.cache.CacheManager
import java.io.File
import java.util.UUID

interface CoverFileManager: CacheManager {
    fun getCleanFileCoverForMusic(music: Music): Cover.CoverFile =
        Cover.CoverFile(
            initialCoverPath = music.path,
        )

    suspend fun getAllCoverIds(): List<UUID> =
        getAllSavedFiles().mapNotNull { it.coverId() }

    private fun File.coverId(): UUID? =
        this.name.split(".").firstOrNull()?.let { cover ->
            try {
                UUID.fromString(cover)
            } catch (_: Exception) {
                null
            }
        }

    override fun buildFileName(id: UUID): String =
        "$id.jpg"
}