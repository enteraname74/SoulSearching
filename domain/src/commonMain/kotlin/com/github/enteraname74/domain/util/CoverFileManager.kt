package com.github.enteraname74.domain.util

import java.io.File
import java.util.UUID

interface CoverFileManager {
    fun getCoverFolder(): File

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

    fun buildFileName(id: UUID): String =
        "$id.jpg"
}