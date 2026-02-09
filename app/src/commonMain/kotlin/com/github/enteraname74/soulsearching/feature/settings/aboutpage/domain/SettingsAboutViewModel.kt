package com.github.enteraname74.soulsearching.feature.settings.aboutpage.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.usecase.release.CommonReleaseUseCase
import com.github.enteraname74.soulsearching.ext.isNewerThanCurrentVersion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

class SettingsAboutViewModel(
    private val commonReleaseUseCase: CommonReleaseUseCase,
): ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<SettingsAboutState> = commonReleaseUseCase.getLatest().mapLatest { latestRelease ->
        SettingsAboutState(
            moreRecentRelease = latestRelease?.takeIf { it.isNewerThanCurrentVersion() }?.also { release ->
                commonReleaseUseCase.setLatestViewedReleaseTag(releaseTag = release.tag)
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsAboutState(
            moreRecentRelease = null,
        )
    )
}