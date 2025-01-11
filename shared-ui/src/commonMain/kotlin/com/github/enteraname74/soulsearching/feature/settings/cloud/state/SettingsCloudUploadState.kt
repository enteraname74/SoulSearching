package com.github.enteraname74.soulsearching.feature.settings.cloud.state

sealed interface SettingsCloudUploadState {
    val searchMetadata: Boolean

    data class Uploading(
        val progress: Float,
        override val searchMetadata: Boolean,
    ) : SettingsCloudUploadState
    data class Idle(
        override val searchMetadata: Boolean,
    ): SettingsCloudUploadState
    data class Error(
        val error: String?,
        override val searchMetadata: Boolean,
    ): SettingsCloudUploadState
}