package com.github.enteraname74.soulsearching.coreui.utils

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.CoroutineScope

@Composable
fun LaunchInit(
    block: suspend CoroutineScope.() -> Unit,
) {
    var hasBeenInit by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(hasBeenInit) {
        if (!hasBeenInit) {
            block()
            hasBeenInit = true
        }
    }
}