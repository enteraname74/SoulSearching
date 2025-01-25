package com.github.enteraname74.soulsearching.repository.utils

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.CloudRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

object DeleteAllHelper: KoinComponent {

    private val cloudRepository by inject<CloudRepository>()

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
            val result = deleteAllRemote(partition.second.map(mapIds))
            cloudRepository.syncDataWithCloud()
            result
        } else {
            SoulResult.Success("")
        }
    }
}