package com.github.enteraname74.soulsearching.coreui.feedbackmanager

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun FeedbackPopUpScaffold(
    feedbackPopUpManager: FeedbackPopUpManager,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        content()
    }
}