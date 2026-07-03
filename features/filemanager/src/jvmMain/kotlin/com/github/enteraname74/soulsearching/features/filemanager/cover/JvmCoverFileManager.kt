package com.github.enteraname74.soulsearching.features.filemanager.cover

import java.io.File
import kotlin.uuid.Uuid

internal abstract class JvmCoverFileManager : CoverFileManager {
    protected abstract fun getCoverFolder(): File

    override suspend fun saveCover(id: Uuid, data: ByteArray) {
        val coverFolder = getCoverFolder()

        if (getCoverPath(id = id) == null) {
            val coverFile = File(coverFolder, buildFileName(id = id))
            coverFile.writeBytes(data)
        }
    }

    override suspend fun getCoverPath(id: Uuid): String? {
        val coverFolder = getCoverFolder()
        val coverFile = File(coverFolder, buildFileName(id = id))

        return if (coverFile.exists()) {
            coverFile.absolutePath
        } else {
            null
        }
    }

    override suspend fun getAllCoverIds(): List<Uuid> {
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

    override suspend fun getCoverData(coverId: Uuid): ByteArray? {
        val coverFolder = getCoverFolder()
        val coverFile = File(coverFolder, buildFileName(id = coverId))

        return if (coverFile.exists()) {
            coverFile.readBytes()
        } else {
            null
        }
    }

    override suspend fun deleteFromId(id: Uuid) {
        val coverFolder = getCoverFolder()
        val coverToDelete = File(coverFolder, buildFileName(id = id))
        coverToDelete.delete()
    }

    private fun File.coverId(): Uuid? =
        this.name.split(".").firstOrNull()?.let { cover ->
            try {
                Uuid.parse(cover)
            } catch (_: Exception) {
                null
            }
        }

    private fun buildFileName(id: Uuid): String =
        "$id.jpg"
}
