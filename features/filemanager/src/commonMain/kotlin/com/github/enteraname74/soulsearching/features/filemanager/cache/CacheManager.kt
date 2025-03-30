package com.github.enteraname74.soulsearching.features.filemanager.cache

import java.io.File
import java.util.*

interface CacheManager {
    val folder: File

    fun buildFileName(id: UUID): String

    suspend fun save(id: UUID, data: ByteArray) {
        if (getPath(id = id) == null) {
            val file = File(folder, buildFileName(id = id))
            file.writeBytes(data)
        }
    }

    suspend fun getPath(id: UUID): String? {
        val file = File(folder, buildFileName(id = id))

        return if (file.exists()) {
            file.absolutePath
        } else {
            null
        }
    }

    suspend fun getAllSavedFiles(): List<File> =
        folder.listFiles()?.toList() ?: emptyList()

    suspend fun getFileData(fileId: UUID): ByteArray? {
        val file = File(folder, buildFileName(id = fileId))

        return if (file.exists()) {
            return file.readBytes()
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