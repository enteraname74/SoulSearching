package com.github.enteraname74.soulsearching.util

import android.content.ClipData
import android.content.Context
import android.content.ClipboardManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private class AndroidClipboardController(
    private val context: Context,
) : ClipboardController {

    override fun copy(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied text", text)
        clipboard.setPrimaryClip(clip)
    }
}

@Composable
actual fun rememberClipboardController(): ClipboardController {
    val context = LocalContext.current.applicationContext
    return remember(context) {
        AndroidClipboardController(context)
    }
}
