package com.github.enteraname74.soulsearching.repository.utils

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.SoulResult
import java.util.*

object DeleteAllHelper {
    suspend fun <T>deleteAll(
        ids: List<UUID>,
        getAll: suspend (ids: List<UUID>) -> List<T>,
        deleteAllLocal: suspend (ids: List<UUID>) -> Unit,
        deleteAllRemote: suspend (ids: List<UUID>) -> SoulResult<String>,
        mapIds: (T) -> UUID,
        getDataMode: (T) -> DataMode,
    ): SoulResult<String> {
        val partition = getAll(ids).partition { getDataMode(it) == DataMode.Local }

        if (partition.first.isNotEmpty()) {
            deleteAllLocal(partition.first.map(mapIds))
        }

        return if (partition.second.isNotEmpty()) {
            deleteAllRemote(partition.second.map(mapIds))
        } else {
            SoulResult.Success("")
        }
    }
}