package com.github.enteraname74.soulsearching.coreui.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.github.enteraname74.domain.util.PlatformUtils
import kotlinx.browser.window
import org.w3c.dom.events.Event

@Composable
actual fun PlatformUtils.isMouseAndKeyboardOnly(): Boolean {
    val query = "(hover: none) and (pointer: coarse)"
    var isMobile by remember {
        mutableStateOf(!window.matchMedia(query).matches)
    }

    DisposableEffect(Unit) {
        val mediaQuery = window.matchMedia(query)

        val listener: (Event) -> Unit = {
            isMobile = mediaQuery.matches
        }

        mediaQuery.addEventListener("change", listener)

        onDispose {
            mediaQuery.removeEventListener("change", listener)
        }
    }

    return isMobile
}