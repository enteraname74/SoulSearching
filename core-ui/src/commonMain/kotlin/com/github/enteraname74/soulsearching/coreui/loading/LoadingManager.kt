package com.github.enteraname74.soulsearching.coreui.loading

import com.github.enteraname74.domain.util.WorkDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class LoadingManager(
    workDispatcher: WorkDispatcher,
) {
    private val _state: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val state: StateFlow<Boolean> = _state.asStateFlow()

    private val coroutineScope: CoroutineScope = CoroutineScope(workDispatcher.dispatcher)

    private var loadingUiWaitJob: Job? = null

    fun startLoading(
        delayMillis: Long = DEFAULT_UI_DELAY_MILLIS,
    ) {
        loadingUiWaitJob = coroutineScope.launch {
            delay(delayMillis.milliseconds)
            _state.value = true
        }
    }

    fun stopLoading() {
        loadingUiWaitJob?.cancel()
        _state.value = false
    }

    suspend fun withLoading(
        delayMillis: Long = DEFAULT_UI_DELAY_MILLIS,
        block: suspend () -> Unit
    ) {
        startLoading(delayMillis)
        block()
        stopLoading()
    }

    fun withLoadingOnScope(
        scope: CoroutineScope,
        block: suspend () -> Unit
    ) {
        scope.launch {
            withLoading(block = block)
        }
    }

    companion object {
        private const val DEFAULT_UI_DELAY_MILLIS = 300L
    }
}
