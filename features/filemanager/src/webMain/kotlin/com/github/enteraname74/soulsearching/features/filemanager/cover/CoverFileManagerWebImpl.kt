package com.github.enteraname74.soulsearching.features.filemanager.cover

import kotlin.uuid.Uuid

internal class CoverFileManagerWebImpl : CoverFileManager {
    override suspend fun saveCover(id: Uuid, data: ByteArray) = Unit

    override suspend fun getCoverPath(id: Uuid): String? = null

    override suspend fun getAllCoverIds(): List<Uuid> = emptyList()

    override suspend fun getCoverData(coverId: Uuid): ByteArray? = null

    override suspend fun deleteFromId(id: Uuid) = Unit
}
