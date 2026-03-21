package com.github.enteraname74.soulsearching.feature.settings.cloud

import com.github.enteraname74.domain.model.User

data class SettingsCloudState(
    val user: User?,
    val hasUrl: Boolean,
)
