package com.github.enteraname74.soulsearching.util

import androidx.compose.runtime.Composable

interface ClipboardController {
    fun copy(text: String)
}

@Composable
expect fun rememberClipboardController(): ClipboardController
