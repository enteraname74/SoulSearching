package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.CloudRepository
import com.github.enteraname74.domain.repository.DataModeRepository
import com.github.enteraname74.domain.repository.MusicRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

class DeleteAllMusicsUseCase(
    private val musicRepository: MusicRepository,
    private val dataModeRepository: DataModeRepository,
    private val cloudRepository: CloudRepository,
) {
    suspend operator fun invoke(ids: List<UUID>): SoulResult<String> {
        val result = musicRepository.deleteAll(
            ids = ids,
        )
        if (result.isError()) return result

        if (dataModeRepository.getCurrentDataModeWithUserCheck().first() == DataMode.Cloud) {
            cloudRepository.syncDataWithCloud()
        }

        return result
    }

}