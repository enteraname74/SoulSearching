package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog

class SoulMixDialog(
    private val onDismiss: () -> Unit,
): SoulDialog {
    @Composable
    override fun Dialog() {
        SoulAlertDialog(
            confirmAction = onDismiss,
            dismissAction = onDismiss,
            title = CoreRes.string.soul_mix_info_dialog_title.toTextSpec(),
            text = CoreRes.string.soul_mix_info_dialog_text.toTextSpec(),
            confirmText = CoreRes.string.ok.toTextSpec(),
        )
    }
}
