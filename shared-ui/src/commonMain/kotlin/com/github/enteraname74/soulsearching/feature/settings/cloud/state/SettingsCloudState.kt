package com.github.enteraname74.soulsearching.feature.settings.cloud.state

import com.github.enteraname74.domain.model.User

sealed interface SettingsCloudState {
    data object Loading: SettingsCloudState
    data class Data(
        val isCloudActivated: Boolean,
        val user: User?,
    ): SettingsCloudState
}
