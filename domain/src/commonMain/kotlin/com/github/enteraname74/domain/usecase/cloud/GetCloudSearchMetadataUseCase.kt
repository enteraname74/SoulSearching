package com.github.enteraname74.domain.usecase.cloud

import com.github.enteraname74.domain.repository.CloudRepository
import kotlinx.coroutines.flow.Flow

class GetCloudSearchMetadataUseCase(
    private val cloudRepository: CloudRepository,
) {
    operator fun invoke(): Flow<Boolean> =
        cloudRepository.getSearchMetadata()
}