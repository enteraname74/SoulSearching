package com.github.enteraname74.soulsearching.feature.settings.aboutpage

import com.github.enteraname74.soulsearching.domain.model.Release

data class SettingsAboutState(
    val mostRecentRelease: Release?,
    val onClickOnVersionName: () -> Unit,
    val navigateBack: () -> Unit,
    val toDevelopers: () -> Unit,
)
