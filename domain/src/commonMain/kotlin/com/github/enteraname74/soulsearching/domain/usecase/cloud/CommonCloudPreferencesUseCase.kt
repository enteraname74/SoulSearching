package com.github.enteraname74.soulsearching.domain.usecase.cloud

import com.github.enteraname74.soulsearching.domain.model.CloudPreferences
import com.github.enteraname74.soulsearching.domain.repository.CloudPreferencesRepository
import kotlinx.coroutines.flow.Flow

class CommonCloudPreferencesUseCase(
    private val cloudPreferencesRepository: CloudPreferencesRepository,
) {
    fun observeUrl(): Flow<String?> =
        cloudPreferencesRepository.observeUrl()

    fun observe(): Flow<CloudPreferences> =
        cloudPreferencesRepository.observePreferences()

    suspend fun setUrl(url: String) {
        cloudPreferencesRepository.setUrl(url)
    }
}
