package com.github.enteraname74.soulsearching.composables.dialog

import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_delete_filled
import com.github.enteraname74.soulsearching.coreui.dialog.SoulAlertDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon

class DeleteAlbumDialog(
    private val onDelete: () -> Unit,
    private val onClose: () -> Unit,
): SoulDialog {

    @Composable
    override fun Dialog() {
        SoulAlertDialog(
            confirmAction = onDelete,
            dismissAction = onClose,
            confirmText = CoreRes.string.delete.toTextSpec(),
            dismissText = CoreRes.string.cancel.toTextSpec(),
            title = CoreRes.string.delete_album_dialog_title.toTextSpec(),
            icon = {
                SoulIcon(
                    icon = CoreRes.drawable.ic_delete_filled,
                    contentDescription = CoreRes.string.delete.toTextSpec().string,
                )
            }
        )
    }
}
