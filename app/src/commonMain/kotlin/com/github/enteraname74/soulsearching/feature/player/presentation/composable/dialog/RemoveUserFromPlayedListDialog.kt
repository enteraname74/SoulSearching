package com.github.enteraname74.soulsearching.feature.player.presentation.composable.dialog

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_person_remove
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.strings.strings

class RemoveUserFromPlayedListDialog(
    private val onRemove: () -> Unit,
    private val onClose: () -> Unit,
): SoulDialog {

    @Composable
    override fun Dialog() {
        SoulAlertDialog(
            confirmAction = onRemove,
            dismissAction = onClose,
            confirmText = strings.sharedListRemoveUserButton,
            dismissText = strings.cancel,
            title = strings.sharedListRemoveUserTitle,
            text = strings.sharedListRemoveUserText,
            icon = {
                SoulIcon(
                    icon = CoreRes.drawable.ic_person_remove,
                    contentDescription = strings.delete,
                )
            }
        )
    }
}
