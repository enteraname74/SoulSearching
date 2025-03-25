package com.github.enteraname74.soulsearching.feature.settings.aboutpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.usecase.release.GetLatestReleaseUseCase
import com.github.enteraname74.domain.usecase.release.SetLatestViewedReleaseUseCase
import com.github.enteraname74.soulsearching.ext.isNewerThanCurrentVersion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

class SettingsAboutViewModel(
    getLatestReleaseUseCase: GetLatestReleaseUseCase,
    private val setLatestViewedReleaseUseCase: SetLatestViewedReleaseUseCase,
): ScreenModel {

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<SettingsAboutState> = getLatestReleaseUseCase().mapLatest { latestRelease ->
        println("RELEASE: $latestRelease")
        SettingsAboutState(
            moreRecentRelease = latestRelease?.takeIf { it.isNewerThanCurrentVersion() }?.also { release ->
                setLatestViewedReleaseUseCase(releaseTag = release.tag)
            }
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsAboutState(
            moreRecentRelease = null,
        )
    )
}