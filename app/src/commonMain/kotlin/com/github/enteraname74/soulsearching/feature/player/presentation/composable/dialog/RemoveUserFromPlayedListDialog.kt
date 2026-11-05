package com.github.enteraname74.soulsearching.feature.player.presentation.composable.dialog

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_person_remove
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.cancel
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.delete
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.shared_list_remove_user_button
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.shared_list_remove_user_text
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.shared_list_remove_user_title

class RemoveUserFromPlayedListDialog(
    private val onRemove: () -> Unit,
    private val onClose: () -> Unit,
): SoulDialog {

    @Composable
    override fun Dialog() {
        SoulAlertDialog(
            confirmAction = onRemove,
            dismissAction = onClose,
            confirmText = CoreRes.string.shared_list_remove_user_button.toTextSpec(),
            dismissText = CoreRes.string.cancel.toTextSpec(),
            title = CoreRes.string.shared_list_remove_user_title.toTextSpec(),
            text = CoreRes.string.shared_list_remove_user_text.toTextSpec(),
            icon = {
                SoulIcon(
                    icon = CoreRes.drawable.ic_person_remove,
                    contentDescription = CoreRes.string.delete.toTextSpec().string,
                )
            }
        )
    }
}
