package com.github.enteraname74.soulsearching.coreui.feedbackmanager

import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FeedbackPopUpNonAndroidManager : FeedbackPopUpManager {
    private var _state: MutableStateFlow<String?> = MutableStateFlow(null)
    val state: StateFlow<String?> = _state.asStateFlow()

    override suspend fun showFeedback(feedback: SoulTextSpec) {
        _state.value = feedback.suspendedString()
    }

    fun consumeFeedback() {
        _state.value = null
    }
}
