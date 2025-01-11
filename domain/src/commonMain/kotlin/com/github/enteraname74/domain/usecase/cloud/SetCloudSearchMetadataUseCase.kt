package com.github.enteraname74.domain.usecase.cloud

import com.github.enteraname74.domain.repository.CloudRepository

class SetCloudSearchMetadataUseCase(
    private val cloudRepository: CloudRepository,
) {
    suspend operator fun invoke(searchMetadata: Boolean) {
        cloudRepository.setSearchMetadata(searchMetadata)
    }
}