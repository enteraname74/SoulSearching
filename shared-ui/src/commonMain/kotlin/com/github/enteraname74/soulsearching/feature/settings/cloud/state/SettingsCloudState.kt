package com.github.enteraname74.soulsearching.feature.settings.cloud.state

sealed interface SettingsCloudState {
    data object Loading: SettingsCloudState
    data class Data(
        val isCloudActivated: Boolean,
    ): SettingsCloudState
}
