package com.github.enteraname74.soulsearching.coreui.feedbackmanager

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import kotlinx.coroutines.launch

@Composable
actual fun FeedbackPopUpScaffold(
    feedbackPopUpManager: FeedbackPopUpManager,
    content: @Composable () -> Unit
) {

    val manager: FeedbackPopUpDesktopManager = feedbackPopUpManager as FeedbackPopUpDesktopManager

    val coroutineScope = rememberCoroutineScope()
    var isShowingFeedback: Boolean by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val state: String? by manager.state.collectAsState()

    state?.let {
        if (isShowingFeedback) return@let
        coroutineScope.launch {
            isShowingFeedback = true
            snackbarHostState.showSnackbar(it)
        }.invokeOnCompletion {
            isShowingFeedback = false
            manager.consumeFeedback()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
    ) {
        content()
    }
}