package com.github.enteraname74.soulsearching.feature.settings.aboutpage.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.usecase.release.CommonReleaseUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsAboutViewModel(
    private val commonReleaseUseCase: CommonReleaseUseCase,
): ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<SettingsAboutState> = commonReleaseUseCase.getLatest().mapLatest { latestRelease ->
        SettingsAboutState(
            mostRecentRelease = latestRelease,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsAboutState(
            mostRecentRelease = null,
        )
    )

    init {
        viewModelScope.launch {
            commonReleaseUseCase.fetchLatest()
        }
    }
}