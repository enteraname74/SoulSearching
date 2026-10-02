package com.github.enteraname74.soulsearching.coreui.keyboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged

@Stable
class GlobalKeyboardShortcutState {
    private val blockingFocusOwners = mutableStateMapOf<Any, Unit>()

    val isBlocked: Boolean
        get() = blockingFocusOwners.isNotEmpty()

    fun setBlocked(owner: Any, blocked: Boolean) {
        if (blocked) {
            blockingFocusOwners[owner] = Unit
        } else {
            blockingFocusOwners.remove(owner)
        }
    }
}

val LocalGlobalKeyboardShortcutState = staticCompositionLocalOf<GlobalKeyboardShortcutState?> {
    null
}

@Composable
fun Modifier.blockGlobalKeyboardShortcutsWhileFocused(): Modifier {
    val shortcutState = LocalGlobalKeyboardShortcutState.current
    val focusOwner = remember { Any() }

    DisposableEffect(shortcutState, focusOwner) {
        onDispose {
            shortcutState?.setBlocked(owner = focusOwner, blocked = false)
        }
    }

    return if (shortcutState == null) {
        this
    } else {
        onFocusChanged { focusState ->
            shortcutState.setBlocked(
                owner = focusOwner,
                blocked = focusState.isFocused,
            )
        }
    }
}
