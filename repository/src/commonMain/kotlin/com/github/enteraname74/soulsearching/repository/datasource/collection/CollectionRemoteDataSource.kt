package com.github.enteraname74.soulsearching.repository.datasource.collection

import com.github.enteraname74.soulsearching.domain.model.CloudCollection
import com.github.enteraname74.soulsearching.domain.model.CollectionSyncData
import com.github.enteraname74.soulsearching.domain.model.SoulResult
import kotlin.uuid.Uuid

interface CollectionRemoteDataSource {
    suspend fun getDeletedRemoteCollectionIds(idsToCheck: List<Uuid>): List<Uuid>
    suspend fun getOfUser(lastUpdateAt: Long?, maxPerPage: Int?, page: Int?): List<CloudCollection>
    suspend fun deleteAll(ids: List<Uuid>): SoulResult<Unit>
    suspend fun upload(collection: CollectionSyncData, coverPath: String?): CloudCollection
}
