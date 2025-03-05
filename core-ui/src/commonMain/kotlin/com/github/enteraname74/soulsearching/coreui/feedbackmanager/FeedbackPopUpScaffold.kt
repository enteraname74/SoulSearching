package com.github.enteraname74.soulsearching.coreui.feedbackmanager

import androidx.compose.runtime.Composable

@Composable
expect fun FeedbackPopUpScaffold(
    feedbackPopUpManager: FeedbackPopUpManager,
    content: @Composable () -> Unit
)