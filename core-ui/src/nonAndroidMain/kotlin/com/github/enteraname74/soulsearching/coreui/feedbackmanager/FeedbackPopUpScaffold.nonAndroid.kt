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

    val manager: FeedbackPopUpNonAndroidManager = feedbackPopUpManager as FeedbackPopUpNonAndroidManager

    val coroutineScope = rememberCoroutineScope()
    var isShowingFeedback: Boolean by remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }

    val state: String? by manager.state.collectAsState()

    state?.let {
        if (isShowingFeedback) return@let
        coroutineScope.launch {
            isShowingFeedback = true
            snackBarHostState.showSnackbar(it)
        }.invokeOnCompletion {
            isShowingFeedback = false
            manager.consumeFeedback()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState
            )
        },
    ) {
        content()
    }
}
