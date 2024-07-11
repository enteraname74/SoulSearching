package com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.usecase.quickaccess.GetAllQuickAccessElementsUseCase
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.QuickAccessState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class AllQuickAccessViewModel(
    getAllQuickAccessElementsUseCase: GetAllQuickAccessElementsUseCase,
) : ScreenModel {
    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<QuickAccessState> = getAllQuickAccessElementsUseCase().flatMapLatest { elements ->
        flowOf(
            QuickAccessState(allQuickAccess = elements)
        )
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        QuickAccessState()
    )
}