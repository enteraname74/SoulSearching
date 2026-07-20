package com.github.enteraname74.soulsearching.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

private class DesktopClipboardController : ClipboardController {

    override fun copy(text: String) {
        val selection = StringSelection(text)
        Toolkit.getDefaultToolkit()
            .systemClipboard
            .setContents(selection, selection)
    }
}

@Composable
actual fun rememberClipboardController(): ClipboardController {
    return remember {
        DesktopClipboardController()
    }
}