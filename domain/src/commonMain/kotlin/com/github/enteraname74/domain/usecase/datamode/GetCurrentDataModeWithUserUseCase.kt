package com.github.enteraname74.domain.usecase.datamode

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.repository.DataModeRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentDataModeWithUserUseCase(
    private val dataModeRepository: DataModeRepository,
) {
    operator fun invoke(): Flow<DataMode> = dataModeRepository
        .getCurrentDataModeWithUserCheck()
}