package com.github.enteraname74.domain.usecase.datamode

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.repository.DataModeRepository

class SetCurrentDataModeUseCase(
    private val dataModeRepository: DataModeRepository
) {
    suspend operator fun invoke(dataMode: DataMode) {
        dataModeRepository.switchDataMode(dataMode)
    }
}