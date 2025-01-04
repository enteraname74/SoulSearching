package com.github.enteraname74.soulsearching.feature.settings.aboutpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Release
import com.github.enteraname74.domain.usecase.release.GetLatestReleaseUseCase
import com.github.enteraname74.soulsearching.domain.AppVersion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsAboutViewModel(
    private val getLatestReleaseUseCase: GetLatestReleaseUseCase,
): ScreenModel {
    private val latestReleaseState: MutableStateFlow<Release?> = MutableStateFlow(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<SettingsAboutState> = latestReleaseState.mapLatest { latestRelease ->
        SettingsAboutState(
            moreRecentRelease = latestRelease?.takeIf { isNewerThanCurrentVersion(it) }
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsAboutState(
            moreRecentRelease = null,
        )
    )

    fun fetchLatestRelease() {
        CoroutineScope(Dispatchers.IO).launch {
            latestReleaseState.value = getLatestReleaseUseCase()
        }
    }

    private fun isNewerThanCurrentVersion(release: Release): Boolean {
        val currentVersion = AppVersion.versionName.split("-").first()
        val cleanedReleaseVersion = release.tag.replace("v", "")

        return currentVersion != cleanedReleaseVersion
    }
}