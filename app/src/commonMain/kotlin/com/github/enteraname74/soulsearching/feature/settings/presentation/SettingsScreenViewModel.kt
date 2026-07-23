package com.github.enteraname74.soulsearching.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.domain.usecase.ShouldInformOfNewReleaseUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus

class SettingsScreenViewModel(
    shouldInformOfNewReleaseUseCase: ShouldInformOfNewReleaseUseCase,
    workDispatcher: WorkDispatcher,
): ViewModel() {
    val shouldShowNewVersionPin: StateFlow<Boolean> = shouldInformOfNewReleaseUseCase()
        .stateIn(
            scope = viewModelScope.plus(workDispatcher.dispatcher),
            started = SharingStarted.Eagerly,
            initialValue = false
        )
}
