package com.github.enteraname74.soulsearching.features.filemanager.cover

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import java.io.File
import java.util.UUID

interface CoverFileManager {
    fun getCoverFolder(): File

    fun getCleanFileCoverForMusic(music: Music): Cover.CoverFile =
        Cover.CoverFile(
            initialCoverPath = music.path,
        )

    suspend fun saveCover(id: UUID, data: ByteArray) {
        val coverFolder = getCoverFolder()

        if (getCoverPath(id = id) == null) {
            val coverFile = File(coverFolder, buildFileName(id = id))
            coverFile.writeBytes(data)
        }
    }

    suspend fun getCoverPath(id: UUID): String? {
        val coverFolder = getCoverFolder()
        val coverFile = File(coverFolder, buildFileName(id = id))

        return if (coverFile.exists()) {
            coverFile.absolutePath
        } else {
            null
        }
    }

    suspend fun getAllCoverIds(): List<UUID> {
        val coverFolder = getCoverFolder()
        val allCoverFiles = coverFolder.listFiles() ?: return emptyList()

        return buildList {
            allCoverFiles.forEach { cover ->
                cover.coverId()?.let {
                    add(it)
                }
            }
        }
    }

    suspend fun getCoverData(coverId: UUID): ByteArray? {
        val coverFolder = getCoverFolder()
        val coverFile = File(coverFolder, buildFileName(id = coverId))

        return if (coverFile.exists()) {
            return coverFile.readBytes()
        } else {
            null
        }
    }

    suspend fun deleteFromId(id: UUID) {
        val coverFolder = getCoverFolder()
        val coverToDelete = File(coverFolder, buildFileName(id = id))
        coverToDelete.delete()
    }

    private fun File.coverId(): UUID? =
        this.name.split(".").firstOrNull()?.let { cover ->
            try {
                UUID.fromString(cover)
            } catch (_: Exception) {
                null
            }
        }

    fun buildFileName(id: UUID): String =
        "$id.jpg"
}