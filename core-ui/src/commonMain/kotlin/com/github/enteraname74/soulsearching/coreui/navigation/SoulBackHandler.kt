package com.github.enteraname74.soulsearching.coreui.navigation

import androidx.compose.runtime.Composable
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState

@Composable
fun SoulBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit
) {
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = enabled,
        onBackCompleted = onBack
    )
}
