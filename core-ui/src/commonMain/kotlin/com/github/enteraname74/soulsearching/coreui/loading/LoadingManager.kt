package com.github.enteraname74.soulsearching.coreui.loading

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoadingManager {
    private val _state: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val state: StateFlow<Boolean> = _state.asStateFlow()

    fun startLoading() {
        _state.value = true
    }

    fun stopLoading() {
        _state.value = false
    }

    suspend fun withLoading(
        block: suspend () -> Unit
    ) {
        startLoading()
        block()
        stopLoading()
    }
}