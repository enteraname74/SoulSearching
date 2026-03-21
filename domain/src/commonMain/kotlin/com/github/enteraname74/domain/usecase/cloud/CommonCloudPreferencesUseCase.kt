package com.github.enteraname74.domain.usecase.cloud

import com.github.enteraname74.domain.repository.CloudPreferencesRepository
import kotlinx.coroutines.flow.Flow

class CommonCloudPreferencesUseCase(
    private val cloudPreferencesRepository: CloudPreferencesRepository,
) {
    fun observeUrl(): Flow<String?> =
        cloudPreferencesRepository.observeUrl()

    suspend fun setUrl(url: String) {
        cloudPreferencesRepository.setUrl(url)
    }
}