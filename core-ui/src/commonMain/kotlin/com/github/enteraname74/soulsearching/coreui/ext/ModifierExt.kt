package com.github.enteraname74.soulsearching.coreui.ext

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

fun Modifier.clickableIf(enabled: Boolean, block: () -> Unit): Modifier =
    if (enabled) {
        this.clickable {
            block()
        }
    } else {
        this
    }