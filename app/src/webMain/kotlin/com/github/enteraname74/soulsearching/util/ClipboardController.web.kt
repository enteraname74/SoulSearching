package com.github.enteraname74.soulsearching.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

private class WebClipboardController : ClipboardController {
    override fun copy(text: String) {
        // TODO WEB: use navigator.clipboard once browser interop is finalized.
    }
}

@Composable
actual fun rememberClipboardController(): ClipboardController =
    remember { WebClipboardController() }
