package com.github.enteraname74.soulsearching.features.filemanager.cover

import kotlin.uuid.Uuid

interface CoverFileManager {
    suspend fun saveCover(id: Uuid, data: ByteArray)
    suspend fun getCoverPath(id: Uuid): String?
    suspend fun getAllCoverIds(): List<Uuid>
    suspend fun getCoverData(coverId: Uuid): ByteArray?
    suspend fun deleteFromId(id: Uuid)
}
