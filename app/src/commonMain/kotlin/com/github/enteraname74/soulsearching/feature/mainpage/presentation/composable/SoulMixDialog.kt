package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.strings.strings

class SoulMixDialog(
    private val onDismiss: () -> Unit,
): SoulDialog {
    @Composable
    override fun Dialog() {
        SoulAlertDialog(
            confirmAction = onDismiss,
            dismissAction = onDismiss,
            title = strings.soulMixInfoDialogTitle,
            text = strings.soulMixInfoDialogText,
            confirmText = strings.ok,
        )
    }
}