package com.github.enteraname74.soulsearching.coreui.loading

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoadingManager {
    private val _state: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val state: StateFlow<Boolean> = _state.asStateFlow()

    private var loadingUiWaitJob: Job? = null

    private fun launchUiLoadingWithDelay(delayMillis: Long) {
        loadingUiWaitJob = CoroutineScope(Dispatchers.IO).launch {
            delay(delayMillis)
            _state.value = true
        }
    }

    fun startLoading(
        delayMillis: Long = DEFAULT_UI_DELAY,
    ) {
        launchUiLoadingWithDelay(delayMillis)
    }

    fun stopLoading() {
        loadingUiWaitJob?.cancel()
        _state.value = false
    }

    suspend fun withLoading(
        delayMillis: Long = DEFAULT_UI_DELAY,
        block: suspend () -> Unit
    ) {
        startLoading(delayMillis)
        block()
        stopLoading()
    }

    companion object {
        private const val DEFAULT_UI_DELAY = 500L
    }
}