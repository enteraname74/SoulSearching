package com.github.enteraname74.domain.usecase.cloud

import com.github.enteraname74.domain.repository.CloudPreferencesRepository
import com.github.enteraname74.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

// TODO SHARED LIST: include network state
class HasValidCloudInformationUseCase(
    private val cloudPreferencesRepository: CloudPreferencesRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<Boolean> =
        combine(
            userRepository.observeUser(),
            cloudPreferencesRepository.observeUrl()
        ) { user, url ->
            user?.hasCredentials() == true && url?.isNotBlank() == true
        }
}