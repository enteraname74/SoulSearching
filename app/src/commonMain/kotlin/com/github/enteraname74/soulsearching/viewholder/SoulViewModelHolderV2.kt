package com.github.enteraname74.soulsearching.viewholder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

abstract class SoulViewModelHolderV2<Navigation, State> : ViewModel() {
    private val _state: MutableStateFlow<State> by lazy {
        MutableStateFlow(getInitialState())
    }
    private val state: StateFlow<State> by lazy {
        _state.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = getInitialState(),
        )
    }
    protected val currentState: State
        get() = _state.value

    private var _navigationState: MutableStateFlow<(Navigation.() -> Unit)?> =
        MutableStateFlow(null)
    private val navigationState: StateFlow<(Navigation.() -> Unit)?> = _navigationState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null,
    )

    protected abstract fun getInitialState(): State

    @Composable
    protected abstract fun Content(
        state: State,
    )

    // TODO VIEW HOLDER 2.0: Could take the copy function directly
    protected fun updateState(updateBlock: State.() -> State) {
        _state.update(updateBlock)
    }

    protected fun navigate(navigateBlock: Navigation.() -> Unit) {
        _navigationState.value = navigateBlock
    }

    private fun consumeNavigation() {
        _navigationState.value = null
    }

    @Composable
    fun Screen(
        navigation: Navigation
    ) {
        val uiState by state.collectAsStateWithLifecycle()
        val uiNavigationState by navigationState.collectAsStateWithLifecycle()

        LaunchedEffect(uiNavigationState) {
            uiNavigationState?.let { navigation.it() }
            consumeNavigation()
        }

        Content(uiState)
    }
}