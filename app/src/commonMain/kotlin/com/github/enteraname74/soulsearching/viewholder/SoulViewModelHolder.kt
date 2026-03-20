package com.github.enteraname74.soulsearching.viewholder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

abstract class SoulViewModelHolder<Actions, Navigation, State>(): ViewModel() {
    private var navigation: Navigation? = null
    private val _state = MutableStateFlow(initialState())
    val state: StateFlow<State> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = initialState(),
    )

    protected abstract fun initialState(): State

    protected abstract val actions: Actions

    protected abstract val content: @Composable (Actions, State) -> Unit

    protected fun updateState(updateBlock: State.() -> State) {
        _state.update(updateBlock)
    }

    protected fun navigate(navigateBlock: Navigation.() -> Unit) {
        navigation?.let { navigateBlock(it) }
    }

    @Composable
    fun Screen(
        navigation: Navigation
    ) {
        LaunchInit {
            this@SoulViewModelHolder.navigation = navigation
        }

        val uiState by state.collectAsStateWithLifecycle()
        content(actions, uiState)
    }
}