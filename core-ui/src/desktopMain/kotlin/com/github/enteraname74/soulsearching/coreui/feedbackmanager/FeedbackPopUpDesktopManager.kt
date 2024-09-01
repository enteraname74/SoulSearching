package com.github.enteraname74.soulsearching.coreui.feedbackmanager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FeedbackPopUpDesktopManager: FeedbackPopUpManager {
    private var _state: MutableStateFlow<String?> = MutableStateFlow(null)
    val state: StateFlow<String?> = _state.asStateFlow()

    override suspend fun showFeedback(feedback: String) {
        _state.value = feedback
    }

    fun consumeFeedback() {
        _state.value = null
    }
}