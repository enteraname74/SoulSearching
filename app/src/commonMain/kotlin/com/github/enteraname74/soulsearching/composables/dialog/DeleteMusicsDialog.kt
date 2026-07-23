package com.github.enteraname74.soulsearching.composables.dialog

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_delete_filled
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.strings.strings

class DeleteMusicsDialog(
    private val onDelete: () -> Unit,
    private val onClose: () -> Unit,
    private val title: String,
    private val text: String,
) : SoulDialog {

    @Composable
    override fun Dialog() {
        SoulAlertDialog(
            title = title,
            text = text,
            confirmAction = onDelete,
            dismissAction = onClose,
            confirmText = strings.delete,
            dismissText = strings.cancel,
            icon = {
                SoulIcon(
                    icon = CoreRes.drawable.ic_delete_filled,
                    contentDescription = strings.delete,
                )
            }
        )
    }
}
