package com.github.enteraname74.soulsearching.features.filemanager.cache

import java.io.File
import java.util.*

interface CacheManager {
    val folder: File

    fun buildFileName(id: UUID): String

    suspend fun save(id: UUID, data: ByteArray) {
        if (getPath(id = id) == null) {
            val coverFile = File(folder, buildFileName(id = id))
            coverFile.writeBytes(data)
        }
    }

    suspend fun getPath(id: UUID): String? {
        val coverFile = File(folder, buildFileName(id = id))

        return if (coverFile.exists()) {
            coverFile.absolutePath
        } else {
            null
        }
    }

    suspend fun getAllSavedFiles(): List<File> =
        folder.listFiles()?.toList() ?: emptyList()

    suspend fun getFileData(coverId: UUID): ByteArray? {
        val coverFile = File(folder, buildFileName(id = coverId))

        return if (coverFile.exists()) {
            return coverFile.readBytes()
        } else {
            null
        }
    }

    suspend fun deleteAll() {
        folder.deleteRecursively()
    }

    suspend fun deleteFromId(id: UUID) {
        val coverToDelete = File(folder, buildFileName(id = id))
        coverToDelete.delete()
    }
}